package com.linkopus.ms.config;

import com.linkopus.ms.middlewares.RequestLoggerFilter;
import com.linkopus.ms.middlewares.RequestIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
	@Bean
	public FilterRegistrationBean<RequestLoggerFilter> requestLoggerFilter() {
		FilterRegistrationBean<RequestLoggerFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLoggerFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<RequestIdFilter> loggingFilter() {
		FilterRegistrationBean<RequestIdFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestIdFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}
