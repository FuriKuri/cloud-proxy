lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.1"
)

lazy val server = (project in file("server")).
  settings(
    commonSettings,
    name := "server"
  )

lazy val client = (project in file("client")).
  settings(
    commonSettings,
    name := "client"
  )