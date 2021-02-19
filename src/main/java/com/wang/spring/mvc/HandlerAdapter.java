package com.wang.spring.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.wang.spring.annotation.mvc.PathVariable;
import com.wang.spring.annotation.mvc.RequestBody;
import com.wang.spring.annotation.mvc.RequestParam;


public class HandlerAdapter {
    private Map<String, Integer> paramMapping;
    
    public HandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }
    /**
     * 主要目的是用反射调用url对应的method
     * @param request
     * @param response
     * @param handler
     */
    public Object handle(HttpServletRequest request, HttpServletResponse response, Handler handler,Map<String, String> pathVariableMap) throws InvocationTargetException, IllegalAccessException {
        if(handler.method.getParameterCount()==0) {
        	return handler.method.invoke(handler.controller);
        }
        Class<?>[] parameterTypes = handler.method.getParameterTypes();
        Parameter[] parameters = handler.method.getParameters();
        //要想给参数赋值，只能通过索引号来找到具体的某个参数
        Object[] paramValues = new Object[parameterTypes.length];
        
        String requestName = HttpServletRequest.class.getName();
        if (this.paramMapping.containsKey(requestName)) {
            Integer requestIndex = this.paramMapping.get(requestName);
            paramValues[requestIndex] = request;
        }
        String responseName = HttpServletResponse.class.getName();
        if (this.paramMapping.containsKey(responseName)) {
            Integer responseIndex = this.paramMapping.get(responseName);
            paramValues[responseIndex] = response;
        }
        //注入消息体的内容
        String bodyContent=null;
        try {
			bodyContent = getBodyContent(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(request.getMethod().equals("POST") && bodyContent!=null) {
        	Integer index=-1;
        	for(Integer i=0;i<parameters.length;i++) {
        		if(parameters[i].isAnnotationPresent(RequestBody.class)) {
        			index=i;
        			break;
        		}
        	}
        	if(index>=0) {
        		Object body = JSON.parseObject(bodyContent, parameterTypes[index]);
        		paramValues[index]=body;
        	}
        }
        //解析RequestParam和pathVariable中的参数
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, Integer> entry : paramMapping.entrySet()) {
        	Integer index = entry.getValue();
        	if(paramValues[index]!=null) {
        		continue;
        	}
        	if (!params.containsKey(entry.getKey())) {
        		if(parameters[index].isAnnotationPresent(PathVariable.class)) {
        			if(pathVariableMap!=null && pathVariableMap.containsKey(entry.getKey())) {
        				paramValues[index] = caseStringValue(pathVariableMap.get(entry.getKey()), parameterTypes[index]);
        			}
        			else {
        				paramValues[index] = caseStringValue(parameters[index].getAnnotation(PathVariable.class).defaultValue(), parameterTypes[index]);
					}
        		}
        		else {
        			String value = parameters[index].isAnnotationPresent(RequestParam.class)?parameters[index].getAnnotation(RequestParam.class).defaultValue():"";
                	paramValues[index] = value;
				}
            }
        	else {
        		String value = Arrays.toString(params.get(entry.getKey())).replaceAll("\\[|\\]", "")
                        .replaceAll(",\\s", ",");
        		paramValues[index] = caseStringValue(value, parameterTypes[index]);
			}
        }
        Object r = handler.method.invoke(handler.controller, paramValues);
        return r;
    }
    /**
     * 转换参数类型
     * @param value
     * @param clazz
     * @return
     */
    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value);
        } else {
            return null;
        }
    }
    /**
     * 获取HttpServletRequest的body中的内容，以字符串的形式返回
     * @param request
     * @return
     * @throws Exception
     */
    private String getBodyContent(HttpServletRequest request) throws Exception {
    	if ( request.getMethod().equals("POST") )
    	{
    	    StringBuffer sb = new StringBuffer();
    	    BufferedReader bufferedReader = null;
    	    String content = "";
    	    try {
    	        bufferedReader =  request.getReader() ;
    	        char[] charBuffer = new char[128];
    	        int bytesRead;
    	        while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
    	            sb.append(charBuffer, 0, bytesRead);
    	        }
    	    } catch (IOException ex) {
    	        throw ex;
    	    } finally {
    	        if (bufferedReader != null) {
    	            try {
    	                bufferedReader.close();
    	            } catch (IOException ex) {
    	                throw ex;
    	            }
    	        }
    	    }
    	    return sb.toString();
    	}
		return null;
	}
}
