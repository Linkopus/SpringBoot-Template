package com.linkopus.ms.middlewares.requireEmailHeader;

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

import java.util.regex.Pattern;

@Component
public class EmailHeaderInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailHeaderInterceptor.class);
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler) throws Exception {
		if (handler instanceof HandlerMethod handlerMethod) {
			RequiredEmailHeader requiredEmailHeader = handlerMethod.getMethodAnnotation(RequiredEmailHeader.class);

			if (requiredEmailHeader == null) {
				requiredEmailHeader = handlerMethod.getBeanType().getAnnotation(RequiredEmailHeader.class);
			}

			if (requiredEmailHeader != null) {
				String emailHeader = request.getHeader(Headers.EMAIL);

				if (emailHeader == null || emailHeader.isEmpty()) {
					throw new ApiError(new ErrorBody(HttpStatus.BAD_REQUEST, ErrorTypes.EMAIL_HEADER_IS_REQUIRED,
							"Missing `email` header in request."), LOGGER);
				}

				if (!EMAIL_PATTERN.matcher(emailHeader).matches()) {
					throw new ApiError(new ErrorBody(HttpStatus.BAD_REQUEST, ErrorTypes.INVALID_EMAIL_FORMAT,
							"The email header format is invalid."), LOGGER);
				}
			}
		}
		return true;
	}
}