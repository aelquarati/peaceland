import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.permission.FsAction
import org.apache.hadoop.fs.{FileSystem, LocalFileSystem, Path}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder

import java.util.Properties
import java.util.concurrent.TimeUnit
import java.io.{FileWriter, PrintWriter}

object Stream extends App {

  import org.apache.kafka.streams.scala.serialization.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

  val conf = new Configuration

  conf.set("fs.defaultFS", "hdfs://localhost:9000")
  val fs = FileSystem.get(conf)
  val path = new Path("/tmp/test.txt")
  val output = fs.create(path)


  def write(s:String) = {
    try {
      output.writeBytes(s + "\n")
      println("wrote message : " + s)
      output.hflush()
    }
  }

  val properties = new Properties()
  //Kafka broker this application is talking to runs on local machine with port 9092
  properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "messages")
  properties.put("key.serializer", classOf[StringSerializer].getName)
  properties.put("value.serializer", classOf[StringSerializer].getName)

  val builder = new StreamsBuilder

  //node that consume records from kafka and store into a kstream
  val stream  = builder.stream[String, String]("drone-input")

  stream.foreach((key, message) => write(message))

  //filter messages and send them back to kafka in alerts topic
  stream.filter((key, message) => shouldSendAlert(message.split(" ").length-1, message))
       .to("alerts")

  stream.print(Printed.toSysOut)

  //create a kafka streams client upon builder and properties
  val streams = new KafkaStreams(builder.build, properties)
  streams.start

  //shut the stream properly when asked to, giving 10 seconds for threads to join in
  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

  val ALERT_WORDS = List("riot", "rebellion")

  def shouldSendAlert(i : Int, words: String): Boolean = {
    val word = words.split(" ")(i)
    if(i>0) {
      ALERT_WORDS.contains(word) || shouldSendAlert(i-1, words)
    }
    else {
      true
    }
  }

}
