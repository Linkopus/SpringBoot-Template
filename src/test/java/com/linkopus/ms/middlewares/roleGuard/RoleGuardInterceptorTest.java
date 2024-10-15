package com.linkopus.ms.middlewares.roleGuard;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.utils.ressources.constants.Headers;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import com.linkopus.ms.utils.ressources.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleGuardInterceptorTest {

	private final RoleGuardInterceptor interceptor = new RoleGuardInterceptor();

	@Test
	void preHandle_HandlerNotHandlerMethod_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Object handler = new Object();

		boolean result = interceptor.preHandle(request, response, handler);

		assertTrue(result, "Interceptor should return true when handler is not an instance of HandlerMethod.");
	}

	@Test
	void preHandle_NoRoleGuardAnnotation_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = NoAnnotationController.class.getMethod("someMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new NoAnnotationController(), method);

		boolean result = interceptor.preHandle(request, response, handlerMethod);

		assertTrue(result, "Interceptor should return true when @RoleGuard is not present.");
	}

	@Test
	void preHandle_MissingRoleHeader_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.ROLE)).thenReturn(null);

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.ROLE_HEADER_IS_REQUIRED, exception.getErrorBody().getName());
		assertEquals("Role header is required", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_EmptyRoleHeader_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.ROLE)).thenReturn("");

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.ROLE_HEADER_IS_REQUIRED, exception.getErrorBody().getName());
		assertEquals("Role header is required", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_InvalidRoleHeader_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.ROLE)).thenReturn("INVALID_ROLE");

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.INVALID_ROLE, exception.getErrorBody().getName());
		assertEquals("Invalid role provided: INVALID_ROLE", exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_RoleNotInAllowedRoles_ShouldThrowApiError() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.ROLE)).thenReturn(UserRole.USER.name());

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		ApiError exception = assertThrows(ApiError.class, () -> {
			interceptor.preHandle(request, response, handlerMethod);
		});

		assertEquals(ErrorTypes.NOT_REQUIRED_ROLE, exception.getErrorBody().getName());
		assertEquals("Access denied. User role 'USER' does not have permission.",
				exception.getErrorBody().getDetails());
	}

	@Test
	void preHandle_RoleInAllowedRoles_ShouldReturnTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.ROLE)).thenReturn(UserRole.ADMIN.name());

		HttpServletResponse response = mock(HttpServletResponse.class);
		Method method = AnnotatedController.class.getMethod("annotatedMethod");
		HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

		boolean result = interceptor.preHandle(request, response, handlerMethod);

		assertTrue(result, "Interceptor should return true when user role is allowed.");
	}

	static class NoAnnotationController {
		public void someMethod() {
		}
	}

	@RoleGuard(roles = {UserRole.ADMIN})
	static class AnnotatedController {
		public void annotatedMethod() {
		}
	}
}
