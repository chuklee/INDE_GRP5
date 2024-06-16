import org.apache.spark.sql.SparkSession



object test_read_db {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("on sen fou")
      .master("local[*]")
      .getOrCreate()

    val jbdcDF = spark
      .read.format("jdbc")
      .option("url", "jdbc:postgresql://localhost/postgres")
      .option("dbtable", "alerte")
      .option("user", "postgres")
      .option("password", "1234")
      .load()

    jbdcDF.show()
  }
}