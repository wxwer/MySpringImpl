package com.wang.spring.ioc;

import com.wang.spring.common.MyProxy;
import com.wang.spring.constants.BeanScope;

public interface BeanDefinition {
    Class<?> getBeanClass();

    BeanScope getScope();

    boolean isSingleton();

    boolean isPrototype();
    
    boolean getIsProxy();
    
    void setIsProxy(boolean isProxy);
    
    MyProxy getProxy();
    
    void setProxy(MyProxy myProxy);
    
    String getInitMethodName();
}
