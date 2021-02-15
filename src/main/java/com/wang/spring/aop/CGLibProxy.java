package com.wang.spring.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.wang.spring.constants.AdviceTypeConstant;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLibProxy implements MethodInterceptor{
	//目标代理方法和增强列表的映射
	Map<Method, Map<String, List<Advice>>> methodAdvicesMap=null;
	
	public CGLibProxy(Map<Method, Map<String, List<Advice>>> methodAdvicesMap) {
		// TODO Auto-generated constructor stub
		this.methodAdvicesMap=methodAdvicesMap;
	}
	/**
	 * 通过CGLib库生成代理类
	 * @param cls
	 * @return
	 */
	public Object getProxy(Class<?> cls) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(cls);
		enhancer.setCallback(this);
		return enhancer.create();
	}
	/**
	 * 拦截目标代理方法，对目标方法进行增强
	 */
	@Override
	public Object intercept(Object object, Method method, Object[] arg2, MethodProxy methodProxy) throws Throwable {
		Object result=null;
		//判断是否增强
		if(intercept(method)) {
			Map<String, List<Advice>> advices =  methodAdvicesMap.get(method);
			try {
				//前置通知
				if(advices!=null && advices.containsKey(AdviceTypeConstant.BEFORE)) {
					invokeAdvice(advices.get(AdviceTypeConstant.BEFORE));
				}
				//环绕通知
				if(advices!=null && advices.containsKey(AdviceTypeConstant.AROUND)) {
					List<Advice> aroundAdvices = advices.get(AdviceTypeConstant.AROUND);
					if(aroundAdvices!=null && !aroundAdvices.isEmpty()) {
						Advice advice = aroundAdvices.get(0);
						JoinPoint joinPoint = new JoinPoint(object, methodProxy, arg2);
						Method adviceMethod = advice.getAdviceMethod();
						Object aspect = advice.getAspect();
						result=adviceMethod.invoke(aspect,joinPoint);
					}
					else {
						result= methodProxy.invokeSuper(object, arg2);
					}
				}
				else {
					result= methodProxy.invokeSuper(object, arg2);
				}
				//后置通知
				if(advices!=null && advices.containsKey(AdviceTypeConstant.AFTER)) {
					invokeAdvice(advices.get(AdviceTypeConstant.AFTER));
				}
			} 
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				//异常通知
				if(advices!=null && advices.containsKey(AdviceTypeConstant.AFTERTHROWING)) {
					invokeAdvice(advices.get(AdviceTypeConstant.AFTERTHROWING));
				}
			}
			finally {
				//返回前通知
				if(advices!=null && advices.containsKey(AdviceTypeConstant.AFTERRETURNING)) {
					invokeAdvice(advices.get(AdviceTypeConstant.AFTERRETURNING));
				}
			}
		}
		else {
			//否则不进行增强
			result= methodProxy.invokeSuper(object, arg2);
		}
		return result;
	}
	//执行通知增强
	private void invokeAdvice(List<Advice> adviceList) throws Throwable {
		if(adviceList!=null && !adviceList.isEmpty()) {
			for(Advice advice:adviceList) {
				Method adviceMethod = advice.getAdviceMethod();
				Object aspect = advice.getAspect();
				adviceMethod.invoke(aspect);
			}
		}
	}
	//判断是否增强
	private boolean intercept(Method method) {
		return methodAdvicesMap.containsKey(method);
	}
}
