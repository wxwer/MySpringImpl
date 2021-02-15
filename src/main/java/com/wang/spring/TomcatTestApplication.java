package com.wang.spring;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author allen
 * @date 2019-01-22 13:20
 */
public class TomcatTestApplication {
    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        HttpServlet httpServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().write("hello, i'm embed tomcat");
            }

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                doGet(req, resp);
            }
        };
        tomcat.setHostname("localhost");
        tomcat.setPort(8080);
        final Context context = tomcat.addContext("/embed-tomcat", null);
        Tomcat.addServlet(context, "dispatch", httpServlet);
        context.addServletMapping("/dispatch", "dispatch");
        try {
            tomcat.init();
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}