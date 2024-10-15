package com.linkopus.ms.middlewares.roleGuard;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ErrorBody;
import com.linkopus.ms.utils.ressources.constants.Headers;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import com.linkopus.ms.utils.ressources.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleGuardInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleGuardInterceptor.class);

	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler) throws Exception {
		if (handler instanceof HandlerMethod handlerMethod) {
			RoleGuard roleGuard = handlerMethod.getMethodAnnotation(RoleGuard.class);

			if (roleGuard == null) {
				roleGuard = handlerMethod.getBeanType().getAnnotation(RoleGuard.class);
			}

			if (roleGuard != null) {
				String roleHeader = request.getHeader(Headers.ROLE);

				if (roleHeader == null || roleHeader.isEmpty()) {
					throw new ApiError(new ErrorBody(HttpStatus.UNAUTHORIZED, ErrorTypes.ROLE_HEADER_IS_REQUIRED,
							"Role header is required"), LOGGER);
				}

				UserRole userRole;
				try {
					userRole = UserRole.valueOf(roleHeader);
				} catch (IllegalArgumentException e) {
					throw new ApiError(new ErrorBody(HttpStatus.UNAUTHORIZED, ErrorTypes.INVALID_ROLE,
							"Invalid role provided: " + roleHeader), LOGGER);
				}

				List<UserRole> allowedRoles = Arrays.asList(roleGuard.roles());
				if (!allowedRoles.contains(userRole)) {
					throw new ApiError(new ErrorBody(HttpStatus.UNAUTHORIZED, ErrorTypes.NOT_REQUIRED_ROLE,
							"Access denied. User role '" + userRole + "' does not have permission."), LOGGER);
				}
			}
		}
		return true;
	}
}