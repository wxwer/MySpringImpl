package com.wang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class SimpleTransactionManager implements TransactionManager{

    private Connection connection; // 数据库连接
    private DataSource dataSource; // 数据源
    private Integer level = Connection.TRANSACTION_REPEATABLE_READ;; // 事务隔离级别
    private Boolean autoCommmit = true; // 是否自动提交

    public SimpleTransactionManager(DataSource dataSource){
        this(dataSource,null,null);
    }

    public SimpleTransactionManager(DataSource dataSource, Integer level, Boolean autoCommmit) {
        this.dataSource = dataSource;
        if(level != null){
            this.level = level;
        }
        if(autoCommmit != null){
            this.autoCommmit = autoCommmit;
        }
    }

    @Override
    public Connection getConnection() throws SQLException{
        this.connection = dataSource.getConnection();
        this.connection.setAutoCommit(autoCommmit);
        this.connection.setTransactionIsolation(level);

        return this.connection;
    }

    @Override
    public void commit() throws SQLException{
        if(this.connection != null){
            this.connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException{
        if(this.connection != null){
            this.connection.rollback();
        }
    }

    /**关闭链接前，若设置了自动提交为false，则必须进行回滚操作*/
    @Override
    public void close() throws SQLException{
        if(!autoCommmit && connection != null){
           connection.rollback();
        }
        /**放回连接池*/
        if(connection != null){
            //dataSource.removeConnection(connection);
        	connection.close();
        }
        /**链接设为null*/
        this.connection = null;
    }
}
