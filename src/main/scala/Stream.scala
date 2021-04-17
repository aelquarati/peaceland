//import SerializationUtils.{CustomDeserializer, CustomSerializer}
import org.apache.kafka.common.serialization.{Serdes, StringSerializer}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}

import java.util.Properties
import java.util.concurrent.TimeUnit

object Stream extends App {

  import org.apache.kafka.streams.scala.serialization.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._


  val properties = new Properties()
  properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "messages")
  properties.put("key.serializer", classOf[StringSerializer].getName)
  properties.put("value.serializer", classOf[StringSerializer].getName)

  val builder = new StreamsBuilder

  //node that consume records from kafka and store into a kstream
  val stream  = builder.stream[String, String]("drone-input")

  //filter messages and send them back to kafka in alerts topic
  stream.filter((key, message) => shouldSendAlert(message.split(" ").length-1, message))
       .to("alerts")

  //stream.print(Printed.toSysOut)

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
