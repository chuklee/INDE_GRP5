import org.apache.spark.{SparkConf, SparkContext}
import scala.util.Random

// Définition de la classe case pour les données GPS
case class GPSData(latitude: Double, longitude: Double, timestamp: Long, userId: String)

// Objet pour générer des données GPS aléatoires
object GPSDataGenerator extends App {
  def generateRandomGPSData(n: Int): List[GPSData] = {
    val rand = new Random()
    val users = List("user1", "user2", "user3", "user4", "user5")

    (1 to n).map { _ =>
      GPSData(
        latitude = rand.nextDouble() * 180 - 90,  // Latitude entre -90 et 90
        longitude = rand.nextDouble() * 360 - 180,  // Longitude entre -180 et 180
        timestamp = rand.nextInt(1000000),
        userId = users(rand.nextInt(users.length))
      )
    }.toList
  }
  val conf = new SparkConf()
    .setMaster("local")
    .setAppName("GPSDataApp")
    .set("spark.executor.memory", "2g")

  val sc = new SparkContext(conf)
  val rdd = sc.parallelize(0 to 10)
  rdd.collect()
  def f(x:Int) = x*2
  rdd.map(f).collect()

}

