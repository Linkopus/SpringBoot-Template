package com.linkopus.ms.middlewares.roleGuard;

import com.linkopus.ms.utils.ressources.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleGuard {
	UserRole[] roles() default {};
}