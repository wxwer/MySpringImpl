package com.wang.demo;

import org.apache.catalina.LifecycleException;
import com.wang.spring.tomcat.TomcatServer;

public class Application {

	public static void main(String[] args) {
		TomcatServer tomcatServer = new TomcatServer(args);
        try {
            tomcatServer.startServer();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        
	}
}
