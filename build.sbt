import Dependencies._

ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "Akka Cluster Distributed Data",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      akkaActor,
      akkaStreams,
      akkaCluster,
      logBack,
      akkaSlf4j,
      akkaHttp
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
