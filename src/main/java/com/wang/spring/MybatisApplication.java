package com.wang.spring;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import com.wang.demo.Service.UserMapper;
import com.wang.demo.model.User;
import com.wang.mybatis.core.CGLibMapperProxy;
import com.wang.mybatis.core.JdkMapperProxy;
import com.wang.mybatis.core.MapperCore;
import com.wang.mybatis.core.MethodDetails;
import com.wang.mybatis.core.NormalDataSource;
import com.wang.mybatis.execute.SimpleExecutor;
import com.wang.mybatis.handler.ResultSetHandler;
import com.wang.spring.utils.ConfigUtil;

public class MybatisApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataSource dataSource =new  NormalDataSource(ConfigUtil.getJdbcDriver(),  ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword());
		Map<Method,MethodDetails> cacheMethodDetails = MapperCore.getCacheMethodDetails();
		SimpleExecutor simpleExecutor = new SimpleExecutor(new MapperCore(), dataSource, false, false);
		Object[] args2 = {1};
		try {
			for(Method method:cacheMethodDetails.keySet()) {
				System.out.println(simpleExecutor.select(method,args2 ));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		JdkMapperProxy jdkMapperProxy = new JdkMapperProxy(simpleExecutor);
		CGLibMapperProxy cgLibMapperProxy = new CGLibMapperProxy(simpleExecutor);
		UserMapper userMapper = (UserMapper) cgLibMapperProxy.getProxy(UserMapper.class);
		
		System.out.println(userMapper.selectUsers(1));
		 //mapperProxy.getProxy(UserMapper.class);
		//System.out.println(userMapper.selectUsers(1));UserMapper userMapper =  (UserMapper)
		/*
		try {
			Class.forName(ConfigUtil.getJdbcDriver());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			//Connection connection = dataSource.getConnection();
			Connection connection =  DriverManager.getConnection( ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword());
			String sql = "select * from user";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeQuery();
			ResultSet resultSet = preparedStatement.getResultSet();
			//resultSet.next();
			//System.out.println(resultSet.getString("name"));
			ResultSetHandler resultSetHandler = new ResultSetHandler(User.class, resultSet);
			List<User> users = resultSetHandler.handle();
			System.out.println(users);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}

}
