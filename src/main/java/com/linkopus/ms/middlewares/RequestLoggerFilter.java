package com.linkopus.ms.middlewares;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RequestLoggerFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggerFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		long startTime = System.currentTimeMillis();
		LOGGER.info("Receiving a {} request on {}", httpRequest.getMethod(), httpRequest.getRequestURI());

		chain.doFilter(request, response);

		long elapsedTime = System.currentTimeMillis() - startTime;
		LOGGER.info("{} request on {} ended with a status of {} and a delay of ({})s", httpRequest.getMethod(),
				httpRequest.getRequestURI(), httpResponse.getStatus(), String.format("%.3f", elapsedTime / 1000.0));
	}
}
