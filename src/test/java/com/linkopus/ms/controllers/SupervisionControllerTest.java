package com.linkopus.ms.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.linkopus.ms.config.ConnectivityCheckConfig;
import com.linkopus.ms.models.supervision.HealthStatus;
import com.linkopus.ms.services.SupervisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Map;

class SupervisionControllerTest {

	@Mock
	private SupervisionService supervisionService;

	@Mock
	private ConnectivityCheckConfig connectivityCheckConfig;

	@InjectMocks
	private SupervisionController supervisionController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCheckHealth() {
		ResponseEntity<Object> response = supervisionController.checkHealth();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertInstanceOf(HealthStatus.class, response.getBody());
		HealthStatus healthStatus = (HealthStatus) response.getBody();
		assertEquals("Server is UP", healthStatus.getMessage());
	}

	@Test
	void testCheckConnectivity() {
		when(connectivityCheckConfig.isCheckMongoDb()).thenReturn(true);
		when(connectivityCheckConfig.isCheckRabbitMq()).thenReturn(true);
		when(connectivityCheckConfig.isCheckRedis()).thenReturn(true);

		when(supervisionService.mongoDbConnectivity()).thenReturn(true);
		when(supervisionService.rabbitMqConnectivity()).thenReturn(false);
		when(supervisionService.redisConnectivity()).thenReturn(true);

		ResponseEntity<Map<String, Boolean>> response = supervisionController.checkConnectivity();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(3, response.getBody().size());
		assertTrue(response.getBody().get("mongoDb"));
		assertFalse(response.getBody().get("rabbitMq"));
		assertTrue(response.getBody().get("redis"));
	}

	@Test
	void testGetLogFile() throws Exception {
		String date = "2023-05-01";
		File mockFile = mock(File.class);
		when(mockFile.getName()).thenReturn("log-2023-05-01.txt");
		when(supervisionService.downloadLogFile(date)).thenReturn(mockFile);

		ResponseEntity<?> response = supervisionController.getLogFile(date);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertInstanceOf(Resource.class, response.getBody());
		assertNotNull(response.getHeaders().getContentDisposition());
		assertTrue(response.getHeaders().getContentDisposition().toString().contains("log-2023-05-01.txt"));
	}
}