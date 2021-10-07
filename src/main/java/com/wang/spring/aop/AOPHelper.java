package com.wang.spring.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections4.map.HashedMap;

import com.wang.spring.annotation.aop.After;
import com.wang.spring.annotation.aop.AfterReturning;
import com.wang.spring.annotation.aop.AfterThrowing;
import com.wang.spring.annotation.aop.Around;
import com.wang.spring.annotation.aop.Aspect;
import com.wang.spring.annotation.aop.Before;
import com.wang.spring.annotation.aop.Pointcut;
import com.wang.spring.constants.AdviceTypeConstant;
import com.wang.spring.ioc.BeanDefinition;
import com.wang.spring.ioc.BeanDefinitionRegistry;
import com.wang.spring.ioc.ClassSetHelper;
import com.wang.spring.ioc.DefaultBeanFactory;
import com.wang.spring.ioc.GenericBeanDefinition;

public class AOPHelper {
	private static volatile AOPHelper aopHelper=null;
	//需要代理的目标类和目标方法的映射
	private static Map<Class<?>, List<Method>> classMethodMap= new ConcurrentHashMap<>();
	//需要代理的目标方法和增强类的映射，value的map的key为通知的类型
	private static Map<Method, Map<String, List<Advice>>> methodAdvicesMap = new ConcurrentHashMap<>();
	/**
	 * 初始化aop助手
	 */
	static {
		try {
			AOPHelper.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private AOPHelper() {}
	/**
	 * 获得AOPHelper的单例
	 * @return
	 */
	public static AOPHelper getInstance() {
		if(aopHelper==null) {
			synchronized (AOPHelper.class) {
				if(aopHelper==null) {
					aopHelper = new AOPHelper();
					return aopHelper;
				}
			}
		}
		return aopHelper;
	}
	/**
	 * AOP助手初始化方法，扫描有@Aspect注解的类，获取代理目标和增强的映射
	 * @throws Exception
	 */
	public static void init() throws Exception {
		Set<Class<?>> aspectClassSet = ClassSetHelper.getClassSetByAnnotation(Aspect.class);
		for(Class<?> aspectClass : aspectClassSet) {
			Map<String, String> pointcuts = new HashedMap<>();
			Object aspect = aspectClass.getDeclaredConstructor().newInstance();
			for(Method method:aspectClass.getMethods()) {
				if(method.isAnnotationPresent(Pointcut.class)) {
					String pointcutName = method.getName();
					String pointcut = method.getAnnotation(Pointcut.class).value();
					if(pointcut!=null && !pointcut.equals("")) {
						pointcuts.put(pointcutName, pointcut);
					}
				}
			}
			for(Method method:aspectClass.getMethods()) {
				injectMethodAdvices(aspect, method, pointcuts);
			}
		}
		//对增强类根据注解的order进行排序
		for(Method method:methodAdvicesMap.keySet()) {
			for(String key:methodAdvicesMap.get(method).keySet()) {
				Collections.sort(methodAdvicesMap.get(method).get(key), new Comparator<Advice>() {
					@Override
					public int compare(Advice o1, Advice o2) {
						// TODO Auto-generated method stub
						if(o1.getOrder()==o2.getOrder()) {
							return 0;
						}
						else if (o1.getOrder()>o2.getOrder()) {
							return 1;
						}
						else {
							return -1;
						}
					}
				});
			}
		}
		//重新注册需要增强的类，注入代理类
		try {
			CGLibProxy cgLibProxy = new CGLibProxy(methodAdvicesMap);
			for(Class<?> cls:classMethodMap.keySet()) {
				BeanDefinition beanDefinition=null;
				if(BeanDefinitionRegistry.containsBeanDefinition(cls.getName())) {
					beanDefinition = BeanDefinitionRegistry.getBeanDefinition(cls.getName());
				}
				else {
					beanDefinition = new GenericBeanDefinition();
					beanDefinition.setBeanClass(cls);
				}
				beanDefinition.setIsProxy(true);
				beanDefinition.setProxy(cgLibProxy);
				BeanDefinitionRegistry.registryBeanDefinition(cls.getName(), beanDefinition);
				System.out.println("AOPHelper 注册 "+cls.getName());
			}
			//DefaultBeanFactory.getInstance().refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取相应的映射
	 * @return
	 */
	public static Map<Class<?>, List<Method>> getClassMethodMap() {
		return classMethodMap;
	}
    public static Map<Method, Map<String, List<Advice>>> getMethodAdvicesMap() {
		return methodAdvicesMap;
	}
	
	/**
	 * 为目标类和方法找到相应的增强列表(Advice)
	 * @param aspect
	 * @param method
	 * @param pointcuts
	 * @throws Exception
	 */
    private static void injectMethodAdvices(Object aspect,Method method,Map<String, String> pointcuts) throws Exception {
    	String pointValue=null;
    	String pointType = null;
    	Integer order = -1;
    	if(method.isAnnotationPresent(Before.class)) {
    		pointValue = (method.getAnnotation(Before.class)).value();
    		order = (method.getAnnotation(Before.class)).order();
    		pointType = AdviceTypeConstant.BEFORE;
    	}
    	else if(method.isAnnotationPresent(After.class)) {
    		pointValue = (method.getAnnotation(After.class)).value();
    		order = (method.getAnnotation(After.class)).order();
    		pointType = AdviceTypeConstant.AFTER;
    	}
    	else if(method.isAnnotationPresent(Around.class)) {
    		pointValue = (method.getAnnotation(Around.class)).value();
    		order = (method.getAnnotation(Around.class)).order();
    		pointType = AdviceTypeConstant.AROUND;
    	}
    	else if(method.isAnnotationPresent(AfterReturning.class)) {
    		pointValue = (method.getAnnotation(AfterReturning.class)).value();
    		order = (method.getAnnotation(AfterReturning.class)).order();
    		pointType = AdviceTypeConstant.AFTERRETURNING;
    	}
    	else if(method.isAnnotationPresent(AfterThrowing.class)) {
    		pointValue = (method.getAnnotation(AfterThrowing.class)).value();
    		order = (method.getAnnotation(AfterThrowing.class)).order();
    		pointType = AdviceTypeConstant.AFTERTHROWING;
    	}
    	else {
    		//System.out.println(method.getName()+" 不是增强");
			return;
		}
    	pointValue = parsePointValue(pointcuts, pointValue);
    	
		Map<String, String> classAndMethod = getClassAndMethod(pointValue);
		try {
			Class<?> targetClass = Class.forName(classAndMethod.get("class"));
			if(!classMethodMap.containsKey(targetClass)) {
				classMethodMap.put(targetClass,new ArrayList<Method>());
			}
			for(Method method2:targetClass.getMethods()) {
				if(classAndMethod.get("method").equals("*") || classAndMethod.get("method").equals(method2.getName())) {
					classMethodMap.get(targetClass).add(method2);
					Advice advice  = new Advice(aspect, method,order);
					if(!methodAdvicesMap.containsKey(method2)) {
						methodAdvicesMap.put(method2, new HashedMap<String, List<Advice>>());
					}
					if(!methodAdvicesMap.get(method2).containsKey(pointType)) {
						methodAdvicesMap.get(method2).put(pointType, new ArrayList<Advice>());
					}
					methodAdvicesMap.get(method2).get(pointType).add(advice);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }

	/**
	 * 获得切点的值，可能直接在通知上定义，也可能在pointcut定义
	 * @param pointcuts
	 * @param pointValue
	 * @return
	 * @throws Exception
	 */
	private static String parsePointValue(Map<String, String> pointcuts,String pointValue) throws Exception {
		if(pointValue==null || pointValue.equals("")) {
			return null;
		}
		String result = null;
		if(pointValue.endsWith("()")) {
			String pointcutName = pointValue.replace("()", "");
			if(!pointcuts.containsKey(pointcutName)) {
				throw new Exception(pointValue+" 未定义");
			}
			result = pointcuts.get(pointcutName);
		}
		else {
			result = pointValue;
		}
		return result;
	}
	/**
	 * 解析切点全限量名，获得代理的目标类和方法
	 * @param value
	 * @return
	 */
    private  static  Map<String, String> getClassAndMethod(String value) {
        Map<String, String> map = new HashMap<String, String>();
        String[] split = value.split("\\.");
        if (split.length == 0) {
            throw new RuntimeException("全限量名解析异常");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            if (i == split.length - 2) {
                stringBuilder.append(split[i]);
            } else {
                stringBuilder.append(split[i] + ".");
            }
        }
        map.put("class", stringBuilder.toString());
        map.put("method", split[split.length - 1]);
        return map;
    }
}
