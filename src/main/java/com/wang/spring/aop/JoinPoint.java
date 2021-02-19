package com.wang.spring.aop;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodProxy;
/**
 * 连接点类
 * @author Administrator
 *
 */
public class JoinPoint {
	private Object target=null;
	private Method method=null;
	//目标方法的代理
	private MethodProxy methodProxy=null;
	private Object[] args=null;
	public JoinPoint(Object target, Method method,Object[] args) {
		this.target = target;
	    this.method = method;
	    this.args = args;
	}
	
	public JoinPoint(Object target, Method method) {
		this.target = target;
	    this.method = method;
	}
	
	public JoinPoint(Object object, MethodProxy methodProxy, Object[] args) {
		// TODO Auto-generated constructor stub
		this.target = object;
	    this.methodProxy = methodProxy;
	    this.args = args;
	}
	
	public JoinPoint(Object object, MethodProxy methodProxy) {
		// TODO Auto-generated constructor stub
		this.target = object;
	    this.methodProxy = methodProxy;
	}
	/**
	 * 执行无参的连接点方法
	 * @return
	 * @throws Throwable
	 */
	public Object proceed() throws Throwable{
		Object result;
		if(methodProxy!=null) {
			result=methodProxy.invokeSuper(target, this.args);
		}
		else {
			if(this.args==null) {
				result=method.invoke(target);
			}
			else {
				result=method.invoke(target, this.args);
			}
		}
		
		return result;
	}
	/**
	 * 执行带参的连接点方法
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public Object proceed(Object[] args) throws Throwable{
		if(methodProxy!=null) {
			return methodProxy.invokeSuper(target, args);
		}
		else {
			return method.invoke(target, args);
		}
	}
	
	
}
