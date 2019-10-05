package kafkaLab2019;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerLabwithKeys {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("Hi ProducerWithCallbackLab.");

		final Logger logger = LoggerFactory.getLogger(ProducerWithCallbackLab.class);
		// create producer properties, because producers need that stuff.
		String bootstrapServers = "localhost:9092";
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		for (int i = 0; i < 10; i++) {
			// create the producer

			String topic = "first_topic";
			String value = "hello world" + Integer.toString(i);
			String key = "id " + Integer.toString(i);
			KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

			// create the records
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);
			logger.info("Key: " + key); //log the key

			// send data
			producer.send(record, new Callback() {

				public void onCompletion(RecordMetadata metadata, Exception exception) {

					if (exception == null) {
						System.out.println("===CALLBACK LOG===");
						logger.info("\n Topic: " + metadata.topic() + "\n Partition: " + metadata.partition()
								+ "\n Offset: " + metadata.offset() + "\n Timestamp: " + metadata.timestamp());
					} else {
						logger.error("Error During Production: " + exception);
					}
				}
			}).get(); // block the .send() to make sync - don't do this in a real production

			producer.flush();
			producer.close();

		}
	}
}
