package com.linkopus.ms.services;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ErrorBody;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class SupervisionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupervisionService.class);
	private final RabbitTemplate rabbitTemplate;
	private final MongoTemplate mongoTemplate;

	@Value("${log.file.directory}")
	private String logDirectory;

	public File downloadLogFile(String date) throws ApiError {
		String logPath = logDirectory + "/" + date + ".log";
		File file = new File(logPath);
		if (!file.exists()) {
			throw new ApiError(new ErrorBody(HttpStatus.NOT_FOUND, ErrorTypes.LOG_FILE_NOT_FOUND,
					"Log file not found for date: " + date), LOGGER);
		}

		return file;
	}

	public SupervisionService(RabbitTemplate rabbitTemplate, MongoTemplate mongoTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		this.mongoTemplate = mongoTemplate;
	}

	public boolean mongoDbConnectivity() {
		try {
			mongoTemplate.executeCommand("{ ping: 1 }");
			LOGGER.info("Successfully connected to MongoDB server.");
			return true;
		} catch (Exception e) {
			LOGGER.error("Failed to connect to MongoDB server: {}", e.getMessage());
			return false;
		}
	}

	private Optional<String> getClusterName() {
		return Optional.ofNullable(rabbitTemplate.execute(channel -> {
			try {
				return channel.getConnection().getServerProperties().get("cluster_name").toString();
			} catch (Exception e) {
				LOGGER.error("Error retrieving cluster name: {}", e.getMessage(), e);
				return null;
			}
		}));
	}

	public boolean rabbitMqConnectivity() {
		try {
			Optional<String> response = getClusterName();
			if (response.isPresent()) {
				LOGGER.info("Successfully connected to RabbitMQ server. Cluster name: {}", response.get());
				return true;
			} else {
				LOGGER.warn("RabbitMQ server connection established but failed to retrieve cluster name.");
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Failed to connect to RabbitMQ server: {}", e.getMessage());
			return false;
		}
	}

	public boolean redisConnectivity() {
		return false;
	}
}
