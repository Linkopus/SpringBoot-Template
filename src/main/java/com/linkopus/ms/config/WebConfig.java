package com.linkopus.ms.config;

import com.linkopus.ms.middlewares.requireEmailHeader.EmailHeaderInterceptor;
import com.linkopus.ms.middlewares.requireTeamHeader.TeamHeaderInterceptor;
import com.linkopus.ms.middlewares.roleGuard.RoleGuardInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private RoleGuardInterceptor roleGuardInterceptor;

	@Autowired
	private EmailHeaderInterceptor emailHeaderInterceptor;

	@Autowired
	private TeamHeaderInterceptor teamHeaderInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(roleGuardInterceptor);
		registry.addInterceptor(emailHeaderInterceptor);
		registry.addInterceptor(teamHeaderInterceptor);
	}
}
