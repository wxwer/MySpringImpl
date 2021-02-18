package com.wang.mybatis.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import com.wang.mybatis.annotation.Delete;
import com.wang.mybatis.annotation.Insert;
import com.wang.mybatis.annotation.Select;
import com.wang.mybatis.annotation.Update;
import com.wang.mybatis.constant.SqlTypeConstant;
import com.wang.mybatis.execute.Executor;
import com.wang.mybatis.execute.SimpleExecutor;
import com.wang.spring.common.MyProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLibMapperProxy implements MethodInterceptor,MyProxy{
    private SimpleExecutor executor=null;

    public CGLibMapperProxy(SimpleExecutor executor) {
        this.executor = executor;
    }
    @Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        if(isIntercept(method)){
        	// getMethodType
        	System.out.println(executor.mapperCore.getMethodDetails(method).getSqlSource().getSql());
            Integer methodType = executor.mapperCore.getMethodDetails(method).getSqlSource().getExecuteType();
            System.out.println("methodType: "+methodType);
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
        	result = methodProxy.invokeSuper(object, args);
		}
        return result;
    }
    
    @Override
    public Object getProxy(Class<?> cls) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(cls);
		enhancer.setCallback(this);
		return enhancer.create();
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
