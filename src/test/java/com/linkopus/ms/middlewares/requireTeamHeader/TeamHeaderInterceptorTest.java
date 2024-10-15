package com.linkopus.ms.middlewares.requireTeamHeader;

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

class TeamHeaderInterceptorTest {

	private final TeamHeaderInterceptor interceptor = new TeamHeaderInterceptor();

	@Test
	void preHandle_HandlerNotHandlerMethod_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Object handler = new Object(); // Not a HandlerMethod

		boolean result = interceptor.preHandle(request, response, handler);

		assertTrue(result, "Interceptor should return true when handler is not an instance of HandlerMethod.");
	}

	@Test
	void preHandle_NoRequiredTeamHeaderAnnotation_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = NoAnnotationController.class.getMethod("someMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new NoAnnotationController(), method);

		boolean result = interceptor.preHandle(request, response, handlerMethod);

		assertTrue(result, "Interceptor should return true when @RequiredTeamHeader is not present.");
	}

	@Test
	void preHandle_MissingTeamHeader_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.TEAM)).thenReturn(null);

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.TEAM_HEADER_IS_REQUIRED, exception.getErrorBody().getName());
		assertEquals("Missing `team` header in request.", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_EmptyTeamHeader_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.TEAM)).thenReturn("");

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.TEAM_HEADER_IS_REQUIRED, exception.getErrorBody().getName());
		assertEquals("Missing `team` header in request.", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_ValidTeamHeader_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.TEAM)).thenReturn("Team123");

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		boolean result = interceptor.preHandle(request, response, handlerMethod);

		assertTrue(result, "Interceptor should return true when team header is present.");
	}

	static class NoAnnotationController {
		public void someMethod() {
		}
	}

	@RequiredTeamHeader
	static class AnnotatedController {
		public void annotatedMethod() {
		}
	}
}
