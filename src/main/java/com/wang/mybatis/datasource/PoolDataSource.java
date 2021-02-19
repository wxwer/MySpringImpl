package com.wang.mybatis.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.wang.spring.utils.ConfigUtil;

public class PoolDataSource implements DataSource{
	//数据源连接信息
	private String driverClassName;
    private String url;
    private String userName;
    private String passWord;
    
    //空闲连接池
    private LinkedBlockingQueue<Connection> idleConnectPool = new LinkedBlockingQueue<>();
    //活跃连接池
    private LinkedBlockingQueue<Connection> busyConnectPool = new LinkedBlockingQueue<>();
    //活跃连接数
    private AtomicInteger activeSize = new AtomicInteger(0);
    //最大连接数
    private Integer maxSize=10;
    private Long waitTimeMill = 100L;
    
	public PoolDataSource(String driverClassName, String url, String userName, String passWord,Integer maxSize,Long waitTimeMill) {
		// TODO Auto-generated constructor stub
		loadDriverClass(driverClassName);
        this.driverClassName = driverClassName;
        this.url = url;
        this.userName = userName;
        this.passWord = passWord;
    	this.maxSize = maxSize;
    	this.waitTimeMill = waitTimeMill;
	}
	@Override
	public void loadDriverClass(String driverClassName) {
    	try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(this.url,this.userName,this.passWord);
	}
	
	@Override
	public Connection getConnection(String url,String username, String password) throws SQLException {
		//从idle池中取出一个连接
        Connection connection = idleConnectPool.poll();
        if (connection != null) {
            //如果有连接，则放入busy池中
            busyConnectPool.offer(connection);
            return connection;
        }
        if (activeSize.get() < maxSize) {
            //通过 activeSize.incrementAndGet() <= maxSize 这个判断
            if (activeSize.incrementAndGet() <= maxSize) {
                connection = DriverManager.getConnection(url,username,password);// 创建新连接
                busyConnectPool.offer(connection);
                return connection;
            }
        }
        //如果空闲池中连接数达到maxSize， 则阻塞等待归还连接
        try {
        	//阻塞获取连接，如果指定时间内内有其他连接释放
            connection = idleConnectPool.poll(waitTimeMill, TimeUnit.MILLISECONDS);
            if (connection == null) {
                System.out.println("等待超时");
                throw new RuntimeException("等待连接超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return connection;
	}

	@Override
	public void removeConnection(Connection connection) throws SQLException {
		if(connection!=null) {
			//如果连接未关闭，则连接从活跃池中取出，放到空闲池，如果连接已关闭，则移除连接
			if(!connection.isClosed()) {
				busyConnectPool.remove(connection);
				idleConnectPool.offer(connection);
			}
			else {
				busyConnectPool.remove(connection);
				idleConnectPool.remove(connection);
				connection = null;
			}
		}
	}

	

}
