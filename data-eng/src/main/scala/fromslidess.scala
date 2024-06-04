
import org.apache.spark.{SparkConf, SparkContext}

object fromslidess extends App{
  val conf = new SparkConf()
    .setMaster("local")
    .setAppName("trees")
    .set("spark.executor.memory", "2g")
  val sc = new SparkContext(conf)
  val memList = sc.parallelize(List("trees", "flowers"))
  // Load data from local text file
  val lines = sc.textFile("trees/paris2010.csv")
  // Load data from HDFS text file
  val rdd =
    sc.textFile("hdfs://master:50070/trees/paris2010.csv")

}
