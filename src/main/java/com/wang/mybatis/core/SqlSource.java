package com.wang.mybatis.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @example select * from users where id = ${id} and name = #{name}
 * -> sql = select * from users where id = ? and name = ?
 * -> params = {id,name}
 * -> paramInjectTypes = {0,1}
 **/
public class SqlSource {
    /**sql语句，待输入字段替换成?*/
    private String sql;
    /**待输入字段*/
    private List<String> params = new ArrayList<>();
    //注入的类型,0表示拼接，1表示动态注入
    private List<Integer> paramInjectTypes = new ArrayList<>();
    /**select update insert delete*/
    private Integer executeType;
    
    public SqlSource(String sql){
        this.sql = sqlInject(sql);
    }

    private String sqlInject(String sql){
    	
        String labelPrefix1 = "${";
        String labelPrefix2 = "#{";
        String labelSuffix = "}";
        while ((sql.indexOf(labelPrefix1) > 0 || sql.indexOf(labelPrefix2) > 0) && sql.indexOf(labelSuffix) > 0){
        	Integer labelPrefix1Index = sql.indexOf(labelPrefix1);
        	Integer labelPrefix2Index = sql.indexOf(labelPrefix2);
        	String sqlParamName;
        	Integer injectType;
        	if(labelPrefix1Index>0 && labelPrefix2Index<=0) {
        		sqlParamName = sql.substring(labelPrefix1Index,sql.indexOf(labelSuffix)+1);
        		injectType = 0;
        	}
        	else if (labelPrefix2Index>0 && labelPrefix1Index<=0) {
        		sqlParamName = sql.substring(labelPrefix2Index,sql.indexOf(labelSuffix)+1);
        		injectType = 1;
			}
        	else if(labelPrefix1Index>0 && labelPrefix2Index>0 && labelPrefix1Index<labelPrefix2Index){
        		sqlParamName = sql.substring(labelPrefix1Index,sql.indexOf(labelSuffix)+1);
        		injectType = 0;
			}
        	else if(labelPrefix1Index>0 && labelPrefix2Index>0 && labelPrefix1Index>labelPrefix2Index){
        		sqlParamName = sql.substring(labelPrefix2Index,sql.indexOf(labelSuffix)+1);
        		injectType = 1;
			}
        	else {
				continue;
			}
            sql = sql.replace(sqlParamName,"?");
            if(injectType==0) {
            	params.add(sqlParamName.replace("${","").replace("}",""));
            	paramInjectTypes.add(0);
            }
            else {
            	params.add(sqlParamName.replace("#{","").replace("}",""));
            	paramInjectTypes.add(1);
			}
            
        }
        return sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getParam() {
        return params;
    }

    public void setParam(List<String> params) {
        this.params = params;
    }
    
    public List<Integer> getParamInjectType() {
        return paramInjectTypes;
    }

    public void setParamInjectType(List<Integer> paramInjectTypes) {
        this.paramInjectTypes = paramInjectTypes;
    }
    

    public Integer getExecuteType() {
        return executeType;
    }

    public void setExecuteType(Integer executeType) {
        this.executeType = executeType;
    }
}
