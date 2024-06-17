import java.io._
import java.time._
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

object create_csv {
  // File to store the last known ID
  val idFile = new File("src/main/scala/last_id.txt")
  val csvFile = "src/main/scala/myFile100000samples.csv"

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

  def generateCsv(startId: Int): Int = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val currentTime = LocalDateTime.now()

    def generateSample(id: Int): String = {
      val timestamp = currentTime.plusSeconds(id - startId).format(formatter)
      s"$id,Name$id,Surname$id,email$id@domain.com,job$id,street$id,$timestamp\n"
    }

    val header = "id,Name,Surname,email,job,street,timestamp"
    val samples = (startId until startId + 100000).map(generateSample).toList.par

    val writer = new PrintWriter(new File(csvFile))
    writer.write(s"$header\n")
    samples.foreach(writer.write)
    writer.close()

    startId + 100000
  }

  def main(args: Array[String]): Unit = {
    val scheduler = Executors.newScheduledThreadPool(1)

    val task = new Runnable {
      def run(): Unit = {
        val lastId = readLastId()
        val newLastId = generateCsv(lastId + 1)
        writeLastId(newLastId)
      }
    }

    scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES)
  }
}
