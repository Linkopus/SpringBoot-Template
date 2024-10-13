package com.linkopus.ms.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class SupervisionServiceTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private SupervisionService supervisionService;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(supervisionService, "logDirectory", tempDir.toString());
	}

	@Test
	void testDownloadLogFile_Success() throws IOException, ApiError {
		String date = "2023-05-01";
		Path logFile = tempDir.resolve(date + ".log");
		Files.createFile(logFile);

		File result = supervisionService.downloadLogFile(date);

		assertNotNull(result);
		assertTrue(result.exists());
		assertEquals(logFile.toString(), result.getPath());
	}

	@Test
	void testDownloadLogFile_FileNotFound() {
		String date = "2023-05-01";

		ApiError exception = assertThrows(ApiError.class, () -> supervisionService.downloadLogFile(date));

		assertEquals(ErrorTypes.LOG_FILE_NOT_FOUND, exception.getErrorBody().getName());
	}

	@Test
	void testMongoDbConnectivity_Success() {
		when(mongoTemplate.executeCommand(anyString())).thenReturn(null);

		boolean result = supervisionService.mongoDbConnectivity();

		assertTrue(result);
		verify(mongoTemplate).executeCommand("{ ping: 1 }");
	}

	@Test
	void testMongoDbConnectivity_Failure() {
		when(mongoTemplate.executeCommand(anyString())).thenThrow(new RuntimeException("Connection failed"));

		boolean result = supervisionService.mongoDbConnectivity();

		assertFalse(result);
		verify(mongoTemplate).executeCommand("{ ping: 1 }");
	}

	@Test
	void testRabbitMqConnectivity_Success() {
		when(rabbitTemplate.execute(any())).thenReturn("test-cluster");

		boolean result = supervisionService.rabbitMqConnectivity();

		assertTrue(result);
	}

	@Test
	void testRabbitMqConnectivity_FailureToRetrieveClusterName() {
		when(rabbitTemplate.execute(any())).thenReturn(null);

		boolean result = supervisionService.rabbitMqConnectivity();

		assertFalse(result);
	}

	@Test
	void testRabbitMqConnectivity_Exception() {
		when(rabbitTemplate.execute(any())).thenThrow(new RuntimeException("Connection failed"));

		boolean result = supervisionService.rabbitMqConnectivity();

		assertFalse(result);
	}

	@Test
	void testRedisConnectivity() {
		boolean result = supervisionService.redisConnectivity();

		assertFalse(result);
	}
}