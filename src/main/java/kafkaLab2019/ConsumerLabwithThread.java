package kafkaLab2019;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerLabwithThread {

	public static void main(String[] args) {
		new ConsumerLabwithThread().run();
	}

	private ConsumerLabwithThread() {

	}

	private void run() {
		Logger logger = LoggerFactory.getLogger(ConsumerLabwithThread.class);

		String bootstrapServers = "localhost:9092";
		String groupId = "Fourth-Lab";
		String topic = "first_topic";
		// create latch for multi-threading
		CountDownLatch latch = new CountDownLatch(1);

		// create consumer runnable
		Runnable myConsumerThread = new ConsumerThread(latch, bootstrapServers, groupId, topic);

		Thread myThread = new Thread(myConsumerThread);
		myThread.start();

		// add shutdownhook

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Caught Shutdwon Hook");
			((ConsumerThread) myConsumerThread).shutdown();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("Application was interrupted", e);
		} finally {
			logger.info("Application is closing");
		}
	}

	public class ConsumerThread implements Runnable {

		private CountDownLatch latch;
		private KafkaConsumer<String, String> consumer;
		private Logger logger = LoggerFactory.getLogger(ConsumerThread.class.getName());

		public ConsumerThread(CountDownLatch latch, String bootstrapServers, String groupId, String topic) {
			this.latch = latch;
			Properties properties = new Properties();
			properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

			// create consumer
			consumer = new KafkaConsumer<String, String>(properties);
			//consumer.subscribe(Collections.singleton(topic));
			 consumer.subscribe(Arrays.asList(topic));
		}

		@Override
		public void run() {
			try {
				while (true) {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

					for (ConsumerRecord<String, String> record : records) {
						logger.info("Key: " + record.key() + ", Value: " + record.value());
						logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
					}
				}
			} catch (WakeupException e) {
				logger.info("Shutdown signal received!");
			} finally {

				consumer.close();
				// tell main code that the consumer is done.
				latch.countDown();
			}
		}

		private void shutdown() {
			// interrupts consumer.poll(). Will throw the exception WakeUpException
			consumer.wakeup();
		}

	}
}
