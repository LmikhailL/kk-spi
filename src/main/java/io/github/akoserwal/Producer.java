package io.github.akoserwal;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;

public class Producer {

  private final static String BOOTSTRAP_SERVER = "http://kafka_common:29094";

  public static void publishEvent(String topic, UserRegisteredEvent value) {
    // Create producer properties
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JsonSerializer.class.getName());

    // Create Kafka producer
    KafkaProducer<String, JsonNode> producer = new KafkaProducer<>(properties);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.valueToTree(value);

    final Header header = new RecordHeader("CorrelationId",
        UUID.randomUUID().toString().getBytes());
    // Send the data to a Kafka topic
    ProducerRecord<String, JsonNode> record = new ProducerRecord<String, JsonNode>(
        topic,
        null,
        null,
        jsonNode,
        List.of(header)
    );

    producer.send(record, (recordMetadata, e) -> {
      if (e == null) {
        System.out.println("Sent data to topic: " + recordMetadata.topic());
        System.out.println("Partition: " + recordMetadata.partition());
        System.out.println("Offset: " + recordMetadata.offset());
      } else {
        e.printStackTrace();
      }
    });

    // Close the producer
    producer.close();
  }
}
