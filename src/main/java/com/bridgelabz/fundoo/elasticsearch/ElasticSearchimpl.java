//package com.bridgelabz.fundoo.elasticsearch;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.delete.DeleteRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.update.UpdateRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Service;
//
//import com.bridgelabz.fundoo.note.model.Note;
//import com.bridgelabz.fundoo.response.Response;
//import com.bridgelabz.fundoo.utility.ResponseHelper;
//import com.fasterxml.jackson.databind.ObjectMapper;
//@Service
//@PropertySource("classpath:ExceptinMessages.properties")
//public class ElasticSearchimpl {
//
//	String index_name = "fundoo";
//	String type_name = "note";
//
//
//	private RestHighLevelClient 
//	client = new RestHighLevelClient(
//			RestClient.builder(new HttpHost("localhost", 9200, "http")));
//	@Autowired
//	private Environment environment;
//
//	public Response updateNote(Note note) {
//		index_name = "fundoo";
//		type_name = "note";
//		 
//
//		ObjectMapper mapper = new ObjectMapper();
//		
//		Map<String, Object> noteCreate = mapper.convertValue(note, Map.class);
//		
//
//		// System.out.println(environment.getProperty("status.notes.updatedSuccessfull"));
//		UpdateRequest request = new UpdateRequest(index_name, type_name, String.valueOf(note.getNoteId())).doc(noteCreate);
//		System.out.println("request"+request);
//		try {
//			client.update(request, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Response response = ResponseHelper.sendError(202, environment.getProperty("status.notes.updatedSuccessfull"));
//		return response;
//	}
//
//	public Response deleteNote(Note note) {
//
//		ObjectMapper mapper = new ObjectMapper();
//		
//		Map<String, Object> noteCreate = mapper.convertValue(note, Map.class);
//
//		DeleteRequest request = new DeleteRequest(index_name, type_name, String.valueOf(note.getNoteId()));
//		// request.(noteCreate);
//		try {
//			client.delete(request, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Response response = ResponseHelper.sendError(304, environment.getProperty("status.note.notDeleted"));
//		return response;
//	}
//
//	public ArrayList<Note> serchNote(String search ,long id) {
//
//		ObjectMapper mapper = new ObjectMapper();
//		SearchRequest searchRequest = new SearchRequest(index_name).types(type_name);
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//		
//		QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(search).analyzeWildcard(true).field("title").field("description");//.filter(QueryBuilders.termQuery("userId",String.valueOf(id)));
//		
//		searchSourceBuilder.query(queryBuilder);
//		searchRequest.source(searchSourceBuilder);
//		SearchResponse searchResponse = new SearchResponse();
//		searchResponse = null;
//		try {
//			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		SearchHit[] hits = searchResponse.getHits().getHits();
//		ArrayList<Note> notes = new ArrayList<Note>();
//		for(SearchHit hit : hits)
//			notes.add(mapper.convertValue(hit.getSourceAsMap(), Note.class));
//		return notes;
//	}
//}
