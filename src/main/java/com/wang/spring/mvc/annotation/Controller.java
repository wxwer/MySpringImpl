package com.wang.spring.mvc.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.wang.spring.ioc.annotation.Component;

@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface Controller {
	String value() default "";
}
