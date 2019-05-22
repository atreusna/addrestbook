package addressbook;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class Controller {
	
	String output;
	String index;
	Database database;
	String searchHitID;
	
	public Controller(Database database, String index) {
		output = "";
		this.database = database;
		this.index = index;
	}
	
	public String listAll(int pageSize, int pageNumber, String query) {
		if (!doesIndexExist())											// prevents an error
			return "";
		output = "";
		SearchRequest searchRequest = new SearchRequest(index);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.queryStringQuery(query));
		searchRequest.source(searchSourceBuilder);
		
		try {
			SearchResponse response = database.getClient().search(searchRequest, RequestOptions.DEFAULT);
			List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
			
			int startAt = pageSize * (pageNumber - 1);					// Setting bounds for where to return results from the list, based on page size and number
			int endAt = startAt + pageSize - 1;
			if (endAt >= searchHits.size())
				endAt = searchHits.size() - 1;
			
			for (int i = startAt; i <= endAt; i++) {
				output += searchHits.get(i).getSourceAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	public boolean doesNameExist(String name) {
		SearchHit[] searchHit = searchName(name);
		if (searchHit.length > 0) {
			searchHitID = searchHit[0].getId();							// To keep track of which entry was just searched for so it won't have to
			return true;												// be searched a second time. Understandably not the best solution since it only
		}																// works if this is called right before updateEntry/deleteEntry
		else
			return false;
	}
	
	public String getName(String name) {
		SearchHit[] searchHit = searchName(name);
		if (searchHit.length > 0) {
			searchHitID = searchHit[0].getId();
			return searchHit[0].getSourceAsString();
		}
		else
			return "";
	}
	
	public SearchHit[] searchName(String name) {
		if (!doesIndexExist())
			return new SearchHit[0];
		SearchRequest searchRequest = new SearchRequest(index);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.termQuery("name", name));
		searchRequest.source(searchSourceBuilder);
		
		try {
			SearchResponse response = database.getClient().search(searchRequest, RequestOptions.DEFAULT);
			return response.getHits().getHits();
		} catch (IOException e) {
			e.printStackTrace();
			return new SearchHit[0];
		}
	}
	
	public void updateEntry(String jsonInput, String name) {
		UpdateRequest request = new UpdateRequest(index, searchHitID);
		request.doc(jsonInput, XContentType.JSON);
		try {
			database.getClient().update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addEntry(String jsonInput, String name) {
		IndexRequest request = new IndexRequest("contacts");
		request.source(jsonInput, XContentType.JSON);
		try {
			database.getClient().index(request, RequestOptions.DEFAULT);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ElasticsearchException e) {
			System.out.println("ElasticsearchException ERROR " + e.getDetailedMessage());
		}
	}
	
	public void deleteEntry(String name) {
		DeleteRequest request = new DeleteRequest(index, searchHitID);
		try {
			database.getClient().delete(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doesIndexExist() {
		GetIndexRequest request = new GetIndexRequest(index);
		try {
			return database.getClient().indices().exists(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
