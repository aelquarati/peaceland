import Utils.{CustomDeserializer, CustomSerializer}
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

  //serde is used to pair serializer and desializer of an object. needed to build the stream with key/value: string/dronemessage
  implicit val stringSerde = Serdes.String
  implicit val messageSerde = Serdes.serdeFrom(new CustomSerializer[DroneMessage], new CustomDeserializer[DroneMessage])

  val properties = new Properties()
  properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "messages")
  properties.put("key.serializer", classOf[StringSerializer].getName)
  properties.put("value.serializer", classOf[CustomSerializer[DroneMessage]].getName)

  val builder = new StreamsBuilder
  val stream  = builder.stream[String, DroneMessage]("drone-input")(Consumed.`with`(stringSerde, messageSerde))

  stream.to("alerts")(Produced.`with`(stringSerde, messageSerde))

  val streams = new KafkaStreams(builder.build, properties)

  streams.start
  stream.print(Printed.toSysOut)

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

}
