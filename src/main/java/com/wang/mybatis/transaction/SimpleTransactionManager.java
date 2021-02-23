package com.wang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

import com.wang.mybatis.datasource.DataSource;
import com.wang.spring.constants.PropagationLevelConstant;

public class SimpleTransactionManager implements TransactionManager{
	
	//ThreadLocal对象保证每个线程对应唯一的数据库连接
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();
    //上级事务连接缓存，使用栈可以保证多级事务的缓存
    private ThreadLocal<Stack<Connection>> delayThreadLocal = new ThreadLocal<>();
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
     * 当前事务是否存在，如果当前的Connection为非自动提交，说明存在
     */
    public boolean isTransactionPresent() {
    	Connection connection = connectionThreadLocal.get();
    	try {
			if(connection!=null && !connection.isClosed() && !connection.getAutoCommit()) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
    
    /**
     * 开始事务，根据事务传播行为进行设置
     */
    @Override
	public void beginTransaction(TransactionStatus status) throws SQLException {
		//PROPAGATION_REQUIRED：如果当前存在事务，则加入该事务，如果当前不存在事务，则创建一个新的事务。
		if(status.propagationLevel==PropagationLevelConstant.PROPAGATION_REQUIRED) {
			if(!status.isTrans) {
				doCreateTransaction(status.isolationLevel);
			}
		}
		//PROPAGATION_SUPPORTS：如果当前存在事务，则加入该事务；如果当前不存在事务，则以非事务的方式继续运行。
		else if(status.propagationLevel==PropagationLevelConstant.PROPAGATION_SUPPORTS) {
			if(!status.isTrans) {
				//不需要创建事务，直接获取自动提交的连接，以非事务方式运行
			}
		}
		//PROPAGATION_MANDATORY：如果当前存在事务，则加入该事务；如果当前不存在事务，则抛出异常。
		else if (status.propagationLevel == PropagationLevelConstant.PROPAGATION_MANDATORY) {
			if(!status.isTrans) {
				throw new RuntimeException("事务传播方式为PROPAGATION_MANDATORY，但当前不存在事务");
			}
		}
		//PROPAGATION_REQUIRES_NEW：重新创建一个新的事务，如果当前存在事务，延缓当前的事务。
		else if (status.propagationLevel == PropagationLevelConstant.PROPAGATION_REQUIRES_NEW) {
			if(status.isTrans) {
				//将当前事务保存在线程本地的栈中，暂缓执行
				if(delayThreadLocal.get()==null) {
					Stack<Connection> stack = new Stack<>();
					delayThreadLocal.set(stack);
				}
				Stack<Connection> stack = delayThreadLocal.get();
				stack.push(connectionThreadLocal.get());
				delayThreadLocal.set(stack);
				connectionThreadLocal.remove();
			}
			//新建一个新的事务
			doCreateTransaction(status.isolationLevel);
		}
		//PROPAGATION_NOT_SUPPORTED：以非事务的方式运行，如果当前存在事务，暂停当前的事务。
		else if (status.propagationLevel == PropagationLevelConstant.PROPAGATION_NOT_SUPPORTED) {
			if(status.isTrans) {
				//将当前事务保存在线程本地的栈中，暂缓执行
				if(delayThreadLocal.get()==null) {
					Stack<Connection> stack = new Stack<>();
					delayThreadLocal.set(stack);
				}
				Stack<Connection> stack = delayThreadLocal.get();
				stack.push(connectionThreadLocal.get());
				delayThreadLocal.set(stack);
				//移除后会获取自动提交的连接
				connectionThreadLocal.remove();
			}
		}
		//PROPAGATION_NEVER：以非事务的方式运行，如果当前存在事务，则抛出异常。
		else if (status.propagationLevel == PropagationLevelConstant.PROPAGATION_NEVER) {
			if(status.isTrans) {
				throw new RuntimeException("事务传播方式为PROPAGATION_NEVER，但当前存在事务");
			}
		}
		//PROPAGATION_NESTED：如果没有，就新建一个事务；如果有，就在当前事务中嵌套其他事务。
		else if (status.propagationLevel == PropagationLevelConstant.PROPAGATION_NESTED) {
			if(!status.isTrans) {
				doCreateTransaction(status.isolationLevel);
			}
		}
    	
	}
    /**
     * 新建一个事务。获取一个新的连接，设置位非自动提交，并存放在connectionThreadLocal中
     * @param level
     * @throws SQLException
     */
	private void doCreateTransaction(Integer level) throws SQLException {
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
	
	
    /**
     * 提交事务
     */
    @Override
    public void commit(TransactionStatus status) throws SQLException{
    	//如果当前不存在上级事务，则提交事务
    	if(!status.isTrans) {
    		Connection connection = connectionThreadLocal.get();
            if(connection != null && !connection.isClosed() && !connection.getAutoCommit()){
                connection.commit();
            }
    	}
    	//如果当前存在上级事务，且传播行为为PROPAGATION_REQUIRES_NEW或PROPAGATION_NESTED，也进行自动提交
    	else if (status.isTrans && status.propagationLevel == PropagationLevelConstant.PROPAGATION_REQUIRES_NEW) {
    		Connection connection = connectionThreadLocal.get();
            if(connection != null && !connection.isClosed() && !connection.getAutoCommit()){
                connection.commit();
            }
		}
    	else if (status.isTrans && status.propagationLevel == PropagationLevelConstant.PROPAGATION_NESTED) {
    		Connection connection = connectionThreadLocal.get();
            if(connection != null && !connection.isClosed() && !connection.getAutoCommit()){
                connection.commit();
            }
		}
    	
    }
    /**
     * 回滚事务
     */
    @Override
    public void rollback() throws SQLException{
    	//如果为非自动提交，则回滚事务
    	Connection connection = connectionThreadLocal.get();
        if(connection != null && !connection.isClosed() && !connection.getAutoCommit()){
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
        if(connection != null  && !connection.isClosed() && connection.getAutoCommit()){
        	dataSource.removeConnection(connection);
        	//连接设为null
            connectionThreadLocal.remove();
        }
    }
    
  /**
   * 关闭事务，恢复连接未自动提交和默认隔离级别，然后关闭连接，关闭链接前，若设置了自动提交为false，则必须进行回滚操作
   */
    @Override
    public void closeTransaction(TransactionStatus status) throws SQLException{
    	Connection connection = connectionThreadLocal.get();
        //如果当前不存在上级事务，且连接未非自动提交，则关闭事务
        if(!status.isTrans && connection != null && !connection.isClosed() && !connection.getAutoCommit()){
        	connection.rollback();
        	connection.setAutoCommit(autoCommit);
        	connection.setTransactionIsolation(level);
        	//放回连接池
        	dataSource.removeConnection(connection);
        	//清除线程本地变量
        	connectionThreadLocal.remove();
        }
        //如果当前存在上级事务，且传播行为为PROPAGATION_REQUIRES_NEW，则关闭事务，并且将上级事务连接恢复到connectionThreadLocal中
        else if (status.isTrans && status.propagationLevel == PropagationLevelConstant.PROPAGATION_REQUIRES_NEW) {
        	connection.rollback();
        	connection.setAutoCommit(autoCommit);
        	connection.setTransactionIsolation(level);
        	dataSource.removeConnection(connection);
        	connectionThreadLocal.remove();
        	//恢复上级事务连接
        	connectionThreadLocal.set(delayThreadLocal.get().pop());
        }
        //如果当前存在上级事务，且传播行为为PROPAGATION_NOT_SUPPORTED，则直接将上级事务连接恢复到connectionThreadLocal中
        else if (status.isTrans && status.propagationLevel == PropagationLevelConstant.PROPAGATION_NOT_SUPPORTED) {
        	connectionThreadLocal.set(delayThreadLocal.get().pop());
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
			if(connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
				transactionId = connection.toString();
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return transactionId;
	}
}