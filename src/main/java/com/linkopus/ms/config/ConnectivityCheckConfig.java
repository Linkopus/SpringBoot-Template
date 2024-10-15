package com.linkopus.ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "connectivity-check")
public class ConnectivityCheckConfig {

	private boolean checkMongoDb;
	private boolean checkRabbitMq;
	private boolean checkRedis;

	public boolean isCheckMongoDb() {
		return checkMongoDb;
	}

	public void setCheckMongoDb(boolean checkMongoDb) {
		this.checkMongoDb = checkMongoDb;
	}

	public boolean isCheckRabbitMq() {
		return checkRabbitMq;
	}

	public void setCheckRabbitMq(boolean checkRabbitMq) {
		this.checkRabbitMq = checkRabbitMq;
	}

	public boolean isCheckRedis() {
		return checkRedis;
	}

	public void setCheckRedis(boolean checkRedis) {
		this.checkRedis = checkRedis;
	}
}
