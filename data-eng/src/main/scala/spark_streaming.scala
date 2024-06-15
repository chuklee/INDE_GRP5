import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object spark_streaming {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Kafka Spark Consumer")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("ERROR")

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
    val nonHeaderDF = stringDF.filter(row => !row.startsWith("latitude"))

    // Split the CSV data and convert to DataFrame with GPSData case class
    val gpsDF = nonHeaderDF.map { row =>
      val fields = row.toString().split(",")
      (fields(0).toString, fields(1).toString, fields(2).toString, fields(3).toString, fields(4).toString, fields(5).toDouble, fields(6).toDouble, fields(7))
    }.toDF("user_id", "first_name", "last_name", "email", "job", "latitude", "longitude", "timestamp")

    // Filter the DataFrame for users within the specified latitude and longitude range
    //val filteredDF = gpsDF.filter($"latitude" >= 0 && $"latitude" <= 90 && $"longitude" >= 0 && $"longitude" <= 180)
    val filteredDF = gpsDF.filter($"longitude" >= 0)
    // || $"longitude" <= 180)
    // || $"latitude" >= 0

    val query = filteredDF.writeStream
      .outputMode("append")
      .format("csv")
      .option("checkpointLocation", "src/main/scala/checkpoint")
      .option("path", "src/main/scala/output")
      .start()


    query.awaitTermination()
  }
}
