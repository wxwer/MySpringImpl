package com.wang.spring.mvc;

import java.util.HashMap;
import java.util.Map;
/**
 * 视图-模型对象
 * @author Administrator
 *
 */
public class ModelAndView {
    private String view;

    private Map<String, Object> model;

    public ModelAndView(String path) {
        this.view = path;
        model = new HashMap<String, Object>();
    }
    
    public ModelAndView(String path,Map<String, Object> model) {
        this.view = path;
        this.model = model;
    }
    
    public ModelAndView addModel(String key, Object value) {
        model.put(key, value);
        return this;
    }
    
    public ModelAndView addModel(Map<String, Object> model) {
        this.model.putAll(model);;
        return this;
    }
    
    public String getPath() {
        return view;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
