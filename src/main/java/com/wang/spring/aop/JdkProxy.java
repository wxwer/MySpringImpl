package com.wang.spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
/**
 * jdk动态代理，必须要有代理接口存在
 * @author Administrator
 *
 */
public class  JdkProxy implements InvocationHandler{
	
	private Object target = null;
	Map<Method, Map<String, List<Advice>>> methodAdvicesMap=null;
	
	public JdkProxy(Map<Method, Map<String, List<Advice>>> methodAdvicesMap) {
		// TODO Auto-generated constructor stub
		this.methodAdvicesMap=methodAdvicesMap;
	}
	
	public Object getProxy(Object target) {
		this.target = target;
		return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(), this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result=null;
		if(intercept(method)) {
			Map<String, List<Advice>> advices =  methodAdvicesMap.get(method);
			try {
				if(advices!=null && advices.containsKey("before")) {
					invokeAdvice(advices.get("before"));
				}
				result=method.invoke(target, args);//相当于调用doWork()方法
				if(advices!=null && advices.containsKey("after")) {
					invokeAdvice(advices.get("after"));
				}
			} 
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				if(advices!=null && advices.containsKey("throwing")) {
					invokeAdvice(advices.get("throwing"));
				}
			}
			finally {
				if(advices!=null && advices.containsKey("returning")) {
					invokeAdvice(advices.get("returning"));
				}
			}
		}
		else {
			result=method.invoke(target, args);//相当于调用doWork()方法
		}
		return result;
	}
	
	private void invokeAdvice(List<Advice> adviceList) throws Throwable {
		if(adviceList!=null && !adviceList.isEmpty()) {
			for(Advice advice:adviceList) {
				Method adviceMethod = advice.getAdviceMethod();
				Object aspect = advice.getAspect();
				adviceMethod.invoke(aspect);
			}
		}
	}
	private boolean intercept(Method method) {
		return methodAdvicesMap.containsKey(method);
	}
}
