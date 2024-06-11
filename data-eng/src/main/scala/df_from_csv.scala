import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType  , StringType, StructField, StructType}

object df_from_csv {
  def main(args : Array[String]) : Unit = {
    println("Hello, world!")



    val spark = SparkSession
                  .builder()
                  .appName("Spark Dataframe From csv")
                  .master("local[*]")
                  .config("spark.driver.extraJavaOptions", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
                  .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val path_csv_file = "src/main/scala/myFile1000samples.csv"
    val user_dataframe = spark.read
                            .option("header", "true")
                            .option("inferSchema", "true")
                            .csv(path_csv_file)

    //user_dataframe.show()
    user_dataframe.printSchema()

    // Transformation: Filter the DataFrame for users from a specific city, e.g., "New York"
    val filtered_df = user_dataframe.filter("user_city == 'Montreal'")

    // Write the content of the DataFrame to a new CSV file
    val path_csv_file_filtered = "src/main/scala/myFile1000samples_filtered.csv"
    filtered_df.write
                .option("header", "true")
                .csv(path_csv_file_filtered)
  }
}
