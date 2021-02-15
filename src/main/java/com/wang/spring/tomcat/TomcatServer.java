package com.wang.spring.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import com.wang.spring.ioc.DefaultBeanFactory;
import com.wang.spring.mvc.DispatcherServlet;


public class TomcatServer {
    private Tomcat tomcat;
    private String[] args;

    public TomcatServer(String[] args) {
        this.args = args;
    }
    
    public void startServer() throws LifecycleException{
        
    	Tomcat tomcat = new Tomcat();
    	DefaultBeanFactory beanFactory = DefaultBeanFactory.getInstance();
        //AOPHelper.initAOP();
        System.out.println("beanFactory.isEmpty():"+beanFactory.isEmpty());
        
        tomcat.setHostname("localhost");
        tomcat.setPort(8080);
        final Context context = tomcat.addContext("/", null);
        Tomcat.addServlet(context, "dispatch", new DispatcherServlet());
        context.addServletMapping("/", "dispatch");
        try {
            tomcat.init();
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    	
    /*
    	tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.start();
        
        DefaultBeanFactory beanFactory = DefaultBeanFactory.getInstance();
        //AOPHelper.initAOP();
        System.out.println("beanFactory.isEmpty():"+beanFactory.isEmpty());
        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());

//        TestServlet testServlet = new TestServlet();
//        tomcat.addServlet(context, "testServlet" ,testServlet).setAsyncSupported(true);
//        context.addServletMappingDecoded("/test.json", "testServlet");

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        //添加前端控制器Servlet
        tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);//.setAsyncSupported(true);
        //添加映射
        context.addServletMapping("/", "dispatcherServlet");
        tomcat.getHost().addChild(context);
        System.out.println("start tomcat...");
        
        Thread thread = new Thread("tomcat_await_thread.") {
            @Override
            public void run() {
                TomcatServer.this.tomcat.getServer().await();
            }
        };

        thread.setDaemon(false);
        thread.start();
        */
    }
}
