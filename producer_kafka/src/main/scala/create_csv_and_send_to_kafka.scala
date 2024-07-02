import java.io._
import java.time._
import java.time.format.DateTimeFormatter
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import scala.util.Random
import org.apache.kafka.clients.producer._

object create_csv_and_send_to_kafka {
  // File to store the last known ID
  val idFile = new File("src/main/scala/last_id.txt")
  val csvFile = "src/main/scala/myFile100000samples.csv"

  // Kafka configuration
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)

  def readLastId(): Int = {
    if (idFile.exists()) {
      scala.io.Source.fromFile(idFile).getLines().next().toInt
    } else {
      0
    }
  }

  def writeLastId(id: Int): Unit = {
    val writer = new PrintWriter(idFile)
    writer.write(id.toString)
    writer.close()
  }

  def generateSampleAndSendToKafka(startId: Int): Int = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val currentTime = LocalDateTime.now()
    val random = new Random()

    def generateSample(id: Int): String = {
      val timestamp = currentTime.plusSeconds(id - startId).format(formatter)
      val age = 18 + random.nextInt(63) // Generate random age between 18 and 80
      val streetNumber = random.nextInt(1001) // Generate random street number between 0 and 1000
      val jobNumber = random.nextInt(100)
      s"$id,Name$id,Surname$id,email$id@domain.com,job$jobNumber,street$streetNumber,$timestamp,$age"
    }

    val header = "id,Name,Surname,email,job,street,timestamp,age"

    // Write header to CSV file
    val writer = new PrintWriter(new File(csvFile))
    writer.write(s"$header\n")

    // Generate samples and send to Kafka row by row
    for (id <- startId until startId + 100000) {
      val sample = generateSample(id)

      // Write to CSV file
      writer.write(s"$sample\n")

      // Send to Kafka
      val record = new ProducerRecord[String, String]("report", id.toString, sample)
      producer.send(record, new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          if (exception == null) {
            println(s"Message for ID $id sent successfully to topic ${metadata.topic()}")
            println(s"Partition: ${metadata.partition()}, Offset: ${metadata.offset()}")
            println(s"Timestamp: ${metadata.timestamp()}")
          } else {
            println(s"Failed to send message for ID $id: ${exception.getMessage}")
          }
        }
      })
    }

    writer.close()
    startId + 100000
  }

  def main(args: Array[String]): Unit = {
    val scheduler = Executors.newScheduledThreadPool(1)
    val task = new Runnable {
      def run(): Unit = {
        val lastId = readLastId()
        val newLastId = generateSampleAndSendToKafka(lastId + 1)
        writeLastId(newLastId)
      }
    }
    scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES)

    // Add a shutdown hook to close the producer
    sys.addShutdownHook {
      producer.close()
      scheduler.shutdown()
    }
  }
}