import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio
import com.twilio.`type`.PhoneNumber

case class GPSData(user_id: Long, latitude: Double, longitude: Double, timestamp: String)

object spark_streaming {
  // Twilio credentials
  val ACCOUNT_SID = "ACe3696a2074bbc69159407b205fe6a170"
  val AUTH_TOKEN = "553ae32e9f94a42a2dc475dfdafc9839"
  val FROM_PHONE_NUMBER = "+12072927257" // Twilio phone number

  Twilio.init(ACCOUNT_SID, AUTH_TOKEN)

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
    val filteredDF = gpsDF.filter($"latitude" >= 0 && $"latitude" <= 90 && $"longitude" >= 0 && $"longitude" <= 180)

    // Function to send an SMS
    def sendSMS(to: String, body: String): Unit = {
      try {
        val message = Message.creator(
          new PhoneNumber(to),
          new PhoneNumber(FROM_PHONE_NUMBER),
          body
        ).create()

        println(s"SMS sent successfully: ${message.getSid}")
      } catch {
        case e: Exception => e.printStackTrace()
      }
    }

    // Process each row to send an SMS
    val query = filteredDF.writeStream
      .outputMode("append")
      .foreachBatch { (batchDF: DataFrame, batchID: Long) =>
        batchDF.collect().foreach { row =>
          val userId = row.getAs[String]("user_id")
          val body = s"User $userId is within the specified latitude and longitude range."
          sendSMS("+33788133903", body) // Send SMS to the specified phone number
        }
      }
      .start()

    query.awaitTermination()
  }
}
