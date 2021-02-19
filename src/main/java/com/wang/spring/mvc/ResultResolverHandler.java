package com.wang.spring.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wang.spring.utils.ConfigUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ResultResolverHandler {

	/**
     * 返回字符串数据
     */
    public static void handleStringResult(Object data, HttpServletResponse response) throws IOException {
        System.out.println("String resolver");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }
    /**
     * 返回JSON数据
     */
    public static void handleJsonResult(Object data, HttpServletResponse response) throws IOException {
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        String json = JSON.toJSON(data).toString();
        writer.write(json);
        writer.flush();
        writer.close();
    }
    /**
     *用JSP解析器解析ModelAndView
     * @param data
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public static void handleViewResult(Object data, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	if(data==null || !(data instanceof ModelAndView)) {
        	throw new RuntimeException("无法转换成ModelAndView");
        }
        ModelAndView view = (ModelAndView) data;
    	String path = view.getPath();
        if (StringUtils.isNotEmpty(path)) {
            if (path.startsWith("/")) { //重定向
                response.sendRedirect(request.getContextPath() + path);
            } else { //请求转发
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                request.getRequestDispatcher(ConfigUtil.getAppJspPath() + path).forward(request, response);
            }
        }
    }
    /**
     * 用freemarker解析ModelAndView
     * @param data
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public static void handlerFreemarkerResult(Object data, Configuration cfg,HttpServletRequest request, HttpServletResponse response) throws Exception, IOException {
    	if(data==null || !(data instanceof ModelAndView)) {
        	throw new RuntimeException("无法转换成ModelAndView");
        }
        ModelAndView view = (ModelAndView) data;
        String path = view.getPath();
        if (StringUtils.isNotEmpty(path)) {
        	if (path.startsWith("/") || path.endsWith("/")) { //重定向
                response.sendRedirect(request.getContextPath() + path);
            } else {
            	Template temp = cfg.getTemplate(path);
                response.setContentType("text/html");
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                temp.process(view.getModel(), writer);
            }
        }
    }
}
