
organization:= "org.sjsclient"

name := "SJSClient"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.squareup.okhttp" % "okhttp" % "2.7.5",
  "org.json4s" % "json4s-native_2.11" % "3.5.1",
  "org.json4s" % "json4s-jackson_2.11" % "3.5.1",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)