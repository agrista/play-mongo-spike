import com.github.play2war.plugin._
import PlayKeys._

name := "mongo-connectivity"

version := "1.0-SNAPSHOT-big"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
    // for Mongo DB access with Jongo
    "org.mongodb" % "mongo-java-driver" % "3.0.1",
    "org.jongo" % "jongo" % "1.2",

    javaJdbc,
    javaEbean,
    cache,
    javaWs
)

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.0"

Play2WarKeys.explodedJar := true

Play2WarKeys.filteredArtifacts := Seq(("com.typesafe.play","play_2.10"))

lazy val root = (project in file(".")).enablePlugins(PlayJava)
