package com.wang.spring.ioc;

import com.wang.spring.common.MyProxy;
import com.wang.spring.constants.BeanScope;
/**
 * Bean的定义类
 * @author Administrator
 *
 */
public interface BeanDefinition {
	//获得类对象
    Class<?> getBeanClass();
    //设置类对象
  	void setBeanClass(Class<?> cls);
    //获得作用域
    BeanScope getScope();
    //是否单例
    boolean isSingleton();
    //是否原型
    boolean isPrototype();
    //是否需要代理
    boolean getIsProxy();
    //设置是否需要代理
    void setIsProxy(boolean isProxy);
    //获得代理类
    MyProxy getProxy();
    //设置代理类
    void setProxy(MyProxy myProxy);
    //获得初始化方法名
    String getInitMethodName();
}
