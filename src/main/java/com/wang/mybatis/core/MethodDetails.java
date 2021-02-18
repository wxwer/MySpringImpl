package com.wang.mybatis.core;

import java.util.List;

public class MethodDetails{
    private Class<?> returnType;

    private boolean HasSet;

    /**æ–¹æ³•è¾“å…¥å�‚æ•°ç±»åž‹é›†å�ˆ*/
    private Class<?>[] parameterTypes;

    /**æ–¹æ³•è¾“å…¥å�‚æ•°å��é›†å�ˆ*/
    private List<String> parameterNames;

    /**sqlè¯­å�¥é›†å�ˆ*/
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
