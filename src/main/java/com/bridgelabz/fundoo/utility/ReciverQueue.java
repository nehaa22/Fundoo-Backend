package com.bridgelabz.fundoo.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Component;

import com.bridgelabz.fundoo.user.model.EmailInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReciverQueue {
	 @Autowired
	 MailServiceUtility mailServiceUtility;
	
	 public void receiveMessage(String message) {
	
	  processMessage(message);
	 }
	 public void receiveMessage(byte[] message) {
	  String strMessage = new String(message);
	 
	  processMessage(strMessage);
	 }
	 private void processMessage(String message) {
	  try {
	   EmailInfo emailInfo = new ObjectMapper().readValue(message, EmailInfo.class);
	 
	   mailServiceUtility.send(emailInfo);
	  	} catch (JsonParseException e) {
	 
	  	} catch (JsonMappingException e) {
	
	  		} catch (Exception e) {
	  
	  			}
	 }
}
