name := "scala-utils"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

coverageMinimum := 70
coverageFailOnMinimum := true
coverageEnabled := true

Test / unmanagedResources / includeFilter := "*.txt"
