package com.wang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionManager {
    //获取链接
    Connection getConnection() throws SQLException;
    //当前是否存在事务
    public boolean isTransactionPresent();
    //开启事务
    public void beginTransaction(TransactionStatus status) throws SQLException;
    //提交事务
    void commit(TransactionStatus status) throws SQLException;
    //回滚事务
    void rollback() throws SQLException;
    //关闭连接
    void close() throws SQLException;
    //关闭事务
    void closeTransaction(TransactionStatus status) throws SQLException;
    //设置事务隔离级别
    void setLevel(Integer level);
    //设置是否自动提交
    void setAutoCommit(Boolean autoCommit);
    //获取事务id
    String getTransactionId();
}
