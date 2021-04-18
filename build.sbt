name := "peaceland_scala"

version := "0.1"

scalaVersion := "2.12.13"

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.3.1"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-hdfs-client
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs-client" % "3.3.0"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.1"


// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.3.0"

// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-hdfs
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "3.3.0"


