val akkaVersion = "2.4.16"

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.1",
  test in assembly := {}
)

lazy val server = (project in file("server")).
  settings(
    commonSettings,
    name := "server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion
    ),
    mainClass in assembly := Some("net.furikuri.cproxy.server.ServerTest")
  )

lazy val client = (project in file("client")).
  settings(
    commonSettings,
    name := "client",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion
    ),
    mainClass in assembly := Some("net.furikuri.cproxy.client.Main")
  )