name := "Spar"

version := "0.0.1"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "latest.integration" % "test"

libraryDependencies += "org.mozilla" % "rhino" % "latest.integration"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.1"
