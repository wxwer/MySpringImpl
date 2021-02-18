package com.wang.mybatis.core;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class NormalDataSource implements DataSource{

    private String driverClassName;

    private String url;

    private String userName;

    private String passWord;

    public NormalDataSource(String driverClassName, String url, String userName, String passWord) {
        loadDriverClass(driverClassName);
        this.driverClassName = driverClassName;
        this.url = url;
        this.userName = userName;
        this.passWord = passWord;
    }
    
    private void loadDriverClass(String driverClassName) {
    	try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,userName,passWord);
    }

    public void removeConnection(Connection connection) throws SQLException{
        if(!connection.getAutoCommit()){
            connection.rollback();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
