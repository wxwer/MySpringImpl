package com.wang.spring.aop;

import java.lang.reflect.Method;

public class Advice {
	private Object aspect;
	private Method adviceMethod;
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