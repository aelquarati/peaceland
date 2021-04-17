name := "peaceland_scala"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.7.0"
libraryDependencies += "org.apache.kafka" % "kafka-streams-scala_2.13" % "2.7.0"
libraryDependencies += "org.apache.kafka" % "kafka_2.13" % "2.7.0"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.3.0"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-hdfs
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "3.3.0"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-hdfs-client
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs-client" % "3.3.0"



