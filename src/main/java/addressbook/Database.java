package addressbook;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class Database {
	
	private RestHighLevelClient client;
	
	public Database() {
		makeConnection();
	}
	
	private void makeConnection() {
		client = new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));
	}
	
	public void closeConnection() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RestHighLevelClient getClient() {
		return client;
	}
}
