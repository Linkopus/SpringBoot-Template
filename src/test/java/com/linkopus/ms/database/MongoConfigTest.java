package com.linkopus.ms.database;

import com.linkopus.ms.config.Config;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.ConnectionPoolClearedEvent;
import com.mongodb.event.ConnectionPoolReadyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoConfigTest {

	private MongoConfig mongoConfig;
	private static final String TEST_MONGODB_URI = "mongodb://testuser:testpass@localhost:27017/testdb";
	private static final String TEST_DB_NAME = "testdb";
	private Config mockConfig;

	@BeforeEach
	void setUp() {
		mockConfig = mock(Config.class);
		when(mockConfig.getMongodbUri()).thenReturn(TEST_MONGODB_URI);
		when(mockConfig.getMongodbDatabaseName()).thenReturn(TEST_DB_NAME);
		mongoConfig = new MongoConfig(mockConfig);
	}

	@Test
	void testMongoClient() {
		try (MockedStatic<MongoClients> mockedMongoClients = mockStatic(MongoClients.class)) {
			MongoClient mockClient = mock(MongoClient.class);
			mockedMongoClients.when(() -> MongoClients.create(any(MongoClientSettings.class))).thenReturn(mockClient);

			MongoClient client = mongoConfig.mongoClient();
			assertNotNull(client, "MongoClient should not be null");
			assertEquals(mockClient, client, "MongoClient should be the mocked instance");
		}
	}

	@Test
	void testMongoTemplate() {
		MongoClient mockMongoClient = mock(MongoClient.class);
		MongoDatabase mockDatabase = mock(MongoDatabase.class);
		when(mockMongoClient.getDatabase(TEST_DB_NAME)).thenReturn(mockDatabase);
		when(mockDatabase.getName()).thenReturn(TEST_DB_NAME);

		MongoTemplate template = mongoConfig.mongoTemplate(mockMongoClient);

		assertNotNull(template, "MongoTemplate should not be null");
		assertEquals(TEST_DB_NAME, template.getDb().getName(), "Database name should match");
	}

	@Test
	void testCustomConnectionPoolListener() {
		MongoConfig.CustomConnectionPoolListener listener = new MongoConfig.CustomConnectionPoolListener();

		ConnectionPoolReadyEvent readyEvent = mock(ConnectionPoolReadyEvent.class);
		ConnectionPoolClearedEvent clearedEvent = mock(ConnectionPoolClearedEvent.class);

		listener.connectionPoolReady(readyEvent);
		listener.connectionPoolReady(readyEvent);
		listener.connectionPoolCleared(clearedEvent);
	}
}
