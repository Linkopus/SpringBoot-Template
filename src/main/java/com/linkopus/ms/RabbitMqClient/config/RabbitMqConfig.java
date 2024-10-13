package com.linkopus.ms.RabbitMqClient.config;

import com.linkopus.ms.RabbitMqClient.consumer.GlobalRabbitMqErrorHandler;
import com.linkopus.ms.config.Config;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
	private final Config config;

	@Autowired
	public RabbitMqConfig(Config config) {
		this.config = config;
	}

	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public ConnectionFactory connectionFactory() throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setUri(config.getRabbitMqUrl());
		SSLContext sslContext = createSslContext();
		connectionFactory.getRabbitConnectionFactory().useSslProtocol(sslContext);

		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}

	@Bean
	public GlobalRabbitMqErrorHandler globalRabbitMqErrorHandler() {
		return new GlobalRabbitMqErrorHandler();
	}

	@Bean
	public RabbitListenerContainerFactory<SimpleMessageListenerContainer> listenerContainerFactory(
			ConnectionFactory connectionFactory, GlobalRabbitMqErrorHandler globalErrorHandler) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setErrorHandler(globalErrorHandler);
		factory.setMessageConverter(messageConverter());
		factory.setAcknowledgeMode(AcknowledgeMode.NONE);

		return factory;
	}

	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	SSLContext createSslContext() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);

		String clientCert = config.getRabbitMqClientCertPath();
		String clientKey = config.getRabbitMqClientKeyPath();
		String certPassword = config.getRabbitMqCertPassword();

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(clientCert.getBytes()));

		String privateKeyPEM = clientKey.replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "")
				.replaceAll(System.lineSeparator(), "").replace("-----END ENCRYPTED PRIVATE KEY-----", "");

		byte[] encryptedPrivateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);

		EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKeyBytes);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(certPassword.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
		Key pbeKey = secretKeyFactory.generateSecret(pbeKeySpec);

		Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());

		PKCS8EncodedKeySpec keySpec = encryptedPrivateKeyInfo.getKeySpec(cipher);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		keyStore.setKeyEntry("client", privateKey, certPassword.toCharArray(), new X509Certificate[]{cert});

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, certPassword.toCharArray());

		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(null, null);

		String caCert = config.getRabbitMqCaCertPath();
		X509Certificate caCertificate = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(caCert.getBytes()));
		trustStore.setCertificateEntry("ca", caCertificate);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustStore);

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return sslContext;
	}
}
