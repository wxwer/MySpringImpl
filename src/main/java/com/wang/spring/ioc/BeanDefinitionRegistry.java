package com.wang.spring.ioc;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 将bean的定义信息BeanDefinition注册到BeanDefinition容器中
 * @author Administrator
 *
 */
public class BeanDefinitionRegistry{

	//BeanDefinition容器
	private static Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

	/**
	 * 注册BeanDefinition
	 */
	public static void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(beanName, "beanName 不能为空");
		Objects.requireNonNull(beanDefinition, "beanDefinition 不能为空");
		beanDefinitionMap.put(beanName, beanDefinition);
	}
	/**
	 * 根据类名className获取BeanDefinition
	 */
	public static BeanDefinition getBeanDefinition(String className) {
		// TODO Auto-generated method stub
		return beanDefinitionMap.get(className);
	}
	/**
	 * BeanDefinition是否存在
	 */
	public static boolean containsBeanDefinition(String className) {
		// TODO Auto-generated method stub
		return beanDefinitionMap.containsKey(className);
	}
	/**
	 * 获得BeanDefinition容器
	 * @return
	 */
	public static Map<String,BeanDefinition> getBeanDefinitionMap() {
		return beanDefinitionMap;
	}

}
