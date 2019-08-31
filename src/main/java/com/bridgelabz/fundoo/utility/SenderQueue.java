package com.bridgelabz.fundoo.utility;

//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bridgelabz.fundoo.user.model.EmailInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SenderQueue {
	
//	@Value("${fanout.exchange}")
//	 private String fanoutExchange;
//	 
//	 private  RabbitTemplate rabbitTemplate;
//	
//	 @Autowired
//	 public SenderQueue(RabbitTemplate rabbitTemplate) {
//
//	  this.rabbitTemplate = rabbitTemplate;
//	  
//	 }
//	 
//	 public void produce(EmailInfo emailInfo) throws Exception {
//	
//	  rabbitTemplate.setExchange(fanoutExchange);
//	  rabbitTemplate.convertAndSend(new ObjectMapper().writeValueAsString(emailInfo));

//	 }

	
}
