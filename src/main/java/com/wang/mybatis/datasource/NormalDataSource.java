package com.wang.mybatis.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * 普通的数据源派生类，每次都会获取到一个新的连接
 * @author Administrator
 *
 */
public class NormalDataSource implements DataSource{
	//驱动类
    private String driverClassName;
    //数据源url
    private String url;
    //账号
    private String userName;
    //密码
    private String passWord;

    public NormalDataSource(String driverClassName, String url, String userName, String passWord) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.userName = userName;
        this.passWord = passWord;
        loadDriverClass(driverClassName);
    }

    @Override
    public void loadDriverClass(String driverClassName) {
    	try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public Connection getConnection(String url,String username, String password) throws SQLException {
    	return DriverManager.getConnection(url,username,password);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(this.url, this.userName, this.passWord);
    }
    
    @Override
    public void removeConnection(Connection connection) throws SQLException{
        if(connection!=null) {
        	connection.close();
        	connection = null;
        }
    }
}
