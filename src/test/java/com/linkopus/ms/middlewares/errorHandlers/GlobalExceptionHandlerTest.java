package com.linkopus.ms.middlewares.errorHandlers;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ErrorBody;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler exceptionHandler;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		exceptionHandler = new GlobalExceptionHandler();
	}

	@Test
	void testHandleApiError() {
		ErrorBody errorBody = new ErrorBody(HttpStatus.BAD_REQUEST, ErrorTypes.INVALID_EMAIL_FORMAT,
				"Invalid email format");
		Logger mockLogger = mock(Logger.class);
		ApiError apiError = new ApiError(errorBody, mockLogger);

		ResponseEntity<Object> response = exceptionHandler.handleApiError(apiError);

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		Map<String, Object> body = (Map<String, Object>) response.getBody();
		assertNotNull(body);
		Map<String, String> error = (Map<String, String>) body.get("error");
		assertNotNull(error);
		assertEquals(ErrorTypes.INVALID_EMAIL_FORMAT.toString(), error.get("name"));
		assertEquals("Invalid email format", error.get("details"));

		String expectedLogMessage = "400 INVALID_EMAIL_FORMAT: Invalid email format";
		verify(mockLogger).error(expectedLogMessage);
	}

	@Test
	void testHandleNoHandlerFoundException() {
		NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/nonexistent", null);

		ResponseEntity<Object> response = exceptionHandler.handleNoHandlerFoundException(ex);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

		Map<String, Object> body = (Map<String, Object>) response.getBody();
		assertNotNull(body);
		Map<String, String> error = (Map<String, String>) body.get("error");
		assertNotNull(error);
		assertEquals(ErrorTypes.ROUTE_NOT_FOUND.toString(), error.get("name"));
		assertEquals("Can't find /nonexistent on the server!", error.get("details"));

	}

	@Test
	void testHandleUnexpectedError() {
		Exception ex = new Exception("Unexpected exception");
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getRequestURI()).thenReturn("/test");

		ResponseEntity<Object> response = exceptionHandler.handleUnexpectedError(ex, mockRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

		Map<String, Object> body = (Map<String, Object>) response.getBody();
		assertNotNull(body);
		Map<String, String> error = (Map<String, String>) body.get("error");
		assertNotNull(error);
		assertEquals(ErrorTypes.UNEXPECTED_ERROR.toString(), error.get("name"));
		assertEquals("Unexpected exception", error.get("details"));

	}
}
