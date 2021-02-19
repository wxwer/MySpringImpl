package com.wang.spring.ioc;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.Resources;

import com.wang.spring.annotation.ioc.Component;
import com.wang.spring.annotation.ioc.Service;
import com.wang.spring.annotation.mvc.Controller;
import com.wang.spring.constants.ConfigConstant;
import com.wang.spring.utils.ClassUtil;
import com.wang.spring.utils.PropsUtil;
/**
 * 类集合助手，可扫描配置文件中的包路径，获得指定类型或被指定注解的类对象集合
 * @author Administrator
 *
 */
public class ClassSetHelper {
	private static Set<Class<?>> CLASS_SET;
	static {
		Properties props = PropsUtil.loadProps("application.properties");
		String basePackName = PropsUtil.getString(props, ConfigConstant.APP_BASE_PACKAGE);
		CLASS_SET = ClassUtil.getClassSet(basePackName);
	}
	/**
	 * 获得所有类对象集合
	 * @return
	 */
	public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }
	/**
	 * 获得被Component注解的类对象集合
	 * @return
	 */
	public static Set<Class<?>> getComponentClassSet(){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> clz : CLASS_SET) {
            if (clz.isAnnotationPresent(Component.class)){
                classSet.add(clz);
            }
        }
        return classSet;
    }
	/**
	 * 获得被Service注解的类对象集合
	 * @return
	 */
    public static Set<Class<?>> getServiceClassSet(){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> clz : CLASS_SET) {
            if (clz.isAnnotationPresent(Service.class)){
                classSet.add(clz);
            }
        }
        return classSet;
    }
    /**
	 * 获得被Controller注解的类对象集合
	 * @return
	 */
    public static Set<Class<?>> getControllerClassSet(){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Controller.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }
    /**
	 * 获得被Component,Service,Controller注解的类对象集合
	 * @return
	 */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> beanClassSet = new HashSet<Class<?>>();
        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClassSet());
        beanClassSet.addAll(getComponentClassSet());
        return beanClassSet;
    }
    
    public static Set<Class<?>> getInheritedComponentClassSet() {
        return getClassSetByInheritedAnnotation(Component.class);
    }
    
    /**
     * 通过超类获取实现类的集合
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET) {
            //isAssignableFrom() 表示superClass是cls的超类
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取基础包名下直接带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> present){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET) {
            if(cls.isAnnotationPresent(present)){
                classSet.add(cls);
            }
        }
        return classSet;
    }
	
    /**
     * 获取基础包名下直接或间接带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByInheritedAnnotation(Class<? extends Annotation> present){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET) {
            if(isAnnoPresent(cls,present)){
                classSet.add(cls);
            }
        }
        return classSet;
    }
    
    /**
     * 判断classz上是否有注解类annoClass，包括直接的和间接的注解
     * @param classz
     * @param annoClass
     */
    private static boolean isAnnoPresent(Class<?> classz,Class<?> annoClass) {
		Annotation[] annotations = (Annotation[]) classz.getAnnotations();
        for (Annotation annotation : annotations) {
            if ( annotation.annotationType() != Deprecated.class &&
                    annotation.annotationType() != SuppressWarnings.class &&
                    annotation.annotationType() != Override.class &&
                    annotation.annotationType() != PostConstruct.class &&
                    annotation.annotationType() != PreDestroy.class &&
                    annotation.annotationType()!= Resource.class &&
                    annotation.annotationType() != Resources.class &&
                    annotation.annotationType() != Generated.class &&
                    annotation.annotationType() != Target.class &&
                    annotation.annotationType() != Retention.class &&
                    annotation.annotationType() != Documented.class &&
                    annotation.annotationType() != Inherited.class) {
                if (annotation.annotationType() ==annoClass){
                    return true;
                }else{
                	return isAnnoPresent(annotation.annotationType(),annoClass);
                }
            }
        }
        return false;
	}
}
