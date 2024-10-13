package com.linkopus.ms.middlewares;

import com.linkopus.ms.utils.ressources.constants.Headers;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestIdFilter implements Filter {
	private static final String REQUEST_ID_MDC_KEY = "requestId";
	private static final String DEFAULT_REQUEST_ID = "00000-xxxxx-0000000-xxx-000";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		String requestId = httpServletRequest.getHeader(Headers.REQUEST_ID_HEADER);
		if (requestId == null || requestId.isEmpty()) {
			requestId = DEFAULT_REQUEST_ID;
		}

		MDC.put(REQUEST_ID_MDC_KEY, requestId);
		httpServletResponse.setHeader(Headers.REQUEST_ID_HEADER, requestId);

		try {
			chain.doFilter(request, response);
		} finally {
			MDC.remove(REQUEST_ID_MDC_KEY);
		}
	}
}