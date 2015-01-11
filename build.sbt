name := """watchrooms"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  javaWs,
  "org.mongodb" % "mongo-java-driver" % "2.12.4",
  "org.springframework.data" % "spring-data-mongodb" % "1.6.1.RELEASE"
)
