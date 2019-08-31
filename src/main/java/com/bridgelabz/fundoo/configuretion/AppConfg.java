package com.bridgelabz.fundoo.configuretion;

//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
import org.modelmapper.ModelMapper;


import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfg {
	/**
	 * Purpose : Creating bean object for PasswordEncoder
	 * @return : Return the bean object
	 */
	@Bean
	public PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder();
	}
	/**
	 * Purpose : Creating bean object for ModelMapper
	 * @return : Return the bean object
	 */

	@Bean
	public ModelMapper modelMapper() {
	    ModelMapper modelMapper = new ModelMapper();
	    modelMapper.getConfiguration()
	        .setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}
	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}
//	@Bean
//	public RestHighLevelClient client() {
//
//		RestHighLevelClient client = new RestHighLevelClient(
//				RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//		return client;
//
//	}
	
}