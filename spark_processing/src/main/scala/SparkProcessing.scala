import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object SparkProcessing {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Analyse des rues interdites et des âges")
      .master("local[*]")
      .config("spark.driver.extraJavaOptions", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
      .getOrCreate()

    // Lire les zones interdites depuis PostgreSQL
    val forbiddenAreasDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:postgresql://172.28.85.10:5432/postgres")
      .option("dbtable", "forbidden_areas")
      .option("user", "postgres")
      .option("password", "abc")
      .load()

    // Broadcast du DataFrame des zones interdites
    val broadcastForbiddenAreas = broadcast(forbiddenAreasDF)

    // Lire les données depuis les fichiers CSV
    val repertoireCsv = "../spark_output/batch_output"
    val df = spark.read.option("header", "true").csv(s"$repertoireCsv/*.csv")

    // Renommer les colonnes pour éviter les problèmes de caractères spéciaux
    val dfRenamed = df.withColumnRenamed(df.columns(4), "job")
      .withColumnRenamed(df.columns(5), "rue")
      .withColumnRenamed(df.columns(6), "date")
      .withColumnRenamed(df.columns(7), "age")

    val dfSelected = dfRenamed.select(
      col("job"),
      col("rue"),
      col("date").cast("timestamp"),
      col("age").cast("int")
    )


    // Joindre les données CSV avec les zones interdites
    val dfInterdites = dfSelected.join(broadcastForbiddenAreas, dfSelected("rue") === broadcastForbiddenAreas("area"))

    // Compter les rues interdites
    val compteurRues = dfInterdites.groupBy("rue").count()
    compteurRues.write.option("header", "true").csv("../input_python/compteur_rues")

    // Compter les âges en intervalles de 10 ans
    val dfAges = dfInterdites.withColumn("intervalle_age", (col("age") / 10).cast("int") * 10)
    val compteurAges = dfAges.groupBy("intervalle_age").count()
    compteurAges.write.option("header", "true").csv("../input_python/compteur_ages")

    // Compter les métiers
    val compteurJobs = dfInterdites.groupBy("job").count()
    compteurJobs.write.option("header", "true").csv("../input_python/compteur_jobs")
    // Compter les présences par heure
    val dfHeures = dfInterdites.withColumn("heure", hour(col("date")))
    val compteurHeures = dfHeures.groupBy("heure").count()
    compteurHeures.write.option("header", "true").csv("../input_python/compteur_heures")
    spark.stop()
  }
}