package kafkaLab2019;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.*;

public class ProducerWithCallbackLab {
	public static void main(String[] args) {
		System.out.println("Hi ProducerWithCallbackLab.");

		final Logger logger = LoggerFactory.getLogger(ProducerWithCallbackLab.class);
		// create producer properties, because producers need that stuff.
		String bootstrapServers = "localhost:9092";
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// create the producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

		
		for (int i =0; i<10; i++) {
		// create the records
			
			
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic",
				"==HELLO WORLD OF KAFKA TOPICS #" + Integer.toString(i)+"===");

		// send data
		System.out.println("===CALLBACK LOG" + Integer.toString(i)+"===");
		producer.send(record, new Callback() {

			public void onCompletion(RecordMetadata metadata, Exception exception) {

				if (exception == null) {
					
					logger.info("\n Topic: " + metadata.topic() + "\n Partition: " + metadata.partition()
							+ "\n Offset: " + metadata.offset() + "\n Timestamp: " + metadata.timestamp());
				} else {
					logger.error("Error During Production: " + exception);
				}
			}
		});
		
		}

		producer.flush();
		producer.close();

	}
}
