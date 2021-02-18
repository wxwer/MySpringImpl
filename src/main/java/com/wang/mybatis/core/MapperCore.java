package com.wang.mybatis.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.wang.mybatis.annotation.Delete;
import com.wang.mybatis.annotation.Insert;
import com.wang.mybatis.annotation.Mapper;
import com.wang.mybatis.annotation.Param;
import com.wang.mybatis.annotation.Select;
import com.wang.mybatis.annotation.Update;
import com.wang.mybatis.constant.SqlTypeConstant;
import com.wang.spring.ioc.ClassSetHelper;

public class MapperCore {
    private static Map<Method,MethodDetails> cacheMethodDetails = new ConcurrentHashMap<>();
    static {
    	new MapperCore().load();;
    }
    public static Map<Method,MethodDetails> getCacheMethodDetails(){
    	return cacheMethodDetails;
    }
    private void load(){
        Set<Class<?>> mapperClassSet = ClassSetHelper.getClassSetByAnnotation(Mapper.class);
        if(mapperClassSet==null || mapperClassSet.isEmpty()) {
        	System.out.println("未找到Mapper类");
        	return;
        }
        for(Class<?> nowClazz: mapperClassSet){
		    if(!nowClazz.isInterface()){
		        continue;
		    }
		    Method[] methods = nowClazz.getDeclaredMethods();
		    for( Method method : methods){
		        MethodDetails methodDetails = handleParameter(method);
		        methodDetails.setSqlSource(handleAnnotation(method));
		        cacheMethodDetails.put(method,methodDetails);
		    }
		}
    }
    public MethodDetails getMethodDetails(Method method) {
		if(cacheMethodDetails==null || cacheMethodDetails.isEmpty() || !cacheMethodDetails.containsKey(method)) {
			return null;
		}
		return cacheMethodDetails.get(method);
	}
    /**
     * 由Method对象解析出表示方法详情的MethodDetails对象
     * @param method
     * @return
     */
    private MethodDetails handleParameter(Method method){

        MethodDetails methodDetails = new MethodDetails();

        int parameterCount = method.getParameterCount();

        Class<?>[] parameterTypes = method.getParameterTypes();

        List<String> parameterNames = new ArrayList<>();

        Parameter[] params = method.getParameters();
        for(Parameter parameter:params){
            parameterNames.add(parameter.getName());
        }
        
        for(int i = 0; i < parameterCount; i++){
            parameterNames.set(i,getParamNameFromAnnotation(method,i,parameterNames.get(i)));
        }

        methodDetails.setParameterTypes(parameterTypes);
        methodDetails.setParameterNames(parameterNames);
        
        Type methodReturnType = method.getGenericReturnType();
        Class<?> methodReturnClass = method.getReturnType();
        if(methodReturnType instanceof ParameterizedType){
/** è¿”å›žæ˜¯é›†å�ˆç±» ç›®å‰�ä»…æ”¯æŒ�List todo*/
            if(!List.class.equals(methodReturnClass)){
                throw new RuntimeException("now ibatis only support list");
            }
            Type type = ((ParameterizedType) methodReturnType).getActualTypeArguments()[0];
            methodDetails.setReturnType((Class<?>) type);
            methodDetails.setHasSet(true);
        }else {
            methodDetails.setReturnType(methodReturnClass);
            methodDetails.setHasSet(false);
        }

        return methodDetails;
    }

    /**
     * 
     * */
    private SqlSource handleAnnotation(Method method){
        SqlSource sqlSource = null;
        String sql = null;
        Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations){
            if(Select.class.isInstance(annotation)){
                Select selectAnnotation = (Select)annotation;
                sql = selectAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlTypeConstant.SELECT_TYPE);
                break;
            }else if(Update.class.isInstance(annotation)){
                Update updateAnnotation = (Update)annotation;
                sql = updateAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlTypeConstant.UPDATE_TYPE);
                break;
            }else if(Delete.class.isInstance(annotation)){
                Delete deleteAnnotation = (Delete) annotation;
                sql = deleteAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlTypeConstant.DELETE_TYPE);
                break;
            }else if(Insert.class.isInstance(annotation)){
                Insert insertAnnotation = (Insert) annotation;
                sql = insertAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlTypeConstant.INSERT_TYPE);
                break;
            }
        }
        if(sqlSource == null){
            throw new RuntimeException("method annotation not null");
        }
        return sqlSource;
    }

    /**
     * 获取指定method的第i个参数的Param参数命名
     * @param method
     * @param i
     * @param paramName
     * @return
     */
    private String getParamNameFromAnnotation(Method method, int i, String paramName) {
        final Object[] paramAnnos = method.getParameterAnnotations()[i];
        for (Object paramAnno : paramAnnos) {
            if (paramAnno instanceof Param) {
                paramName = ((Param) paramAnno).value();
            }
        }
        return paramName;
    }
}
