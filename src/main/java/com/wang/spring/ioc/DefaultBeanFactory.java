package com.wang.spring.ioc;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import com.wang.spring.annotation.ioc.Autowired;
import com.wang.spring.annotation.ioc.Bean;
import com.wang.spring.annotation.ioc.Component;
import com.wang.spring.annotation.ioc.Configuration;
import com.wang.spring.annotation.ioc.Qualifier;
import com.wang.spring.annotation.ioc.Service;
import com.wang.spring.annotation.mvc.Controller;
import com.wang.spring.common.MyProxy;
import com.wang.spring.constants.BeanScope;

public class DefaultBeanFactory implements BeanFactory{
	//bean工厂单例
	private static DefaultBeanFactory instance = null;
	//Bean容器，也叫IOC容器
	private static Map<String, Object> beanMap = new ConcurrentHashMap<>();
	//Bean的注册信息BeanDefinition容器
	private static Map<String, BeanDefinition> beanDefinitionMap = BeanDefinitionRegistry.getBeanDefinitionMap();
	/**
	 * 初始化Bean
	 */
	static {
		Set<Class<?>> beanClassSet = ClassSetHelper.getBeanClassSet();//ClassSetHelper.getInheritedComponentClassSet(); //
		if(beanClassSet!=null && !beanClassSet.isEmpty()) {
			try {
				for(Class<?> beanClass : beanClassSet) {
					GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
					genericBeanDefinition.setBeanClass(beanClass);
					BeanDefinitionRegistry.registryBeanDefinition(beanClass.getName(), genericBeanDefinition);
				}
				//注册配置的bean
				getInstance().initConfigBean();
				//注册其他所有的bean
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private  DefaultBeanFactory() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 获取单例Bean工厂
	 * @return
	 */
	public static DefaultBeanFactory getInstance() {
		synchronized (DefaultBeanFactory.class) {
			if(null==instance) {
				synchronized (DefaultBeanFactory.class) {
					instance=new DefaultBeanFactory();
				}
			}
		}
		return instance;
	}
	public static Map<String,Object> getBeanMap() {
		return beanMap;
	}
	
	/**
	 * 根据类的全限定名获取bean
	 */
	@Override
	public Object getBean(String beanName) {
		// TODO Auto-generated method stub
		Object bean = null;
		try {
			bean=doGetBean(beanName,BeanScope.SINGLETON);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bean;
	}
	@Override
	public Object getBean(String beanName,BeanScope beanScope) {
		// TODO Auto-generated method stub
		Object bean = null;
		try {
			bean=doGetBean(beanName,beanScope);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 根据类的class对象获取bean
	 */
	@Override
	public Object getBean(Class<?> cls) {
		// TODO Auto-generated method stub
		Object bean = null;
		try {
			bean=doGetBean(cls.getName(),BeanScope.SINGLETON);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bean;
	}
	@Override
	public Object getBean(Class<?> cls,BeanScope beanScope) {
		// TODO Auto-generated method stub
		Object bean = null;
		try {
			bean=doGetBean(cls.getName(),beanScope);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bean;
	}
	
	/**
	 * 设置bean
	 */
	@Override
	public void setBean(String beanName, Object obj) {
		// TODO Auto-generated method stub
		try {
			Objects.requireNonNull(beanName, "beanName 不能为空");
			beanMap.put(beanName, obj);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * 设置bean
	 */
	@Override
	public void setBean(Class<?> cls, Object obj) {
		// TODO Auto-generated method stub
		this.setBean(cls.getName(), obj);
	}
	/**
	 * 刷新，重新注入所有的bean
	 */
	@Override
	public void refresh() throws Exception {
		// TODO Auto-generated method stub
		for(Map.Entry<String, BeanDefinition> entry: beanDefinitionMap.entrySet()) {
			getBean(entry.getKey());
		}
	}
	/**
	 * 注入@Configuration中配置bean
	 * @throws Exception
	 */
	private void initConfigBean() throws Exception {
		Set<Class<?>> configClassSet = ClassSetHelper.getClassSetByAnnotation(Configuration.class);
		if(configClassSet==null || configClassSet.isEmpty()) {
			return;
		}
		for(Class<?> configClass : configClassSet) {
			//注册Configuration
			GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
			genericBeanDefinition.setBeanClass(configClass);
			BeanDefinitionRegistry.registryBeanDefinition(configClass.getName(), genericBeanDefinition);
			Object configBean = getBean(configClass);
			Method[] methods = configClass.getDeclaredMethods();
			for(Method method:methods) {
				if(method.isAnnotationPresent(Bean.class)) {
					Class<?> returnClass = method.getReturnType();
					Object bean = method.invoke(configBean);
					String keyName = returnClass.getName();
					beanMap.put(keyName, bean);
					System.out.println("成功注入"+configClass.getName()+" 中的  "+returnClass.getName());
				}
				
			}
			beanMap.remove(configClass.getName());
		}
	}
	
	/**
	 * 从beanMap获取bean，如果不存在则实例化一个bean
	 * @param beanName
	 * @return
	 * @throws Exception
	 */
	private Object doGetBean(String beanName,BeanScope beanScope) throws Exception{
		Objects.requireNonNull(beanName, "beanName 不能为空");
		Object bean = beanMap.get(beanName);
		if(bean!=null) {
			return bean;
		}
		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		Class<?> beanClass = beanDefinition.getBeanClass();
		//找到实现类
		beanClass = findImplementClass(beanClass,null);
		//判断是否需要代理，若需要则生成代理类
		if(beanDefinition.getIsProxy() && beanDefinition.getProxy()!=null) {
			MyProxy myProxy = beanDefinition.getProxy();
			bean=myProxy.getProxy(beanClass);
		}
		else {
			bean = beanClass.getDeclaredConstructor().newInstance();
		}
		//反射调用init方法
		String initMethodName = beanDefinition.getInitMethodName();
		if(initMethodName!=null) {
			Method method = beanClass.getMethod(initMethodName, null);
			method.invoke(bean, null);
		}
		//如果bean是单例的则缓存
		if(beanScope == BeanScope.SINGLETON) {
			beanMap.put(beanName, bean);
		}
		//注入bean的属性
		fieldInject(beanClass, bean, false);
		return bean;
	}
	/**
	 * 找到实现类
	 * @param interfaceClass
	 * @return
	 */
	private static Class<?> findImplementClass(Class<?> interfaceClass,String name){
		Class<?> implementClass = interfaceClass;
		Set<Class<?>> classSet = new HashSet<>();
		for(Class<?> cls : ClassSetHelper.getClassSet()) {
			if(interfaceClass!=null && interfaceClass.isAssignableFrom(cls) && !interfaceClass.equals(cls)) {
				if(name!=null && !name.equals("")) {
					if(isClassAnnotationedName(cls, name)) {
						return cls;
					}
				}
				classSet.add(cls);
			}
			else if(interfaceClass==null) {
				if(name!=null && !name.equals("")) {
					if(isClassAnnotationedName(cls, name) || (cls.getSimpleName().equals(name) && !cls.isInterface())) {
						return cls;
					}
				}
			}
		if(classSet!=null && !classSet.isEmpty()) {
			implementClass = classSet.iterator().next();
			}
		}
		return implementClass;
	}
	private static boolean isClassAnnotationedName(Class<?> cls,String name) {
		return (cls.isAnnotationPresent(Component.class) && cls.getAnnotation(Component.class).value().equals(name)) ||
				(cls.isAnnotationPresent(Service.class) && cls.getAnnotation(Service.class).value().equals(name)) ||
				(cls.isAnnotationPresent(Controller.class) && cls.getAnnotation(Controller.class).value().equals(name));
	}
	/**
	 * 依赖注入
	 * @param beanClass
	 * @param instance
	 * @param isProxyed
	 * @throws Exception
	 */
	private void fieldInject(Class<?> beanClass, Object bean, boolean isProxyed) throws Exception{
        Field[] beanFields = beanClass.getDeclaredFields();
        if (beanFields != null && beanFields.length > 0) {
            for (Field beanField : beanFields) {
                //找Autowired/Resource注解属性
                if (beanField.isAnnotationPresent(Autowired.class) || beanField.isAnnotationPresent(Resource.class)) {
                    Class<?> beanFieldClass = beanField.getType();
                    String qualifier = null;
                    if(beanField.isAnnotationPresent(Autowired.class)) {
                    	if(beanField.isAnnotationPresent(Qualifier.class)) {
                        	qualifier=beanField.getAnnotation(Qualifier.class).value();
                        }
                        //Service找实现
                        beanFieldClass = findImplementClass(beanFieldClass,qualifier);
                    }
                    else if(beanField.isAnnotationPresent(Resource.class)){
                    	qualifier = beanField.getAnnotation(Resource.class).name();
                    	if(qualifier==null || qualifier.equals("")) {
                    		qualifier = beanFieldClass.getSimpleName();
                    	}
                    	Class<?> tmpClass= findImplementClass(null,qualifier);
                    	if(tmpClass==null || tmpClass.isInterface()) {
                    		Class<?> beanAnnotationType = beanField.getAnnotation(Resource.class).type();
                    		if(beanAnnotationType!=java.lang.Object.class) {
                    			beanFieldClass = findImplementClass(beanAnnotationType, null);
                    		}
                    		else {
                    			beanFieldClass = findImplementClass(beanFieldClass, null);
                    			//System.out.println("beanFieldClass "+beanFieldClass);
                    		}
                    	}
                    	else {
                    		beanFieldClass = tmpClass;
						}
                    }
                    //根据beanName去找实例(这时是实现类的beanNamCe)
                    beanField.setAccessible(true);
                    try {
                        //反射注入属性实例。
                        beanField.set(bean,
                                beanMap.containsKey(beanFieldClass.getName()) ?
                                        beanMap.get(beanFieldClass.getName()) : //第二遍再判断时前面已经缓存了b，可以取到，
                                        getBean(beanFieldClass.getName()) //第一遍缓存没有b，再getBean。
                        );
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
	/**
	 * beanMap是否为空
	 */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return beanMap==null || beanMap.isEmpty();
	}
}
