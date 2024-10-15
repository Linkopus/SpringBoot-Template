package com.linkopus.ms.database;

import com.linkopus.ms.config.Config;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.event.ConnectionPoolClearedEvent;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ConnectionPoolReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfig.class);
	private final Config config;

	public MongoConfig(Config config) {
		this.config = config;
	}

	static class CustomConnectionPoolListener implements ConnectionPoolListener {
		private boolean isInitialConnection = true;

		@Override
		public void connectionPoolCleared(@NotNull ConnectionPoolClearedEvent event) {
			LOGGER.warn("MongoDB connection lost. Reconnecting...");
		}

		@Override
		public void connectionPoolReady(@NotNull ConnectionPoolReadyEvent event) {
			if (isInitialConnection) {
				LOGGER.info("MongoDB connection successfully established.");
				isInitialConnection = false;
			} else {
				LOGGER.info("MongoDB reconnected successfully.");
			}
		}
	}

	@Bean
	public MongoClient mongoClient() {
		ConnectionPoolSettings connectionPoolSettings = ConnectionPoolSettings.builder()
				.addConnectionPoolListener(new CustomConnectionPoolListener()).build();

		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(config.getMongodbUri()))
				.applyToConnectionPoolSettings(builder -> builder.applySettings(connectionPoolSettings))
				.applyToSocketSettings(
						builder -> builder.connectTimeout(8, TimeUnit.SECONDS).readTimeout(8, TimeUnit.SECONDS))
				.applyToClusterSettings(builder -> builder.serverSelectionTimeout(8, TimeUnit.SECONDS)).build();

		return MongoClients.create(settings);
	}

	@Bean
	public MongoTemplate mongoTemplate(MongoClient mongoClient) {
		return new MongoTemplate(mongoClient, config.getMongodbDatabaseName());
	}
}
