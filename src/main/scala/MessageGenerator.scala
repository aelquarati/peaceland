import Main.properties
import Utils.{CustomDeserializer, CustomSerializer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{Serdes, StringSerializer}
import org.apache.kafka.streams.StreamsConfig

import java.util.Properties

object MessageGenerator {

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

  val drone = new Drone("1", 12, 23)
  val citizen = new Citizen("2", 18, "France", 23)
  val list = List("hello")
  val droneMessage = new DroneMessage(drone, citizen, list )

  val producer = new KafkaProducer[String, DroneMessage](properties)
  producer.send(new ProducerRecord[String, DroneMessage]("drone-input", "1", droneMessage))


}
