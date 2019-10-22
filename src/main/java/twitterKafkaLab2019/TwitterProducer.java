package twitterKafkaLab2019;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import io.github.cdimascio.dotenv.Dotenv;

public class TwitterProducer {

	Logger log = LoggerFactory.getLogger(TwitterProducer.class);

	public TwitterProducer() {

	}

	public static void main(String[] args) {
		new TwitterProducer().run();
		// Attempts to establish a connection.
	}

	public void run() {
		/**
		 * Set up your blocking queues: Be sure to size these properly based on expected
		 * TPS of your stream
		 */
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

		// create twitter client
		Client client = createTwitterClient(msgQueue);
		// Attempts to establish a connection.
		client.connect();
		// create a kafka producer

		// loop to send tweets to kafka
		// on a different thread, or multiple different threads....
		while (!client.isDone()) {
			String msg = null;
			try {
				msg = msgQueue.poll(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				client.stop();
			}
			if (msg != null) {
				log.info(msg);

			}
			log.info("End of Application");
		}

	}
	public Client createTwitterClient(BlockingQueue<String> msgQueue) {

		// source: https://github.com/twitter/hbc
		/**
		 * Declare the host you want to connect to, the endpoint, and authentication
		 * (basic auth or oauth)
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

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms

		List<String> terms = Lists.newArrayList("bitcoin");
		hosebirdEndpoint.trackTerms(terms);
		

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));

		Client hosebirdClient = builder.build();
		return hosebirdClient;

	}

	public void createTwitterProducer() {
	}
}
