package com.wang.mybatis.bean;

import java.util.HashMap;
import java.util.Map;

public class SqlResultCache{

    private static Map<String,Object> map = new HashMap<>();

    public void putCache(String key, Object val) {
        map.put(key,val);
    }
    
    public Object getCache(String key) {
        return map.get(key);
    }

    public void cleanCache() {
        map.clear();
    }

    public int getSize() {
        return map.size();
    }

    public void removeCache(String key) {
        map.remove(key);
    }
}