import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object spark_streaming {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Kafka Spark Consumer")
      .master("local[*]")
      .config("spark.driver.extraJavaOptions", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("ERROR")

    // Read data from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "172.17.124.50:9092") // Remplacer par hostname -I sur WSL
      .option("subscribe", "my_topic")
      .option("startingOffsets", "earliest")
      .option("failOnDataLoss", "false")
      .option("auto.offset.reset", "earliest")
      .load()

    // Convert Kafka messages to String
    val stringDF = kafkaDF.selectExpr("CAST(value AS STRING)").as[String]

    // Filter out the header row
    val nonHeaderDF = stringDF.filter(row =>
        !row.startsWith("user_id"))
          /*|| !row.startsWith("user_id")
          || !row.startsWith("firstname")
          || !row.startsWith("location"))*/

    // Split the CSV data and convert to DataFrame with schema
    val gpsDF = nonHeaderDF.map { row =>
      val fields = row.split(",")
      (
        fields(0).toInt, // user_id
        fields(1), // first_name
        fields(2) // location ex : 'Paris'
      )
    }.toDF("user_id", "firstname","location")

    // Transformation: Filter the DataFrame for users from a specific city, e.g., "Paris"
    val filteredDF = gpsDF.filter("location == 'Paris'")

    // Function to write to PostgreSQL
    def writeToPostgres(df: DataFrame, batchId: Long): Unit = {
      df.write
        .format("jdbc")
        .option("url", "jdbc:postgresql://localhost:5432/postgres")
        .option("dbtable", "location")
        .option("user", "postgres")
        .option("password", "1234")
        .mode("append")
        .save()
    }

    // Write the data to PostgreSQL using foreachBatch
    val postgresQuery = filteredDF.writeStream
      .outputMode("append")
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        println(s"Batch ID: $batchId")
        batchDF.show(false)  // Show complete content for debugging
        writeToPostgres(batchDF, batchId)
      }
      .option("checkpointLocation", "src/main/scala/checkpoint_postgres")
      .start()

    postgresQuery.awaitTermination()
  }
}
