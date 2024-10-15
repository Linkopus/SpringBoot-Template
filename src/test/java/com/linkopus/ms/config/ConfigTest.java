package com.linkopus.ms.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ConfigTest {

	@Mock
	private Dotenv dotenvMock;

	private Config config;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		config = new Config(dotenvMock);
	}

	@Test
	void getPort_shouldReturnPortFromEnv() {
		when(dotenvMock.get("PORT", "3000")).thenReturn("8080");
		String port = config.getPort();
		assertEquals("8080", port);
	}

	@Test
	void getPort_shouldReturnDefaultPortIfEnvNotSet() {
		when(dotenvMock.get("PORT", "3000")).thenReturn("3000");
		String port = config.getPort();
		assertEquals("3000", port);
	}

	@Test
	void getMongodbUri_shouldReturnMongodbUriFromEnv() {
		when(dotenvMock.get("MONGODB_URI", "mongodb://localhost:27017")).thenReturn("mongodb://remotehost:27017/myDb");
		String uri = config.getMongodbUri();
		assertEquals("mongodb://remotehost:27017", uri);
	}

	@Test
	void getMongodbUri_shouldReturnDefaultMongodbUriIfEnvNotSet() {
		when(dotenvMock.get("MONGODB_URI", "mongodb://localhost:27017")).thenReturn("mongodb://localhost:27017");
		String uri = config.getMongodbUri();
		assertEquals("mongodb://localhost:27017", uri);
	}

	@Test
	void getMongodbDatabaseName_shouldReturnDatabaseNameFromEnv() {
		when(dotenvMock.get("MONGODB_URI", "mongodb://localhost:27017/defaultDb"))
				.thenReturn("mongodb://remotehost:27017/myDb");
		String dbName = config.getMongodbDatabaseName();
		assertEquals("myDb", dbName);
	}

	@Test
	void getMongodbDatabaseName_shouldReturnDefaultDatabaseNameIfEnvNotSet() {
		when(dotenvMock.get("MONGODB_URI", "mongodb://localhost:27017/defaultDb"))
				.thenReturn("mongodb://localhost:27017/defaultDb");
		String dbName = config.getMongodbDatabaseName();
		assertEquals("defaultDb", dbName);
	}

	@Test
	void getRabbitMqUrl_shouldReturnRabbitMqUrlFromEnv() {
		when(dotenvMock.get("RABBITMQ_URL", "localhost")).thenReturn("rabbitmq://remotehost");
		String rabbitMqUrl = config.getRabbitMqUrl();
		assertEquals("rabbitmq://remotehost", rabbitMqUrl);
	}

	@Test
	void getRabbitMqUrl_shouldReturnDefaultRabbitMqUrlIfEnvNotSet() {
		when(dotenvMock.get("RABBITMQ_URL", "localhost")).thenReturn("localhost");
		String rabbitMqUrl = config.getRabbitMqUrl();
		assertEquals("localhost", rabbitMqUrl);
	}

	@Test
	void getRabbitMqClientCertPath_shouldReturnClientCertPathFromEnv() {
		when(dotenvMock.get("RABBITMQ_CLIENT_CERTIFICATE", "")).thenReturn("/path/to/cert");
		String certPath = config.getRabbitMqClientCertPath();
		assertEquals("/path/to/cert", certPath);
	}

	@Test
	void getRabbitMqClientKeyPath_shouldReturnClientKeyPathFromEnv() {
		when(dotenvMock.get("RABBITMQ_CLIENT_PRIVATE_KEY", "")).thenReturn("/path/to/key");
		String keyPath = config.getRabbitMqClientKeyPath();
		assertEquals("/path/to/key", keyPath);
	}

	@Test
	void getRabbitMqCaCertPath_shouldReturnCaCertPathFromEnv() {
		when(dotenvMock.get("RABBITMQ_CA_CERTIFICATE", "")).thenReturn("/path/to/ca-cert");
		String caCertPath = config.getRabbitMqCaCertPath();
		assertEquals("/path/to/ca-cert", caCertPath);
	}

	@Test
	void getRabbitMqCertPassword_shouldReturnCertPasswordFromEnv() {
		when(dotenvMock.get("RABBITMQ_CERT_PASSWORD", "")).thenReturn("password123");
		String certPassword = config.getRabbitMqCertPassword();
		assertEquals("password123", certPassword);
	}

	@Test
	void getIdentity_shouldReturnIdentity() {
		Config configWithIdentity = new Config(dotenvMock);
		configWithIdentity.identity = "my_identity"; // Mocking @Value injection
		assertEquals("my_identity", configWithIdentity.getIdentity());
	}
}
