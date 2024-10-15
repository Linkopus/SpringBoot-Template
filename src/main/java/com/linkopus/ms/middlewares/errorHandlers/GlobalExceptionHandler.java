package com.linkopus.ms.middlewares.errorHandlers;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiError.class)
	public ResponseEntity<Object> handleApiError(ApiError ex) {
		Map<String, Object> errorResponse = new HashMap<>();
		Map<String, String> errorDetails = new HashMap<>();

		errorDetails.put("name", ex.getErrorBody().getName().toString());
		errorDetails.put("details", ex.getErrorBody().getDetails());
		errorResponse.put("error", errorDetails);

		return new ResponseEntity<>(errorResponse, ex.getErrorBody().getStatus());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
		Map<String, Object> errorResponse = new HashMap<>();
		Map<String, String> errorDetails = new HashMap<>();

		errorDetails.put("name", ErrorTypes.ROUTE_NOT_FOUND.toString());
		errorDetails.put("details", "Can't find " + ex.getRequestURL() + " on the server!");
		errorResponse.put("error", errorDetails);

		LOGGER.error("404 ROUTE_NOT_FOUND: Can't find {} on the server!", ex.getRequestURL());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedError(Exception ex, HttpServletRequest request) {
		Map<String, Object> errorResponse = new HashMap<>();
		Map<String, String> errorDetails = new HashMap<>();

		errorDetails.put("name", ErrorTypes.UNEXPECTED_ERROR.toString());
		errorDetails.put("details", ex.getMessage());
		errorResponse.put("error", errorDetails);

		LOGGER.error("500 UNEXPECTED_ERROR: {}", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
