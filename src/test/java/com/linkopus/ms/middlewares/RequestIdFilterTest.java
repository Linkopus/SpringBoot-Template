package com.linkopus.ms.middlewares;

import com.linkopus.ms.utils.ressources.constants.Headers;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestIdFilterTest {

	private RequestIdFilter filter;

	@BeforeEach
	void setUp() {
		filter = new RequestIdFilter();
		MDC.clear();
	}

	@AfterEach
	void tearDown() {
		MDC.clear();
	}

	@Test
	void testDoFilter_WithRequestIdHeader() throws IOException, ServletException {
		String requestIdValue = "12345";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.REQUEST_ID_HEADER)).thenReturn(requestIdValue);

		HttpServletResponse response = mock(HttpServletResponse.class);

		FilterChain chain = new FilterChain() {
			@Override
			public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
				assertEquals(requestIdValue, MDC.get("requestId"));
			}
		};
		filter.doFilter(request, response, chain);
		verify(response).setHeader(Headers.REQUEST_ID_HEADER, requestIdValue);
		assertNull(MDC.get("requestId"), "MDC should be cleared after filter execution.");
	}

	@Test
	void testDoFilter_WithoutRequestIdHeader() throws IOException, ServletException {
		String defaultRequestId = "00000-xxxxx-0000000-xxx-000";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.REQUEST_ID_HEADER)).thenReturn(null);

		HttpServletResponse response = mock(HttpServletResponse.class);

		FilterChain chain = new FilterChain() {
			@Override
			public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
				assertEquals(defaultRequestId, MDC.get("requestId"));
			}
		};

		filter.doFilter(request, response, chain);
		verify(response).setHeader(Headers.REQUEST_ID_HEADER, defaultRequestId);
		assertNull(MDC.get("requestId"), "MDC should be cleared after filter execution.");
	}

	@Test
	void testDoFilter_WithEmptyRequestIdHeader() throws IOException, ServletException {
		String defaultRequestId = "00000-xxxxx-0000000-xxx-000";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.REQUEST_ID_HEADER)).thenReturn("");

		HttpServletResponse response = mock(HttpServletResponse.class);

		FilterChain chain = new FilterChain() {
			@Override
			public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
				assertEquals(defaultRequestId, MDC.get("requestId"));
			}
		};

		filter.doFilter(request, response, chain);

		verify(response).setHeader(Headers.REQUEST_ID_HEADER, defaultRequestId);
		assertNull(MDC.get("requestId"), "MDC should be cleared after filter execution.");
	}

	@Test
	void testDoFilter_ExceptionDuringChainDoFilter() throws IOException, ServletException {
		String requestIdValue = "12345";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(Headers.REQUEST_ID_HEADER)).thenReturn(requestIdValue);

		HttpServletResponse response = mock(HttpServletResponse.class);

		final ServletException exception = new ServletException("FilterChain exception");

		FilterChain chain = new FilterChain() {
			@Override
			public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
				assertEquals(requestIdValue, MDC.get("requestId"));
				throw exception;
			}
		};

		ServletException thrownException = assertThrows(ServletException.class, () -> {
			filter.doFilter(request, response, chain);
		});

		assertEquals(exception, thrownException, "Exception should be propagated up the chain.");
		verify(response).setHeader(Headers.REQUEST_ID_HEADER, requestIdValue);
		assertNull(MDC.get("requestId"), "MDC should be cleared even when an exception occurs.");
	}
}
