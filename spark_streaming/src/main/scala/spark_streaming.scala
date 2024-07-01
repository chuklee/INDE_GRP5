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
    // Read forbidden areas from PostgreSQL
    val forbiddenAreasDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:postgresql://172.28.85.10:5432/postgres")
      .option("dbtable", "forbidden_areas")
      .option("user", "postgres")
      .option("password", "abc")
      .load()

    // Broadcast the forbidden areas DataFrame
    val broadcastForbiddenAreas = broadcast(forbiddenAreasDF)

    // Read data from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "172.17.124.50:9092") // Remplacer par hostname -I sur WSL 172.17.124.50
      .option("subscribe", "my_topic")
      .option("startingOffsets", "earliest")
      .option("failOnDataLoss", "false")
      .option("auto.offset.reset", "earliest")
      .load()

    // Convert Kafka messages to String
    val stringDF = kafkaDF.selectExpr("CAST(value AS STRING)").as[String]

    // Filter out the header row
    val nonHeaderDF = stringDF.filter(row => !row.startsWith("id"))

    /*val nonHeaderDF = stringDF.filter(row =>
        !row.startsWith("user_id")
          && !row.startsWith("id"))
          && !row.startsWith("firstname")
          && !row.startsWith("location"))*/

    // Split the CSV data and convert to DataFrame with schema
    // Sample of one row : "13,Collen,Gower,Collen.Gower@yopmail.com,firefighter, champs élysées ,15-07-2024, 02:07:00"
    val gpsDF = nonHeaderDF.map { row =>
      val fields = row.split(",")
      (
        fields(0).toInt, // user_id
        fields(1), // first_name
        fields(2), // last_name
        fields(3), // email
        fields(4), // job
        fields(5), // location ex : 'Paris'
        fields(6), // date
        fields(7).toInt // age
      )
    }.toDF("user_id", "firstname", "lastname", "email", "job", "location", "date", "age")

    // Convert the date column to timestamp
    val formattedDF = gpsDF.withColumn("date", to_timestamp(col("date"), "yyyy-MM-dd HH:mm:ss"))
    // Transformation: Filter the DataFrame for users from a specific city, e.g., "Paris"
    // Join with forbidden areas to filter the DataFrame
    val filteredDF = formattedDF.join(broadcastForbiddenAreas, formattedDF("location") === broadcastForbiddenAreas("area"), "inner")
      .select("user_id", "firstname", "lastname", "email", "job", "location", "date", "age")

    // Function to write to PostgreSQL
    def writeToPostgres(df: DataFrame, batchId: Long): Unit = {
      df.write
        .format("jdbc")
        .option("url", "jdbc:postgresql://172.28.85.10:5432/postgres")
        .option("dbtable", "alerte_utilisateur")
        .option("user", "postgres")
        .option("password", "abc")
        .mode("append")
        .save()
    }

    // Write the data to PostgreSQL using foreachBatch
    val postgresQuery = filteredDF
      .writeStream
      .outputMode("append")
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        /* FOR DEBUG PURPOSES ONLY  : */
        // println(s"Batch ID: $batchId")
        // batchDF.show(false)
        /* FOR DEBUG PURPOSES ONLY */
        writeToPostgres(batchDF, batchId)
      }
      //.option("checkpointLocation", "src/main/scala/checkpoint_postgres")
      .start()

    postgresQuery.awaitTermination()
  }
}
