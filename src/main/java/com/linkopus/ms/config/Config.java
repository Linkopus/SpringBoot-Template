package com.linkopus.ms.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {
	private final Dotenv dotenv;

	@Value("${identity:default_identity}")
	String identity;

	public Config(Dotenv dotenv) {
		this.dotenv = dotenv;
	}

	public Config() {
		dotenv = Dotenv.load();
	}

	public String getPort() {
		return dotenv.get("PORT", "3000");
	}

	public String getMongodbUri() {
		String uri = dotenv.get("MONGODB_URI", "mongodb://localhost:27017");

		if (uri.endsWith("/")) {
			return uri.substring(0, uri.length() - 1);
		}
		String[] parts = uri.split("/");
		if (parts.length > 3) {
			return String.join("/", parts[0], parts[1], parts[2]);
		}
		return uri;
	}

	public String getMongodbDatabaseName() {
		String uri = dotenv.get("MONGODB_URI", "mongodb://localhost:27017/defaultDb");
		String[] parts = uri.split("/");
		if (parts.length > 3) {
			String lastPart = parts[parts.length - 1];
			return lastPart.split("\\?")[0];
		}
		return "defaultDb";
	}

	public String getRabbitMqUrl() {
		return dotenv.get("RABBITMQ_URL", "localhost");
	}

	public String getRabbitMqClientCertPath() {
		return dotenv.get("RABBITMQ_CLIENT_CERTIFICATE", "");
	}

	public String getRabbitMqClientKeyPath() {
		return dotenv.get("RABBITMQ_CLIENT_PRIVATE_KEY", "");
	}

	public String getRabbitMqCaCertPath() {
		return dotenv.get("RABBITMQ_CA_CERTIFICATE", "");
	}

	public String getRabbitMqCertPassword() {
		return dotenv.get("RABBITMQ_CERT_PASSWORD", "");
	}

	public String getIdentity() {
		return identity;
	}
}