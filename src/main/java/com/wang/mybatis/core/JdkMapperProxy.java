package com.wang.mybatis.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.wang.mybatis.annotation.Delete;
import com.wang.mybatis.annotation.Insert;
import com.wang.mybatis.annotation.Select;
import com.wang.mybatis.annotation.Update;
import com.wang.mybatis.constant.SqlTypeConstant;
import com.wang.mybatis.execute.SimpleExecutor;
import com.wang.spring.common.MyProxy;

public class JdkMapperProxy implements InvocationHandler,MyProxy{
    private SimpleExecutor executor=null;

    public JdkMapperProxy(SimpleExecutor executor) {
        this.executor = executor;
    }
    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable {
        Object result = null;
        if(isIntercept(method)){
        	// getMethodType
            Integer methodType = executor.mapperCore.getMethodDetails(method).getSqlSource().getExecuteType();
            System.out.println(methodType);
            if(methodType == null){
                throw new RuntimeException("method is normal sql method");
            }
            if(methodType == SqlTypeConstant.SELECT_TYPE){
            	
                List<Object> list = executor.select(method,args);
                result = list;
                if(!executor.mapperCore.getMethodDetails(method).isHasSet()){
                    if(list.size() == 0){
                        result = null;
                    }else {
                        result = list.get(0);
                    }
                }
            }else{
                Integer count = executor.update(method,args);
                result = count;
            }
        }
        else if (Object.class.equals(method.getDeclaringClass())) {
        	result = method.invoke(object, args);
		}
        return result;
    }
    
    @Override
    public Object getProxy(Class<?> targetClass){
        return Proxy.newProxyInstance(targetClass.getClassLoader(),targetClass.getInterfaces(),this);
    }
    
    private boolean isIntercept(Method method) {
    	for(Annotation annotation : method.getAnnotations()) {
    		if(annotation.annotationType().equals(Select.class) ||
    			annotation.annotationType().equals(Update.class) ||
    			annotation.annotationType().equals(Insert.class) ||
    			annotation.annotationType().equals(Delete.class)) {
    			return true;
    		}
    	}
    	return false;
    }
}