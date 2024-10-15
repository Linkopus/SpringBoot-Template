package com.linkopus.ms.controllers;

import com.linkopus.ms.config.ConnectivityCheckConfig;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.middlewares.requireEmailHeader.RequiredEmailHeader;
import com.linkopus.ms.middlewares.roleGuard.RoleGuard;
import com.linkopus.ms.services.SupervisionService;
import com.linkopus.ms.utils.ressources.enums.UserRole;
import com.linkopus.ms.models.supervision.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RoleGuard(roles = {UserRole.ADMIN})
@RequiredEmailHeader
@RequestMapping("/supervision")
public class SupervisionController {

	@Autowired
	private SupervisionService supervisionService;

	@Autowired
	private ConnectivityCheckConfig connectivityCheckConfig;

	private static final Logger LOGGER = LoggerFactory.getLogger(SupervisionController.class);

	@GetMapping("/health")
	public ResponseEntity<Object> checkHealth() {
		LOGGER.info("Checking server health...");
		return new ResponseEntity<>(new HealthStatus("Server is UP"), HttpStatus.OK);
	}

	@GetMapping("/connectivity")
	public ResponseEntity<Map<String, Boolean>> checkConnectivity() {
		LOGGER.info("Starting connectivity check...");

		Map<String, Boolean> connectivityChecksResult = new HashMap<>();

		if (connectivityCheckConfig.isCheckMongoDb()) {
			LOGGER.info("Checking MongoDB connection...");
			boolean isMongoConnected = supervisionService.mongoDbConnectivity();
			connectivityChecksResult.put("mongoDb", isMongoConnected);
			if (isMongoConnected) {
				LOGGER.info("MongoDB is connected.");
			} else {
				LOGGER.error("MongoDB is not connected.");
			}
		}

		if (connectivityCheckConfig.isCheckRabbitMq()) {
			LOGGER.info("Checking RabbitMQ connection...");
			boolean isRabbitMqConnected = supervisionService.rabbitMqConnectivity();
			connectivityChecksResult.put("rabbitMq", isRabbitMqConnected);
			if (isRabbitMqConnected) {
				LOGGER.info("RabbitMQ is connected.");
			} else {
				LOGGER.error("RabbitMQ is not connected.");
			}
		}

		if (connectivityCheckConfig.isCheckRedis()) {
			LOGGER.info("Checking Redis connection...");
			boolean isRedisConnected = supervisionService.redisConnectivity();
			connectivityChecksResult.put("redis", isRedisConnected);
			if (isRedisConnected) {
				LOGGER.info("Redis is connected.");
			} else {
				LOGGER.error("Redis is not connected.");
			}
		}

		LOGGER.info("Connectivity check result: {}", connectivityChecksResult);
		return ResponseEntity.ok(connectivityChecksResult);
	}

	@GetMapping("/logs/{date}")
	public ResponseEntity<?> getLogFile(@PathVariable String date) throws ApiError {
		File file = supervisionService.downloadLogFile(date);
		Resource resource = new FileSystemResource(file);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
		return ResponseEntity.ok().headers(headers).body(resource);
	}
}
