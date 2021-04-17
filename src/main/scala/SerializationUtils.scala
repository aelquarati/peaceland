import org.apache.kafka.common.serialization.{Deserializer, Serializer}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}


object SerializationUtils {

  class CustomSerializer[T] extends Serializer[T] {
    override def serialize(topic: String, data: T): Array[Byte] = {
      val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(stream)
      oos.writeObject(data)
      oos.close()
      stream.toByteArray
    }
  }

  class CustomDeserializer[T] extends Deserializer[T] {
    override def deserialize(topic: String, data: Array[Byte]): T = {
      val objIn = new ObjectInputStream(new ByteArrayInputStream(data))
      val obj = objIn.readObject().asInstanceOf[T]
      objIn.close
      obj
    }
  }
}
