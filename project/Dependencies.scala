import sbt._

object Dependencies {
  val AkkaVersion = "2.6.17"
  lazy val akkaHttpVersion = "10.2.7"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
  lazy val akkaCluster = "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion
  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion
  lazy val logBack = "ch.qos.logback" % "logback-classic" % "1.2.7"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
}
