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

		// create the records
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic",
				"Hello World of Kakfa Topics");

		// send data
		producer.send(record, new Callback() {

			public void onCompletion(RecordMetadata metadata, Exception exception) {
				System.out.println("===CALLBACK LOG===");	
			
				if(exception ==null) {
					logger.info(
							"Topic: " + metadata.topic() + 
							"Partition: " + metadata.partition() + 
							"Offset: " + metadata.offset() + 
							"Timestamp: " + metadata.timestamp()
							);
				}
				else {
					logger.error("Error During Production: " + exception);
				}
			}});

		producer.flush();
		producer.close();

	}
}
