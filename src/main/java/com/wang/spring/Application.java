package com.wang.spring;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;

import com.wang.demo.Service.IService;
import com.wang.demo.Service.Service1;
import com.wang.demo.Service.UserPrintService;
import com.wang.spring.aop.AOPHelper;
import com.wang.spring.aop.Advice;
import com.wang.spring.aop.CGLibProxy;
import com.wang.spring.aop.JdkProxy;
import com.wang.spring.constants.RequestMethod;
import com.wang.spring.ioc.BeanFactory;
import com.wang.spring.ioc.ClassSetHelper;
import com.wang.spring.ioc.DefaultBeanFactory;
import com.wang.spring.mvc.DispatcherServlet;
import com.wang.spring.mvc.Request;
import com.wang.spring.tomcat.TomcatServer;
import com.wang.spring.utils.PropsUtil;

public class Application {

	public static void main(String[] args) {
		/**
		// TODO Auto-generated method stub
		DefaultBeanFactory beanFactory = DefaultBeanFactory.getInstance();
		try {
			UserPrintService userPrintService = (UserPrintService) beanFactory.getBean(UserPrintService.class);
			userPrintService.printUser();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		DefaultBeanFactory beanFactory = DefaultBeanFactory.getInstance();
		AOPHelper aopHelper = new AOPHelper();
		try {
			Map<Class<?>, List<Method>> classMethodMap= aopHelper.getClassMethodMap();
			Map<Method, Map<String, List<Advice>>> methodAdvicesMap = aopHelper.getMethodAdvicesMap();
			System.out.println(classMethodMap);
			System.out.println(methodAdvicesMap);
			System.out.println("CGLibProxy.....................");
			CGLibProxy cgLibProxy = new CGLibProxy(methodAdvicesMap);
			Service1 service1=(Service1) cgLibProxy.getProxy(Service1.class);
			service1.service();
			/*
			System.out.println("JdkProxy.....................");
			Service1 service21 = new Service1();
			JdkProxy jdkProxy = new JdkProxy(methodAdvicesMap);
			IService service22=(IService) jdkProxy.getProxy(service21);
			
			service22.service();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		TomcatServer tomcatServer = new TomcatServer(args);
        try {
            tomcatServer.startServer();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        
	}
}
