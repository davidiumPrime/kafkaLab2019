package twitterKafkaLab2020;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;

public class ElasticSearchConsumer {
	static Logger log = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
	public static RestHighLevelClient createClient() {
		 log = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
		// put your dotenv file under the classpath
		Dotenv dotenv = Dotenv.load();
		String hostname = dotenv.get("hostname");
		String username = dotenv.get("username");
		String password = dotenv.get("password");

		
		log.info("\n" + "=====" + "\n" + "Hostname: " + hostname + "\n" + "Username: " + username
				+ "\n" + "Password: " + password + "\n" + "=====");
		
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

		RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 443, "https"))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {

						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				});
		RestHighLevelClient client = new RestHighLevelClient(builder);
		return client;

	}

	public static void main(String[] args) {
		
		try {
			 log = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
			RestHighLevelClient client = createClient();

			String jsonString = "{\"foo\":\"bar\"}";

			// make sure these indices exist under your elasticsearch credentials
			@SuppressWarnings("deprecation")
			IndexRequest indexRequest = new IndexRequest("twitter", "tweets").source(jsonString, XContentType.JSON);

			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			String id = indexResponse.getId();
			log.info(id);

			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
