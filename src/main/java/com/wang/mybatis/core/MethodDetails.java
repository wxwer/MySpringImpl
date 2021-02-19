package com.wang.mybatis.core;

import java.util.List;

public class MethodDetails{
	//方法返回类型
    private Class<?> returnType;
    //是否返回集合
    private boolean HasSet;
    //参数类型
    private Class<?>[] parameterTypes;
    //参数名称
    private List<String> parameterNames;
    //解析得到的Sql包装对象
    private SqlSource sqlSource;

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public boolean isHasSet() {
        return HasSet;
    }

    public void setHasSet(boolean hasSet) {
        HasSet = hasSet;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }
}
