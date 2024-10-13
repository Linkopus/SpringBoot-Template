package com.linkopus.ms.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import com.linkopus.ms.RabbitMqClient.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = {ConnectivityCheckConfig.class})
@TestPropertySource(properties = {"connectivity-check.check-mongo-db=true", "connectivity-check.check-rabbit-mq=false",
		"connectivity-check.check-redis=true"})
@EnableConfigurationProperties(ConnectivityCheckConfig.class)
class ConnectivityCheckConfigTest {

	@Autowired
	private ConnectivityCheckConfig connectivityCheckConfig;

	@MockBean
	private RabbitMqConfig rabbitMqConfig;

	@MockBean
	private ConnectionFactory connectionFactory;

	@Test
	void testCheckMongoDb() {
		assertTrue(connectivityCheckConfig.isCheckMongoDb());
	}

	@Test
	void testCheckRabbitMq() {
		assertFalse(connectivityCheckConfig.isCheckRabbitMq());
	}

	@Test
	void testCheckRedis() {
		assertTrue(connectivityCheckConfig.isCheckRedis());
	}
}