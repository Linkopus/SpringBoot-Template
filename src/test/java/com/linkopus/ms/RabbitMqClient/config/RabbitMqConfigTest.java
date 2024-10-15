package com.linkopus.ms.RabbitMqClient.config;

import com.linkopus.ms.RabbitMqClient.consumer.GlobalRabbitMqErrorHandler;
import com.linkopus.ms.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMqConfigTest {

	private Config mockConfig;
	private RabbitMqConfig rabbitMqConfig;

	@BeforeEach
	void setUp() throws Exception {
		mockConfig = mock(Config.class);
		when(mockConfig.getRabbitMqUrl()).thenReturn("amqp://guest:guest@localhost:5672/");
		when(mockConfig.getRabbitMqClientCertPath())
				.thenReturn("-----BEGIN CERTIFICATE-----\nMIID...==\n-----END CERTIFICATE-----");
		when(mockConfig.getRabbitMqClientKeyPath())
				.thenReturn("-----BEGIN ENCRYPTED PRIVATE KEY-----\nMIIE...==\n-----END ENCRYPTED PRIVATE KEY-----");
		when(mockConfig.getRabbitMqCertPassword()).thenReturn("dummyPassword");
		when(mockConfig.getRabbitMqCaCertPath())
				.thenReturn("-----BEGIN CERTIFICATE-----\nMIID...==\n-----END CERTIFICATE-----");

		SSLContext mockSslContext = SSLContext.getDefault();

		rabbitMqConfig = Mockito.spy(new RabbitMqConfig(mockConfig));
		doReturn(mockSslContext).when(rabbitMqConfig).createSslContext();
	}

	@Test
	void testMessageConverter() {
		Jackson2JsonMessageConverter converter = rabbitMqConfig.messageConverter();
		assertNotNull(converter, "Message converter should not be null");
	}

	@Test
	void testConnectionFactory() throws Exception {
		ConnectionFactory connectionFactory = rabbitMqConfig.connectionFactory();
		assertNotNull(connectionFactory, "ConnectionFactory should not be null");

		verify(mockConfig, times(1)).getRabbitMqUrl();
	}

	@Test
	void testGlobalRabbitMqErrorHandler() {
		GlobalRabbitMqErrorHandler errorHandler = rabbitMqConfig.globalRabbitMqErrorHandler();
		assertNotNull(errorHandler, "GlobalRabbitMqErrorHandler should not be null");
	}

	@Test
	void testListenerContainerFactory() throws Exception {
		ConnectionFactory connectionFactory = rabbitMqConfig.connectionFactory();
		GlobalRabbitMqErrorHandler errorHandler = rabbitMqConfig.globalRabbitMqErrorHandler();
		RabbitListenerContainerFactory<SimpleMessageListenerContainer> factory = rabbitMqConfig
				.listenerContainerFactory(connectionFactory, errorHandler);
		assertNotNull(factory, "ListenerContainerFactory should not be null");

		assertInstanceOf(SimpleRabbitListenerContainerFactory.class, factory,
				"Factory should be an instance of SimpleRabbitListenerContainerFactory");

		SimpleRabbitListenerContainerFactory simpleFactory = (SimpleRabbitListenerContainerFactory) factory;
		SimpleMessageListenerContainer container = simpleFactory.createListenerContainer();
		assertNotNull(container, "Listener container should not be null");
	}

	@Test
	void testRabbitAdmin() throws Exception {
		ConnectionFactory connectionFactory = rabbitMqConfig.connectionFactory();
		RabbitAdmin rabbitAdmin = rabbitMqConfig.rabbitAdmin(connectionFactory);
		assertNotNull(rabbitAdmin, "RabbitAdmin should not be null");
	}

	@Test
	void testCreateSslContext() throws Exception {
		SSLContext sslContext = rabbitMqConfig.createSslContext();
		assertNotNull(sslContext, "SSLContext should not be null");

		rabbitMqConfig.connectionFactory();
		verify(rabbitMqConfig, atLeastOnce()).createSslContext();
	}
}
