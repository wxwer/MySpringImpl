package com.wang.spring.ioc;

import com.wang.spring.constants.BeanScope;

public interface BeanDefinition {
    Class<?> getBeanClass();

    BeanScope getScope();

    boolean isSingleton();

    boolean isPrototype();

    String getInitMethodName();
}
