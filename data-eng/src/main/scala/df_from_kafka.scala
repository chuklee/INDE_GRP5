import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}
import org.apache.spark.sql.functions._

object df_from_kafka {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Kafka Spark Consumer")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("ERROR")

    // Define schema for CSV data
    val schema = new StructType()
      .add("user_id", IntegerType)
      .add("user_name", StringType)
      .add("user_city", StringType)

    // Read data from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "my_topic")
      .option("startingOffsets", "earliest")
      .option("failOnDataLoss", "false")
      .option("auto.offset.reset", "earliest")
      .load()

    // Convert Kafka messages to String
    val stringDF = kafkaDF.selectExpr("CAST(value AS STRING)").as[String]

    // Filter out the header row
    val nonHeaderDF = stringDF.filter(row => !row.startsWith("user_id"))

    // Split the CSV data and convert to DataFrame
    val processedDF = nonHeaderDF.map { row =>
      val fields = row.split(",")
      (fields(0).toInt, fields(1), fields(2))
    }.toDF("user_id", "user_name", "user_city")

    // Filter the DataFrame for users from a specific city, e.g., "Montreal"
    val filteredDF = processedDF.filter("user_city == 'Montreal'")

    // Write the processed data to a CSV file
    val query = filteredDF.writeStream
      .outputMode("append")
      .format("csv")
      .option("checkpointLocation", "src/main/scala/checkpoint")
      .option("path", "src/main/scala/output")
      .start()

    query.awaitTermination()
  }
}
