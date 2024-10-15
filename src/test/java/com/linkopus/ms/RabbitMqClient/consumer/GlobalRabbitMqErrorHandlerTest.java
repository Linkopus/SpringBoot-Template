package com.linkopus.ms.RabbitMqClient.consumer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalRabbitMqErrorHandlerTest {

	private GlobalRabbitMqErrorHandler errorHandler;
	private Logger logger;
	private ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> listAppender;

	@BeforeEach
	void setUp() {
		errorHandler = new GlobalRabbitMqErrorHandler();
		setIdentityField(errorHandler, "my_service");
		logger = (Logger) LoggerFactory.getLogger(GlobalRabbitMqErrorHandler.class);
		listAppender = new ListAppender<>();
		listAppender.start();
		logger.addAppender(listAppender);
	}

	@AfterEach
	void tearDown() {
		logger.detachAppender(listAppender);
	}

	private void setIdentityField(GlobalRabbitMqErrorHandler errorHandler, String identity) {
		try {
			java.lang.reflect.Field identityField = GlobalRabbitMqErrorHandler.class.getDeclaredField("identity");
			identityField.setAccessible(true);
			identityField.set(errorHandler, identity);
		} catch (Exception e) {
			throw new RuntimeException("Failed to set identity field via reflection", e);
		}
	}

	@Test
	void testHandleError_WithListenerExecutionFailedException() {
		String queueName = "my_service_topic1";
		String errorMessage = "Test exception message";
		org.springframework.amqp.core.MessageProperties messageProperties = mock(
				org.springframework.amqp.core.MessageProperties.class);
		when(messageProperties.getConsumerQueue()).thenReturn(queueName);
		Message failedMessage = mock(Message.class);
		when(failedMessage.getMessageProperties()).thenReturn(messageProperties);
		Exception cause = new Exception(errorMessage);
		ListenerExecutionFailedException exception = new ListenerExecutionFailedException("Listener execution failed",
				cause, failedMessage);
		errorHandler.handleError(exception);
		var logsList = listAppender.list;
		assertEquals(1, logsList.size(), "Should log one error message");
		var loggingEvent = logsList.get(0);
		assertEquals(ch.qos.logback.classic.Level.ERROR, loggingEvent.getLevel());
		String expectedTopic = "topic1";
		String expectedMessage = "Error consuming message for topic '{}' from queue '{}', error: {}";
		assertEquals(expectedMessage, loggingEvent.getMessage());
		Object[] args = loggingEvent.getArgumentArray();
		assertArrayEquals(new Object[]{expectedTopic, queueName, exception.getMessage()}, args);
	}

	@Test
	void testHandleError_WithOtherThrowable() {
		String errorMessage = "General error message";
		Exception exception = new Exception(errorMessage);
		errorHandler.handleError(exception);
		var logsList = listAppender.list;
		assertEquals(1, logsList.size(), "Should log one error message");
		var loggingEvent = logsList.get(0);
		assertEquals(ch.qos.logback.classic.Level.ERROR, loggingEvent.getLevel());
		String expectedMessage = "Error in message listener: {}";
		assertEquals(expectedMessage, loggingEvent.getMessage());
		Object[] args = loggingEvent.getArgumentArray();
		assertArrayEquals(new Object[]{errorMessage}, args);
	}
}
