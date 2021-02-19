package com.wang.demo.interceptor;

import com.wang.spring.annotation.aop.After;
import com.wang.spring.annotation.aop.Around;
import com.wang.spring.annotation.aop.Aspect;
import com.wang.spring.annotation.aop.Before;
import com.wang.spring.annotation.aop.Pointcut;
import com.wang.spring.aop.JoinPoint;

@Aspect
public class Interceptor {
	@Pointcut("com.wang.demo.service.UserService.register")
	public void point() {
		
	}
	
	@Before(value = "point()",order = 1)
	public void beforeService() {
		System.out.println("开始调用registerUser,当前时间为："+System.currentTimeMillis());
	}
	
	@Before(value = "com.wang.demo.service.UserService.loginUser")
	public void beforeService2() {
		System.out.println("开始调用loginUser,当前时间为："+System.currentTimeMillis());
	}
	
	@Before(value = "com.wang.demo.service.UserService.isUserLogin")
	public void beforeService3() {
		System.out.println("开始调用isUserLogin,当前时间为："+System.currentTimeMillis());
	}
	
	@Around(value = "point()")
	public Object aroundService(JoinPoint joinPoint) {
		Object result=null;
		System.out.println("around调用之前............");
		try {
			result=joinPoint.proceed();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("around调用之后..........");
		return result;
	}
	
	@After("point()")
	public void afterService() {
		System.out.println("registerUser方法执行结束");
	}
}
