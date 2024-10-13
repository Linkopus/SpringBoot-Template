package com.linkopus.ms.RabbitMqClient.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisher.class);

	private final RabbitTemplate rabbitTemplate;

	@Autowired
	public MessagePublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publish(String topic, Object data, String identity) {
		try {
			Request request = new Request();
			request.setPublisherApikey(identity);
			request.setData(data);

			rabbitTemplate.convertAndSend(topic, request);

			LOGGER.info("Data published successfully via exchange {}.", topic);
		} catch (Exception e) {
			LOGGER.error("Error while publishing data: {}", e.getMessage());
		}
	}
}
