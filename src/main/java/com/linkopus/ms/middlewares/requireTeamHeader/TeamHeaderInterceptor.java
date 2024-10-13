package com.linkopus.ms.middlewares.requireTeamHeader;

import com.linkopus.ms.middlewares.errorHandlers.ApiError.ApiError;
import com.linkopus.ms.middlewares.errorHandlers.ApiError.ErrorBody;
import com.linkopus.ms.utils.ressources.constants.Headers;
import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TeamHeaderInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamHeaderInterceptor.class);

	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler) throws Exception {
		if (handler instanceof HandlerMethod handlerMethod) {
			RequiredTeamHeader requiredTeamHeader = handlerMethod.getMethodAnnotation(RequiredTeamHeader.class);

			if (requiredTeamHeader == null) {
				requiredTeamHeader = handlerMethod.getBeanType().getAnnotation(RequiredTeamHeader.class);
			}

			if (requiredTeamHeader != null) {
				String teamHeader = request.getHeader(Headers.TEAM);

				if (teamHeader == null || teamHeader.isEmpty()) {
					throw new ApiError(new ErrorBody(HttpStatus.BAD_REQUEST, ErrorTypes.TEAM_HEADER_IS_REQUIRED,
							"Missing `team` header in request."), LOGGER);
				}
			}
		}
		return true;
	}
}
