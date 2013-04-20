name := "LispParser"

version := "0.0.1"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "org.mozilla" % "rhino" % "1.7R3"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"
