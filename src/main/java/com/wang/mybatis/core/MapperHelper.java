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
import com.wang.mybatis.executor.ExecutorFactory;
import com.wang.spring.ioc.BeanDefinition;
import com.wang.spring.ioc.BeanDefinitionRegistry;
import com.wang.spring.ioc.ClassSetHelper;
import com.wang.spring.ioc.GenericBeanDefinition;
/**
 * 扫描被@Mapper注解的类，解析方法对应的方法详情类MethodDetails
 * @author Administrator *
 */
public class MapperHelper {
	//单例对象
	private static MapperHelper mapperHelper = null;
	//方法详情映射
    private static Map<Method,MethodDetails> cacheMethodDetails = new ConcurrentHashMap<>();
    //被@Mapper注解的类集合
    private static Set<Class<?>> mapperClassSet = null;
    static {
    	MapperHelper.init();
    }
    private MapperHelper() {
    }
    /**
     * 获得MapperHelper的单例
     * @return
     */
    public static MapperHelper getInstance() {
    	synchronized (MapperHelper.class) {
			if(mapperHelper==null) {
				synchronized (MapperHelper.class) {
					mapperHelper = new MapperHelper();
				}
			}
		}
    	return mapperHelper;
    }
    /**
     * 获得cacheMethodDetails
     * @return
     */
    public static Map<Method,MethodDetails> getCacheMethodDetails(){
    	return cacheMethodDetails;
    }
    /**
     * 初始化方法
     */
    private  static void init(){
    	mapperClassSet = ClassSetHelper.getClassSetByAnnotation(Mapper.class);
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
		    	//对方法解析，获得详情类
		        MethodDetails methodDetails = handleParameter(method);
		        //解析方法注解上的Sql语句
		        methodDetails.setSqlSource(handleAnnotation(method));
		        cacheMethodDetails.put(method,methodDetails);
		    }
		}
        //将被@Mapper注解的类注册到BeanDefinition容器中，被设置代理类
        if(mapperClassSet!=null && !mapperClassSet.isEmpty()) {
    		CGLibMapperProxy cgLibMapperProxy = new CGLibMapperProxy(ExecutorFactory.getExecutor());
    		for(Class<?> cls:mapperClassSet) {
    			BeanDefinition beanDefinition=null;
				if(BeanDefinitionRegistry.containsBeanDefinition(cls.getName())) {
					beanDefinition = BeanDefinitionRegistry.getBeanDefinition(cls.getName());
				}
				else {
					beanDefinition = new GenericBeanDefinition();
					beanDefinition.setBeanClass(cls);
				}
				beanDefinition.setIsProxy(true);
				beanDefinition.setProxy(cgLibMapperProxy);
				BeanDefinitionRegistry.registryBeanDefinition(cls.getName(), beanDefinition);
				System.out.println("MapperHelper 注册 "+cls.getName());
			}
    	}
    }
    /**
     * 获得某个方法的详情类
     * @param method
     * @return
     */
    public  static MethodDetails getMethodDetails(Method method) {
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
    private  static MethodDetails handleParameter(Method method){
    	
        MethodDetails methodDetails = new MethodDetails();
        int parameterCount = method.getParameterCount();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<String> parameterNames = new ArrayList<>();
        Parameter[] params = method.getParameters();
        //设置参数名称
        for(Parameter parameter:params){
            parameterNames.add(parameter.getName());
        }
        //设置注解中的参数名称
        for(int i = 0; i < parameterCount; i++){
            parameterNames.set(i,getParamNameFromAnnotation(method,i,parameterNames.get(i)));
        }
        //将参数类型和参数名添加到MethodDetails对象中
        methodDetails.setParameterTypes(parameterTypes);
        methodDetails.setParameterNames(parameterNames);
        //获取方法返回类型
        Type methodReturnType = method.getGenericReturnType();
        Class<?> methodReturnClass = method.getReturnType();
        if(methodReturnType instanceof ParameterizedType){
        	//如果是可参数化的类型，如List，则获取具体参数类型
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
     * 解析注解中的Sql语句，生成SqlSource对象
     * @param method
     * @return
     */
    private  static SqlSource handleAnnotation(Method method){
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
    private static String getParamNameFromAnnotation(Method method, int i, String paramName) {
        final Object[] paramAnnos = method.getParameterAnnotations()[i];
        for (Object paramAnno : paramAnnos) {
            if (paramAnno instanceof Param) {
                paramName = ((Param) paramAnno).value();
            }
        }
        return paramName;
    }
}
