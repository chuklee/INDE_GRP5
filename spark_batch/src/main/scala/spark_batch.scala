import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{DateType, DoubleType, IntegerType, LongType, StringType, StructType, TimestampType}

object spark_batch {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Kafka Spark Report")
      .master("local[*]") // Utiliser 'local[*]' en développement, changer en production
      .getOrCreate()

    // Configuration pour lire les données de Kafka
    val kafkaDF = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092") // Remplacer par votre serveur Kafka
      .option("subscribe", "my_topic") // Remplacer par votre nom de topic
      .option("startingOffsets", "earliest") // Lire depuis le début du topic
      .option("failOnDataLoss", "false")
      .option("auto.offset.reset", "earliest")
      .load()

    // Transformation des données Kafka (présumées en CSV) en DataFrame
    import spark.implicits._
    val chipData = kafkaDF.selectExpr("CAST(value AS STRING)").as[String]

    // Filter out header rows (assuming the header starts with specific keywords)
    val dataframe_without_header = chipData
      .filter(row => {
        val rowString = row.toString
        !rowString.startsWith("id") &&
          !rowString.startsWith("user_id") &&
          !rowString.startsWith("latitude") &&
          !rowString.startsWith("longitude") &&
          !rowString.startsWith("timestamp")
      })

    // Parse CSV rows into columns
    // example 3,Netty,Wolfgram,Netty.Wolfgram@yopmail.com,developer,90,4.54,26-07-2024
    val converted_and_parsedDF = dataframe_without_header
      .map { row =>
        val fields = row.toString().split(",")
        (fields(0).toInt, fields(1).toString, fields(2).toString, fields(3).toString, fields(4).toString, fields(5).toDouble, fields(6).toDouble , fields(7))
      }
      .toDF("user_id", "first_name", "last_name", "email", "job", "latitude", "longitude", "timestamp")

    // Convert timestamp from String to TimestampType
    val dataWithTimestamp = converted_and_parsedDF
      .withColumn("timestamp", to_timestamp(col("timestamp"), "dd-MM-yyyy"))

    // Aggregation: Example of counting by user ID
    val aggregatedData = dataWithTimestamp
      .withWatermark("timestamp", "1 week")
      .groupBy(window(col("timestamp"), "1 week"), col("user_id"))
      .count()
      .select(
        col("window.start").as("window_start"),
        col("window.end").as("window_end"),
        col("user_id"),
        col("count")
      )

    // Define the output directory for weekly reports
    // Define the output directory for weekly reports
    val query = aggregatedData
      .writeStream
      .outputMode("append") // Utiliser 'append' ou 'complete' selon votre cas d'utilisation
      .format("csv") // Utiliser 'parquet', 'json', ou autre format de fichier
      .option("path", "/src/main/scala/batch_output") // Remplacer par le chemin de votre répertoire de sortie
      .option("checkpointLocation", "/src/main/scala/batch_checkpoint") // Chemin pour la sauvegarde de point de contrôle
      .trigger(Trigger.ProcessingTime("60 seconds"))
      .start()

    // Run the streaming query for a specified duration, then stop
    // Run for 130 seconds
    //query.awaitTermination(130000)
    query.awaitTermination()

    // Stop the query gracefully after the specified duration
    //query.stop()
  }
}
