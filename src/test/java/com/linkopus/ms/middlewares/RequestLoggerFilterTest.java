package com.linkopus.ms.middlewares;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {RequestLoggerFilterTest.class})
class RequestLoggerFilterTest {

	private RequestLoggerFilter requestLoggerFilter;
	private FilterChain mockChain;

	@BeforeEach
	void setup() {
		requestLoggerFilter = new RequestLoggerFilter();
		mockChain = mock(FilterChain.class);
	}

	@Test
	void testRequestLoggerFilterLogsRequest() throws IOException, ServletException {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/test");
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		requestLoggerFilter.doFilter(mockRequest, mockResponse, mockChain);

		verify(mockChain).doFilter(mockRequest, mockResponse);
	}
}
