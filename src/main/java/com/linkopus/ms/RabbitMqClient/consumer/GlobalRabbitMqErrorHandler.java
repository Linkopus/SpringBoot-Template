package com.linkopus.ms.RabbitMqClient.consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
public class GlobalRabbitMqErrorHandler implements ErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRabbitMqErrorHandler.class);

	@Value("${identity:default_identity}")
	private String identity;

	@Override
	public void handleError(@NotNull Throwable t) {
		if (t instanceof ListenerExecutionFailedException exception) {
			Message failedMessage = exception.getFailedMessage();
			String queue = failedMessage.getMessageProperties().getConsumerQueue();
			String topic = queue.substring(identity.length() + 1);

			LOGGER.error("Error consuming message for topic '{}' from queue '{}', error: {}", topic, queue,
					t.getMessage());
		} else {
			LOGGER.error("Error in message listener: {}", t.getMessage());
		}
	}
}
