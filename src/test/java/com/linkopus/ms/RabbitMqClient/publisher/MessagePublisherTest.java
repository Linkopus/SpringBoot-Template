package com.linkopus.ms.RabbitMqClient.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessagePublisherTest {

	private MessagePublisher messagePublisher;
	private RabbitTemplate rabbitTemplate;

	@BeforeEach
	void setUp() {
		rabbitTemplate = mock(RabbitTemplate.class);
		messagePublisher = new MessagePublisher(rabbitTemplate);
	}

	@Test
	void testPublish_Successful() {
		String topic = "testTopic";
		Object data = "testData";
		String identity = "testIdentity";

		messagePublisher.publish(topic, data, identity);

		ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
		verify(rabbitTemplate, times(1)).convertAndSend(eq(topic), requestCaptor.capture());

		Request sentRequest = requestCaptor.getValue();
		assertNotNull(sentRequest, "Request should not be null");
		assertEquals(identity, sentRequest.getPublisherApikey(), "Publisher API key should match");
		assertEquals(data, sentRequest.getData(), "Data should match");
	}

	@Test
	void testPublish_ExceptionThrown() {
		String topic = "testTopic";
		Object data = "testData";
		String identity = "testIdentity";

		doThrow(new RuntimeException("Test Exception")).when(rabbitTemplate).convertAndSend(eq(topic),
				any(Request.class));

		messagePublisher.publish(topic, data, identity);

		verify(rabbitTemplate, times(1)).convertAndSend(eq(topic), any(Request.class));
	}

	@Test
	void testPublish_NullData() {
		String topic = "testTopic";
		Object data = null;
		String identity = "testIdentity";

		messagePublisher.publish(topic, data, identity);

		ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
		verify(rabbitTemplate, times(1)).convertAndSend(eq(topic), requestCaptor.capture());

		Request sentRequest = requestCaptor.getValue();
		assertNotNull(sentRequest, "Request should not be null");
		assertEquals(identity, sentRequest.getPublisherApikey(), "Publisher API key should match");
		assertNull(sentRequest.getData(), "Data should be null");
	}
}
