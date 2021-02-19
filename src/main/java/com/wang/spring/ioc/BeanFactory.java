package com.wang.spring.ioc;

import com.wang.spring.constants.BeanScope;
/**
 * Bean工厂接口
 * @author Administrator
 *
 */
public interface BeanFactory {
	//根据类名获得bean
	Object getBean(String beanName,BeanScope beanScope);
	
	Object getBean(String name);
    //根据类对象获得bean
	Object getBean(Class<?> cls,BeanScope beanScope);
	
    Object getBean(Class<?> cls);
    //设置bean
    void setBean(String name, Object obj);

    void setBean(Class<?> cls, Object obj);
    //刷新Bean工厂
    void refresh()throws Exception;
    //Bean工厂是否为空
    boolean isEmpty();
}
