package com.linkopus.ms.services.events;

import com.linkopus.ms.RabbitMqClient.consumer.QueueNameProvider;
import com.linkopus.ms.RabbitMqClient.publisher.Request;
import com.linkopus.ms.services.events.consumers.ConsumerService;
import com.linkopus.ms.services.events.consumers.Topics;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsumerServiceTest {

	private ConsumerService consumerService;
	private QueueNameProvider queueNameProvider;
	private Logger logger;
	private ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> listAppender;

	@BeforeEach
	void setUp() {
		queueNameProvider = mock(QueueNameProvider.class);
		consumerService = new ConsumerService(queueNameProvider);
		logger = (Logger) LoggerFactory.getLogger(ConsumerService.class);
		listAppender = new ListAppender<>();
		listAppender.start();
		logger.addAppender(listAppender);
	}

	@AfterEach
	void tearDown() {
		logger.detachAppender(listAppender);
	}

	@Test
	void testInit() {
		consumerService.init();
		var logsList = listAppender.list;
		assertEquals(1, logsList.size(), "Should log one message for subscription.");
		var loggingEvent = logsList.get(0);
		assertEquals("Subscribed to topic successfully: {}", loggingEvent.getMessage());
		assertEquals(ch.qos.logback.classic.Level.INFO, loggingEvent.getLevel());
		assertArrayEquals(new Object[]{Topics.TEST}, loggingEvent.getArgumentArray());
	}

	@Test
	void testSubscribeToTopicTest() throws Exception {
		Request message = new Request();
		message.setPublisherApikey("test-api-key");
		message.setData("test-data");
		consumerService.subscribeToTopicTest(message);
		var logsList = listAppender.list;
		assertEquals(3, logsList.size(), "Should log three messages when a message is received.");
		var log1 = logsList.get(0);
		assertEquals("Received message on topic {} from {}", log1.getMessage());
		assertEquals(ch.qos.logback.classic.Level.INFO, log1.getLevel());
		assertArrayEquals(new Object[]{Topics.TEST, "test-api-key"}, log1.getArgumentArray());
		var log2 = logsList.get(1);
		assertEquals("Received message: topic : test", log2.getFormattedMessage());
		assertEquals(ch.qos.logback.classic.Level.INFO, log2.getLevel());
		var log3 = logsList.get(2);
		assertEquals("message body: {}", log3.getMessage());
		assertEquals(ch.qos.logback.classic.Level.INFO, log3.getLevel());
		assertArrayEquals(new Object[]{"test-data"}, log3.getArgumentArray());
	}
}
