package com.linkopus.ms.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {
	private final Config config;

	public ServerConfig(Config config) {
		this.config = config;
	}

	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
		return factory -> factory.setPort(Integer.parseInt(config.getPort()));
	}
}
