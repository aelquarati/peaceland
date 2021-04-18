import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringSerializer}
import org.apache.kafka.streams.StreamsConfig
import java.text.SimpleDateFormat

import java.util.Properties
object Producer extends App {

  def sendReportsPerMinute(): Unit = {
    val start = System.currentTimeMillis()
    MessageGenerator.sendAllDronesMessages(50)
    MessageGenerator.moveDrones(50)
    val end = System.currentTimeMillis()
    val elapsedTime = end - start
    Thread.sleep(60000 - elapsedTime)
  }

  def sendReports(i:Int): Unit = {
    if(i>0) {
      sendReportsPerMinute()
      sendReports(1)
    }
  }

  sendReports(1)

  object MessageGenerator {

    val properties = new Properties()
    properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "messages")
    properties.put("key.serializer", classOf[StringSerializer].getName)
    properties.put("value.serializer", classOf[StringSerializer].getName)

    val producer = new KafkaProducer[String, String](properties)

    def sendMessage(drone:Drone): Any = {
      val dateFormatter = new SimpleDateFormat("yyyy-MM-dd")
      val hourFormatter = new SimpleDateFormat("HH:mm")
      val date = System.currentTimeMillis
      val formattedDate = dateFormatter.format(date)
      val formattedHour = hourFormatter.format(date)
      val citizenId = util.Random.nextInt(200)
      val message = drone.createMessage(Population.citizens(citizenId))
      val stringMessage = message.toString
      val keyDate = " Date:"+formattedDate+"; Hour:"+formattedHour
      producer.send(new ProducerRecord[String, String]("drone-input", keyDate, stringMessage))
    }

    def sendAllDronesMessages(i:Int): Any = {
      if(i>=0) {
        val drone = Population.drones(i)
        sendMessage(drone)
        sendAllDronesMessages(i-1)
      }
    }

    def moveDrones(i:Int):Any = {
      if(i>=0) {
        val drone = Population.drones(i)
        drone.move(2,2)
        moveDrones(i-1)
      }
    }
  }

}
