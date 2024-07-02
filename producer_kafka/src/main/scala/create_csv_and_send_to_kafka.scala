import java.io._
import java.time._
import java.time.format.DateTimeFormatter
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import scala.util.{Random, Try, Success, Failure}
import org.apache.kafka.clients.producer._
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._

object FunctionalKafkaProducer {
  // File paths
  val IdFilePath = "src/main/scala/last_id.txt"
  val CsvFilePath = "src/main/scala/myFile100000samples.csv"

  // Kafka configuration
  val KafkaProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

  // Pure functions
  def readLastId(path: String): Try[Int] =
    Try(scala.io.Source.fromFile(path).getLines().next().toInt).recoverWith {
      case _: java.io.FileNotFoundException => Success(0)
    }

  def writeLastId(path: String, id: Int): Try[Unit] =
    Try {
      val writer = new PrintWriter(new File(path))
      writer.write(id.toString)
      writer.close()
    }

  def generateSample(id: Int, currentTime: LocalDateTime): String = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val random = new Random()
    val timestamp = currentTime.format(formatter)
    val age = 18 + random.nextInt(63)
    val streetNumber = random.nextInt(1001)
    val jobNumber = random.nextInt(100)
    s"$id,Name$id,Surname$id,email$id@domain.com,job$jobNumber,street$streetNumber,$timestamp,$age"
  }

  def generateSamplesStream(startId: Int, count: Int): LazyList[String] =
    LazyList.from(startId).take(count).map(id => generateSample(id, LocalDateTime.now()))

  def writeToCsv(path: String, header: String, samples: LazyList[String]): Try[Unit] =
    Try {
      val writer = new PrintWriter(new File(path))
      writer.write(s"$header\n")
      samples.foreach(sample => writer.write(s"$sample\n"))
      writer.close()
    }

  def sendToKafka(producer: KafkaProducer[String, String], topic: String, key: String, value: String): Future[RecordMetadata] = {
    val promise = concurrent.Promise[RecordMetadata]()
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception == null) promise.success(metadata) else promise.failure(exception)
      }
    })
    promise.future
  }

  def processAndSend(producer: KafkaProducer[String, String], samples: LazyList[String])(implicit ec: ExecutionContext): Future[Unit] =
    Future.sequence(
      samples.map { sample =>
        val id = sample.split(",")(0)
        sendToKafka(producer, "report", id, sample).map { metadata =>
          println(s"Message for ID $id sent successfully to topic ${metadata.topic()}")
          println(s"Partition: ${metadata.partition()}, Offset: ${metadata.offset()}")
          println(s"Timestamp: ${metadata.timestamp()}")
        }
      }
    ).map(_ => ())

  // Main execution
  def run()(implicit ec: ExecutionContext): Future[Unit] = {
    val producer = new KafkaProducer[String, String](KafkaProps)

    def process(): Future[Unit] = for {
      lastId <- Future.fromTry(readLastId(IdFilePath))
      samples = generateSamplesStream(lastId + 1, 100000)
      _ <- Future.fromTry(writeToCsv(CsvFilePath, "id,Name,Surname,email,job,street,timestamp,age", samples))
      _ <- processAndSend(producer, samples)
      newLastId = lastId + 100000
      _ <- Future.fromTry(writeLastId(IdFilePath, newLastId))
    } yield ()

    val scheduler = Executors.newScheduledThreadPool(1)
    scheduler.scheduleAtFixedRate(() => process(), 0, 1, TimeUnit.MINUTES)

    sys.addShutdownHook {
      producer.close()
      scheduler.shutdown()
    }

    Future.never // Keep the program running
  }

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    run()
    Thread.currentThread().join() // Keep the main thread alive
  }
}