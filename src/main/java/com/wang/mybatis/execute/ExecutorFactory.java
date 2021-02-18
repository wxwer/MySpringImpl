package com.wang.mybatis.execute;

import javax.sql.DataSource;

import com.wang.mybatis.core.MapperCore;
import com.wang.mybatis.core.NormalDataSource;
import com.wang.spring.utils.ConfigUtil;

public class ExecutorFactory {
	
	public static SimpleExecutor getExecutor(){
		DataSource dataSource =new  NormalDataSource(ConfigUtil.getJdbcDriver(),  ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword());
		SimpleExecutor simpleExecutor = new SimpleExecutor(new MapperCore(), dataSource, false, false);
		return simpleExecutor;
	}
}
