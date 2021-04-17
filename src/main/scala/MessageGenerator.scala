import SerializationUtils.{CustomDeserializer, CustomSerializer}
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

  val producer = new KafkaProducer[String, DroneMessage](properties)

  def sendMessage(i:Int, drone:Drone): Any = {
    if(i>=0) {
      val message = drone.createMessage(Population.citizens(i))
      producer.send(new ProducerRecord[String, DroneMessage]("drone-input", message))
      sendMessage(i-1, drone)
    }
  }

  def sendAllDronesMessages(i:Int): Any = {
    if(i>=0) {
      val drone = Population.drones(i)
      sendMessage(200, drone)
      sendAllDronesMessages(i-1)
    }
  }
}
