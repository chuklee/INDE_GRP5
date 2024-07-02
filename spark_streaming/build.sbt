ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "spark_streaming"
  )


libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1"

// To use dataframe
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.1"

// To read from Kafka
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1"


// To write to postgres database
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3"


javaOptions ++= Seq(
  "-Xms512M",
  "-Xmx1024M",
  "-Xss1M",
  "-XX:+CMSClassUnloadingEnabled",
  "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED"
)
