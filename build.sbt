name := "scala-dl4j-test2"

version := "0.1"

scalaVersion := "2.13.1"




libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.29" % Test
//libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0-alpha1" from "file:///Users/bwong/git/perf-tools/commonclass/target/scala-2.11/commonclass_2.11-1.0.jar"

libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-beta5"
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "1.0.0-beta5"

