package com.wang.spring.annotation.mvc;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import com.wang.spring.constants.RequestMethod;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RequestMapping {
    String value() default "";

    RequestMethod method() default RequestMethod.GET;
}
