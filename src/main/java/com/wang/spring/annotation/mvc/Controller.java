package com.wang.spring.annotation.mvc;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.wang.spring.annotation.ioc.Component;

@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface Controller {
	String value() default "";
}
