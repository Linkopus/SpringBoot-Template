package com.linkopus.ms.middlewares.requireEmailHeader;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.utils.ressources.constants.Headers;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailHeaderInterceptorTest {

	private final EmailHeaderInterceptor interceptor = new EmailHeaderInterceptor();

	@Test
	void preHandle_HandlerNotHandlerMethod_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Object handler = new Object();

		boolean result = interceptor.preHandle(request, response, handler);

		assertTrue(result, "Interceptor should return true when handler is not an instance of HandlerMethod.");
	}

	@Test
	void preHandle_NoRequiredEmailHeaderAnnotation_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = NoAnnotationController.class.getMethod("someMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new NoAnnotationController(), method);

		boolean result = interceptor.preHandle(request, response, handlerMethod);

		assertTrue(result, "Interceptor should return true when @RequiredEmailHeader is not present.");
	}

	@Test
	void preHandle_MissingEmailHeader_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.EMAIL)).thenReturn(null);

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.EMAIL_HEADER_IS_REQUIRED, exception.getErrorBody().getName());
		assertEquals("Missing `email` header in request.", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_InvalidEmailHeaderFormat_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.EMAIL)).thenReturn("invalid-email");

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.INVALID_EMAIL_FORMAT, exception.getErrorBody().getName());
		assertEquals("The email header format is invalid.", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_ValidEmailHeader_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.EMAIL)).thenReturn("test@example.com");

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		boolean result = interceptor.preHandle(request, response, handlerMethod);

		assertTrue(result, "Interceptor should return true when email header is valid.");
	}

	static class NoAnnotationController {
		public void someMethod() {
		}
	}

	@RequiredEmailHeader
	static class AnnotatedController {
		public void annotatedMethod() {
		}
	}
}
