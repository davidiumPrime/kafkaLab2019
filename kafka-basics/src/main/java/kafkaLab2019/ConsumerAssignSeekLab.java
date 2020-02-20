package kafkaLab2019;

import java.time.Duration;
import java.util.Arrays;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerAssignSeekLab {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(ConsumerLab.class);

		String bootstrapServers = "localhost:9092";
		String groupId = "Fifth-Lab";
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

		// Assign and Seek - Primarily used to replay data or seek a specific message
		//assign
		TopicPartition partitionToRead = new TopicPartition("first_topic", 0);
		long offsetToRead = 15L;
		consumer.assign(Arrays.asList(partitionToRead));

		// seek - in this case, partition 15
		consumer.seek(partitionToRead, offsetToRead);

		int messagesToRead = 5;
		boolean keepOnReading = true;
		int messagesProcessed = 0;

		// poll for new data
		while (keepOnReading) {

			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

			for (ConsumerRecord<String, String> record : records) {
				messagesToRead += 1;
				logger.info("Key: " + record.key() + ", Value: " + record.value());
				logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
				if (messagesToRead >= messagesProcessed) {
					keepOnReading = false;
					break;
				}
			}
		}
		logger.info("Exiting the Application.");
		consumer.close();
	}
}