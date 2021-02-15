package com.wang.spring.mvc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wang.spring.constants.RequestMethod;
import com.wang.spring.ioc.ClassSetHelper;
import com.wang.spring.ioc.DefaultBeanFactory;
import com.wang.spring.mvc.annotation.PathVariable;
import com.wang.spring.mvc.annotation.RequestMapping;
import com.wang.spring.mvc.annotation.RequestParam;
import com.wang.spring.mvc.annotation.ResponseBody;
import com.wang.spring.utils.ConfigUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class DispatcherServlet extends HttpServlet {
	//IOC容器工程
	private static DefaultBeanFactory beanFactory=DefaultBeanFactory.getInstance();
	//请求到Handler的映射
	private Map<Request, Handler> handlerMapping = new HashMap<>();
	//Handler到Handler的映射
	private Map<Handler, HandlerAdapter> adapaterMapping = new HashMap<>();
	private Configuration cfg = null;
	
	@Override
    public void init(ServletConfig config) throws ServletException{
		//请求解析
        initMultipartResolver();
        //多语言、国际化
        initLocaleResolver();
        //主题View层的
        initThemeResolver();
        //=========== 重要 =========
        try {
        	//解析url和Method的关联关系
			initHandlerMappings();
			System.out.println("initHandlerMappings...");
			//适配器（匹配的过程）
	        initHandlerAdapters();
	        System.out.println("initHandlerAdapters...");
	        InitFreemarkerResolver();
	        System.out.println("InitFreemarkerResolver...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Freemarker的初始化配置
	 * @throws Exception
	 */
	private void InitFreemarkerResolver() throws Exception {
		System.out.println(this.getClass().getResource("/").getPath());
		cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(this.getClass().getResource("/").getPath()+ConfigUtil.getAppJspPath()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}
	
	private void registerServlet(ServletContext servletContext) {
        //动态注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        if(defaultServlet==null) {
        	throw new RuntimeException("无法动态注册处理静态资源的默认Servlet");
        }
        defaultServlet.addMapping(this.getClass().getResource("/").getPath()+ConfigUtil.getAppAssetPath() + "*");
    }
	/**
     * 请求解析
     * @param context
     */
    private void initMultipartResolver(){}
    /**
     * 多语言、国际化
     * @param context
     */
    private void initLocaleResolver(){}
    /**
     * 主题View层的
     * @param context
     */
    private void initThemeResolver(){}
    /**
     * 解析url和Method的关联关系
     * @param context
     */
    private void initHandlerMappings() throws Exception{
    	if(beanFactory.isEmpty()) {
    		throw new Exception("ioc容器未初始化");
    	}
    	Set<Class<?>> classSet = ClassSetHelper.getControllerClassSet();
    	for(Class<?> clazz:classSet) {
            String url = "";
            RequestMethod requestMethod = RequestMethod.GET;
            if(clazz.isAnnotationPresent(RequestMapping.class)) {
            	RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            	url = requestMapping.value();
            	requestMethod = requestMapping.method();
            }
          //扫描Controller下面的所有方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String url2 = (url + requestMapping.value()).replaceAll("/+", "/");
                requestMethod = requestMapping.method();
                Request req = new Request(requestMethod, url2);
                Handler handler = new Handler(beanFactory.getBean(clazz), method);
                handlerMapping.put(req, handler);
                System.out.println("Mapping: " + url2 + " to  :" + method.toString());
            }
        }		
    }
    /**
     * 适配器（匹配的过程）,主要是用来动态匹配我们参数的
     * @param context
     */
    private void initHandlerAdapters() throws Exception{
        if (handlerMapping.isEmpty()) {
            throw new Exception("handlerMapping 未初始化");
        }
        //参数类型作为key，参数的索引号作为值
        Map<String, Integer> paramMapping = null;
        for (Map.Entry<Request, Handler> entry : handlerMapping.entrySet()) {
            paramMapping = new HashMap<String, Integer>();
            //把这个方法上面所有的参数全部获取到
            Request req = entry.getKey();
            Handler handler =entry.getValue();
            Class<?>[] parameterTypes = handler.method.getParameterTypes();
            //有顺序，但是通过反射，没法拿到我们参数名字
            //因为每个参数上面是可以加多个数组的，所以是二维数组,第一位表示参数位置，第二位表示注解个数
            Annotation[][] pa = handler.method.getParameterAnnotations();
            //匹配自定参数列表
            for (int i = 0; i < pa.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramMapping.put(type.getName(), i);
                    continue;
                }
                for (Annotation annotation : pa[i]) {
                	String paramName;
                    if (annotation instanceof RequestParam) {
                        paramName = ((RequestParam) annotation).value();
                        if (!"".equals(paramName.trim())) {
                            paramMapping.put(paramName, i);
                        }
                    }
                    else if (annotation instanceof PathVariable) {
                    	paramName = ((PathVariable) annotation).value();
                        if (!"".equals(paramName.trim())) {
                            paramMapping.put(paramName, i);
                        }
					}
                    else if(annotation instanceof ResponseBody) {
                    	paramMapping.put("ResponseBody", i);
                    }
                }
            }
            adapaterMapping.put(handler, new HandlerAdapter(paramMapping));
        }
    }
    /**
     * 处理GET请求
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    /**
     * 处理POST请求,在这里调用自己写的Controller的方法
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("request method is "+req.getMethod()+" url is "+req.getRequestURI());
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception, Msg :" + Arrays.toString(e.getStackTrace()));
        }
    }
    
    private Request createRequest(HttpServletRequest request) {
    	String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        Request req = new Request(Enum.valueOf(RequestMethod.class, request.getMethod().toUpperCase()), url);
        return req;
    }
    /**
     * 根据请求找到对应的Handler
     * @param request
     * @return
     */
    private Handler getHandler(HttpServletRequest request) {
        if (handlerMapping.isEmpty()) {
        	System.out.println("handlerMapping is empty" );
            return null;
        }
        Request req = createRequest(request);
        System.out.println("getHander,url is "+req.requestPath);
        for(Request request2:handlerMapping.keySet()) {
        	if(request2.equals(req)) {
        		return handlerMapping.get(request2);
        	}
        }
        return null;
    }
    /**
     * 根据Handler找到对应的HandlerAdapter
     * @param handler
     * @return
     */
    private HandlerAdapter getHandlerAdapter(Handler handler) {
    	if(handler==null || adapaterMapping==null || adapaterMapping.isEmpty()) {
    		return null;
    	}
		return adapaterMapping.get(handler);
	}
    
    private Map<String, String> getPathVariableMap(HttpServletRequest request){
        Request req = createRequest(request);
        for(Request reqKey:handlerMapping.keySet()) {
        	if(reqKey.equals(req)) {
        		return Request.parsePathVariable(reqKey.requestPath, req.requestPath);
        	}
        }
		return null;
        
    }
    /**
     * 处理请求，执行方法，解析结果
     * @param request
     * @param response
     * @throws Exception
     */
    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Handler handler = getHandler(request);
		if(handler==null) {
			response.getWriter().write("404 Handler Not Found");
		}
		HandlerAdapter handlerAdapter = getHandlerAdapter(handler);
		//执行HandlerAdapter，解析执行结果
		if(null!=handlerAdapter) {
			Map<String, String> pathVariableMap = getPathVariableMap(request);
			Object data = handlerAdapter.handle(request, response, handler,pathVariableMap);
			if(data instanceof ModelAndView) {
				handlerFreemarkerResult(data,request,response);
			}
			else if(isResponseBody(handler)){
				handleJsonResult(data,response);
			}
			else {
				handleStringResult(data, response);
			}
		}
		else {
			response.getWriter().write("404 HandlerAdapter Not Found");
		}
	}
    /**
     * 返回字符串数据
     */
    private void handleStringResult(Object data, HttpServletResponse response) throws IOException {
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
    private void handleJsonResult(Object data, HttpServletResponse response) throws IOException {
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        String json = JSON.toJSON(data).toString();
        writer.write(json);
        writer.flush();
        writer.close();
    }
    private boolean isResponseBody(Handler handler) {
    	return handler.method.isAnnotationPresent(ResponseBody.class) || handler.controller.getClass().isAnnotationPresent(ResponseBody.class);
    }
    /**
     *用JSP解析器解析ModelAndView
     * @param data
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void handleViewResult(Object data, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
    private void handlerFreemarkerResult(Object data, HttpServletRequest request, HttpServletResponse response) throws Exception, IOException {
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
