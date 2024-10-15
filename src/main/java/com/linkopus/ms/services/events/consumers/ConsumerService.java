package com.linkopus.ms.services.events.consumers;

import com.linkopus.ms.RabbitMqClient.consumer.QueueNameProvider;
import com.linkopus.ms.RabbitMqClient.publisher.Request;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

	@Autowired
	public ConsumerService(QueueNameProvider queueNameProvider) {
	}

	@PostConstruct
	public void init() {
		for (Topics topic : Topics.values()) {
			LOGGER.info("Subscribed to topic successfully: {}", topic);
		}
	}

	@RabbitListener(containerFactory = "listenerContainerFactory", queues = "#{queueNameProvider.createAndGetQueueName('test')}")
	public void subscribeToTopicTest(Request message) throws Exception {
		LOGGER.info("Received message on topic {} from {}", Topics.TEST, message.getPublisherApikey());

		LOGGER.info("Received message: topic : test");

		LOGGER.info("message body: {}", message.getData());
	}
}
