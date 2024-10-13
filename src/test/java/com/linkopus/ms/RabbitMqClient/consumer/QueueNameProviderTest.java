package com.linkopus.ms.RabbitMqClient.consumer;

import com.linkopus.ms.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueueNameProviderTest {

	private RabbitAdmin rabbitAdmin;
	private Config config;
	private QueueNameProvider queueNameProvider;

	@BeforeEach
	void setUp() {
		rabbitAdmin = mock(RabbitAdmin.class);
		config = mock(Config.class);
		queueNameProvider = new QueueNameProvider(rabbitAdmin, config);
	}

	@Test
	void testCreateAndGetQueueName() {
		String identity = "serviceA";
		String topic = "myTopic";
		String expectedQueueName = identity + "_" + topic;

		when(config.getIdentity()).thenReturn(identity);

		String actualQueueName = queueNameProvider.createAndGetQueueName(topic);

		assertEquals(expectedQueueName, actualQueueName, "Queue name should be correctly constructed.");

		ArgumentCaptor<Queue> queueCaptor = ArgumentCaptor.forClass(Queue.class);
		verify(rabbitAdmin).declareQueue(queueCaptor.capture());
		Queue declaredQueue = queueCaptor.getValue();
		assertNotNull(declaredQueue, "Queue should not be null.");
		assertEquals(expectedQueueName, declaredQueue.getName(), "Declared queue name should match.");

		ArgumentCaptor<DirectExchange> exchangeCaptor = ArgumentCaptor.forClass(DirectExchange.class);
		verify(rabbitAdmin).declareExchange(exchangeCaptor.capture());
		DirectExchange declaredExchange = exchangeCaptor.getValue();
		assertNotNull(declaredExchange, "Exchange should not be null.");
		assertEquals(topic, declaredExchange.getName(), "Declared exchange name should match the topic.");

		ArgumentCaptor<Binding> bindingCaptor = ArgumentCaptor.forClass(Binding.class);
		verify(rabbitAdmin).declareBinding(bindingCaptor.capture());
		Binding declaredBinding = bindingCaptor.getValue();
		assertNotNull(declaredBinding, "Binding should not be null.");
		assertEquals(expectedQueueName, declaredBinding.getDestination(),
				"Binding destination should be the queue name.");
		assertEquals(topic, declaredBinding.getRoutingKey(), "Binding routing key should match the topic.");
		assertEquals(Binding.DestinationType.QUEUE, declaredBinding.getDestinationType(),
				"Destination type should be QUEUE.");
	}

	@Test
	void testCreateAndGetQueueName_NullTopic() {
		String identity = "serviceA";
		String topic = null;

		when(config.getIdentity()).thenReturn(identity);

		assertThrows(NullPointerException.class, () -> {
			queueNameProvider.createAndGetQueueName(topic);
		}, "Passing null topic should throw NullPointerException.");
	}

	@Test
	void testCreateAndGetQueueName_EmptyTopic() {
		String identity = "serviceA";
		String topic = "";
		String expectedQueueName = identity + "_";

		when(config.getIdentity()).thenReturn(identity);

		String actualQueueName = queueNameProvider.createAndGetQueueName(topic);

		assertEquals(expectedQueueName, actualQueueName,
				"Queue name should be correctly constructed even with empty topic.");

		verify(rabbitAdmin, times(1)).declareQueue(any(Queue.class));
		verify(rabbitAdmin, times(1)).declareExchange(any(DirectExchange.class));
		verify(rabbitAdmin, times(1)).declareBinding(any(Binding.class));
	}
}
