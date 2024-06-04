import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    val props = new Properties()

props.put("bootstrap.servers", "localhost:9092")
 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

 props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
 props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
 props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])

 val producer = new KafkaProducer[String, String](props)

   //  Send a message
   val key = "my_key"
   val value = "my_value"

   val record = new ProducerRecord[String, String]("my_topic", key, value)
   producer.send(record)
   producer.close()


   val result = producer.send(record)
  }
}