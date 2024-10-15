package com.linkopus.ms.RabbitMqClient.consumer;

import com.linkopus.ms.config.Config;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class QueueNameProvider {

	private final RabbitAdmin rabbitAdmin;
	private final Config config;

	public QueueNameProvider(RabbitAdmin rabbitAdmin, Config config) {
		this.rabbitAdmin = rabbitAdmin;
		this.config = config;
	}

	public String createAndGetQueueName(String topic) {
		Objects.requireNonNull(topic, "Topic cannot be null");
		String queueName = config.getIdentity() + "_" + topic;
		Queue queue = new Queue(queueName, true, false, false);
		rabbitAdmin.declareQueue(queue);
		DirectExchange exchange = new DirectExchange(topic);
		rabbitAdmin.declareExchange(exchange);
		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(topic));
		return queueName;
	}
}
