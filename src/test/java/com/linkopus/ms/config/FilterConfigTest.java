package com.linkopus.ms.config;

import com.linkopus.ms.middlewares.RequestLoggerFilter;
import com.linkopus.ms.middlewares.RequestIdFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {FilterConfig.class})
@ExtendWith(SpringExtension.class)
class FilterConfigTest {

	@Autowired
	private FilterRegistrationBean<RequestLoggerFilter> requestLoggerFilterBean;

	@Autowired
	private FilterRegistrationBean<RequestIdFilter> requestIdFilterBean;

	@Test
	void testRequestLoggerFilterBeanIsRegistered() {
		assertNotNull(requestLoggerFilterBean);
		assertEquals("/*", requestLoggerFilterBean.getUrlPatterns().iterator().next());
	}

	@Test
	void testRequestIdFilterBeanIsRegistered() {
		assertNotNull(requestIdFilterBean);
		assertEquals("/*", requestIdFilterBean.getUrlPatterns().iterator().next());
	}
}
