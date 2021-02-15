package com.wang.spring.ioc;

import com.wang.spring.constants.BeanScope;

public interface BeanFactory {
	Object getBean(String beanName,BeanScope beanScope);
	
	Object getBean(String name);
    
	Object getBean(Class<?> cls,BeanScope beanScope);
	
    Object getBean(Class<?> cls);
    
    void setBean(String name, Object obj);

    void setBean(Class<?> cls, Object obj);

    void refresh()throws Exception;
    
    boolean isEmpty();
}
