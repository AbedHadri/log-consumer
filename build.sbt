name := "log-consumer"

version := "0.0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  val akkaV = "2.5.19"
  val alpAkkaV = "0.21.1"
  val sprayJsonV = "1.3.5"
  val logbackV = "1.2.3"
  val scalaLoggingV = "3.9.0"
  val scalaTestV = "3.0.5"
  val pureConfigV="0.10.1"
  val akkaHttpV = "10.1.6"
  val catsV = "1.6.0"

  Seq(
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-stream-kafka" % alpAkkaV,
    "io.spray" %%  "spray-json" % sprayJsonV,
    "com.github.pureconfig" %% "pureconfig" % pureConfigV,
    "ch.qos.logback" % "logback-classic" % logbackV,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.typelevel" %% "cats-core" % catsV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
  )
}

mainClass in (Compile, run) := Some("com.abedhadri.LogConsumer")
