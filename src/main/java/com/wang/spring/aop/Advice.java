package com.wang.spring.aop;

import java.lang.reflect.Method;
/**
 * 增强类
 * @author Administrator
 *
 */
public class Advice {
	//切面对象
	private Object aspect;
	//增强方法
	private Method adviceMethod;
	//级别，一个方法有多个增强时根据级别排序
	private Integer order;
	private String throwName;
	public Advice(Object aspect, Method adviceMethod,Integer order) {
		this.aspect = aspect;
	    this.adviceMethod = adviceMethod;
	    this.order = order;
	}
	
	public Object getAspect() {
		return aspect;
	}
	public void setAspect(Object aspect) {
		this.aspect=aspect;
	}
	
	public Method getAdviceMethod() {
		return adviceMethod;
	}
	public void setAdviceMethod(Method adviceMethod) {
		this.adviceMethod=adviceMethod;
	}
	
	public String getThrowName() {
		return throwName;
	}
	public void setThrowName(String throwName) {
		this.throwName=throwName;
	}
	
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order=order;
	}
}