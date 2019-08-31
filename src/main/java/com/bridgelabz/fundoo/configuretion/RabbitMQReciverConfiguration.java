package com.bridgelabz.fundoo.configuretion;

//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bridgelabz.fundoo.utility.ReciverQueue;

@Configuration
public class RabbitMQReciverConfiguration {
//	private static final String LISTENER_METHOD = "receiveMessage";
//	 @Value("${queue.name}")
//	 private String queueName;
//	 
//	 @Value("${fanout.exchange}")
//	 private String fanoutExchange;
//	 
//	 @Bean
//	 SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,MessageListenerAdapter listenerAdapter) {
//	  SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//	  container.setConnectionFactory(connectionFactory);
//	  container.setQueueNames(queueName);
//	  container.setMessageListener(listenerAdapter);
//	  return container;
//	 }
//	 
//	 @Bean
//	 MessageListenerAdapter listenerAdapter(ReciverQueue reciverQueue) {
//	  return new MessageListenerAdapter(reciverQueue, LISTENER_METHOD);
//	 }

}
