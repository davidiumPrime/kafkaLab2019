package twitterKafkaLab2019;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterProducer2 {
	Logger log = LoggerFactory.getLogger(TwitterProducer.class);
	Producer producer;

	
	
	public static void main(String[] args) {
		try {
			new TwitterProducer2().run();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Attempts to establish a connection.
	}
	public Twitter getTwitterinstance() {

		/**
		 * if not using properties file, we can set access token by following way
		 */
		Dotenv dotenv = Dotenv.load();

		String consumerKey = dotenv.get("consumerKey");
		String consumerSecret = dotenv.get("consumerSecret");
		String token = dotenv.get("token");
		String tokenSecret = dotenv.get("tokenSecret");

		
		log.info(
			"\n"+	"====="+ "\n"+
				"Consumer Key: " + consumerKey + "\n" +
				"Consumer Secret: " + consumerSecret + "\n" + 
				"Token: " + token + "\n" + 
				"Token Secret: " + tokenSecret + "\n" +
				"====="
				);
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(token).setOAuthAccessTokenSecret(tokenSecret).setHttpStreamingReadTimeout(120000);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}

	public void run() throws TwitterException {
		Twitter twitter = getTwitterinstance();
		Query query = new Query("bitcoin");
		QueryResult result;
		result = twitter.search(query);
		List<Status> statuses = result.getTweets();
		List<String> myTweets = statuses.stream().map(item -> item.getText()).collect(Collectors.toList());

		producer = startProducer();
		for (int i = 0; i < myTweets.size(); i++) {

			String msg = myTweets.get(i);

			if (msg != null) {
				log.info("msg :" + msg);
				producer.send(new ProducerRecord<>("twitter_tweets", null, msg), new Callback() {
					@Override
					public void onCompletion(RecordMetadata recordMetadata, Exception e) {
						if (e != null) {
							log.info("Something bad happened " + e.getMessage());
						}
					}
				});
			}
		}
		producer.flush();
		producer.close();
	}

	public Producer startProducer() {
		String bootstrapServers = "localhost:9092";
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		return producer;

	}
	
	
}
