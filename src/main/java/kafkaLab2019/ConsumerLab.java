package kafkaLab2019;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerLab {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(ConsumerLab.class);

		String bootstrapServers = "localhost:9092";
		String groupId = "Third-Lab";
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		// create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

		// subscribe consumer to the topics

		consumer.subscribe(Collections.singleton("first_topic")); // _<this is subscribing to one topic, but you can
																	// subscribe to more as below
		// consumer.subscribe(Arrays.asList("first_topic", "second_topic"));

		// poll for new data
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

			for (ConsumerRecord<String, String> record : records) {
				logger.info("Key: " + record.key() + ", Value: " + record.value());
				logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
			}
		}
	}
}
