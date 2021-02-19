package com.wang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import com.wang.mybatis.datasource.DataSource;

public class SimpleTransactionManager implements TransactionManager{
	
	//ThreadLocal对象保证每个线程对应唯一的数据库连接
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>(); 
    //数据源
    private DataSource dataSource; 
    //事务隔离级别，默认为可重复读
    private Integer level = Connection.TRANSACTION_REPEATABLE_READ;
    //是否自动提交
    private Boolean autoCommit = true; 

    public SimpleTransactionManager(DataSource dataSource){
        this(dataSource,null,null);
    }

    public SimpleTransactionManager(DataSource dataSource, Integer level, Boolean autoCommmit) {
        this.dataSource = dataSource;
        if(level != null){
            this.level = level;
        }
        if(autoCommmit != null){
            this.autoCommit = autoCommmit;
        }
    }
    /**
     * 获得连接对象，先从TreadLocal中获取，如果没有则获取一个新的连接
     */
    @Override
    public Connection getConnection() throws SQLException{
    	Connection connection = connectionThreadLocal.get();
    	if(connection!=null && !connection.isClosed()){
    		return connection;
    	}
    	else {
    		Connection tempConnection = dataSource.getConnection();
    		tempConnection.setAutoCommit(autoCommit);
    		tempConnection.setTransactionIsolation(level);
    		connectionThreadLocal.set(tempConnection);
    		return tempConnection;
		}
    }
    /**
     * 开始事务，设置连接未不可自动提交，并设置相应事务隔离级别
     */
    @Override
	public void beginTransaction(Integer level) throws SQLException {
    	Connection tempConnection = dataSource.getConnection();
		tempConnection.setAutoCommit(false);
		if(level!=null) {
			tempConnection.setTransactionIsolation(level);
		}
		else {
			tempConnection.setTransactionIsolation(this.level);
		}
		connectionThreadLocal.set(tempConnection);
	}
    
    @Override
	public void beginTransaction() throws SQLException {
    	Connection tempConnection = dataSource.getConnection();
		tempConnection.setAutoCommit(false);
		tempConnection.setTransactionIsolation(this.level);
		connectionThreadLocal.set(tempConnection);
	}
    /**
     * 提交事务
     */
    @Override
    public void commit() throws SQLException{
    	Connection connection = connectionThreadLocal.get();
        if(connection != null && !connection.isClosed()){
            connection.commit();
        }
    }
    /**
     * 回滚事务
     */
    @Override
    public void rollback() throws SQLException{
    	Connection connection = connectionThreadLocal.get();
        if(connection != null && !connection.isClosed()){
            connection.rollback();
        }
    }
    /**
     * 普通的关闭链接，如果是自动提交才自动关闭连接
     */
    @Override
    public void close() throws SQLException{
    	Connection connection = connectionThreadLocal.get();
        //放回连接池
        if(connection.getAutoCommit() && connection != null  && !connection.isClosed()){
        	dataSource.removeConnection(connection);
        	//连接设为null
            connectionThreadLocal.remove();
        }
    }
    
  /**
   * 关闭事务，恢复连接未自动提交和默认隔离级别，然后关闭连接，关闭链接前，若设置了自动提交为false，则必须进行回滚操作
   */
    @Override
    public void closeTransaction() throws SQLException{
    	Connection connection = connectionThreadLocal.get();
        //放回连接池
        if(!connection.getAutoCommit() && connection != null && !connection.isClosed()){
        	connection.rollback();
        	connection.setAutoCommit(autoCommit);
        	connection.setTransactionIsolation(level);
        	dataSource.removeConnection(connection);
        	//链接设为null
        	connectionThreadLocal.remove();
        }
    }
    /**
     * 设置事务隔离级别
     */
	@Override
	public void setLevel(Integer level) {
		// TODO Auto-generated method stub
		this.level = level;
	}
	/**
	 * 设置是否自动提交
	 */
	@Override
	public void setAutoCommit(Boolean autoCommit) {
		// TODO Auto-generated method stub
		this.autoCommit = autoCommit;
	}
	/**
	 * 获得事务id，由于一个事务对应唯一一个连接，因此返回的是连接名
	 */
	@Override
	public String getTransactionId() {
		// TODO Auto-generated method stub
		Connection connection = connectionThreadLocal.get();
		String transactionId = null;
		try {
			if(!connection.getAutoCommit() && connection != null && !connection.isClosed()) {
				transactionId = connection.toString();
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return transactionId;
	}
}