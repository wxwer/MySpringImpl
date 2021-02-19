package com.wang.mybatis.datasource;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 数据源接口
 * @author Administrator
 *
 */
public interface DataSource {
	//加载驱动
	void loadDriverClass(String driverClassName);
	//获得默认连接
	Connection getConnection() throws SQLException;
	//释放连接
	void removeConnection(Connection connection) throws SQLException;
	//根据参数获得连接
	Connection getConnection(String url,String username, String password) throws SQLException;
}
