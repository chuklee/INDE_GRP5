ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "data-eng"
  )

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1"

// To use dataframe
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.1"

// To read from Kafka
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1"

//To send mail
libraryDependencies += "javax.mail" % "mail" % "1.4.7"

// To send SMS
libraryDependencies += "com.twilio.sdk" % "twilio" % "7.15.5"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
