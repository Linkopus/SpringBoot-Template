package com.linkopus.ms.models.supervision;

import io.swagger.v3.oas.annotations.media.Schema;

public class ConnectivityStatus {
	@Schema(description = "MongoDB connection status")
	private Boolean mongoDb;

	@Schema(description = "RabbitMQ connection status")
	private Boolean rabbitMq;

	@Schema(description = "Redis connection status")
	private Boolean redis;
}
