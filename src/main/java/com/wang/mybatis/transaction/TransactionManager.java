package com.wang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionManager {
    //获取链接
    Connection getConnection() throws SQLException;
    //开启事务
    void beginTransaction(Integer level) throws SQLException;
    void beginTransaction() throws SQLException;
    //提交事务
    void commit() throws SQLException;
    //回滚事务
    void rollback() throws SQLException;
    //关闭连接
    void close() throws SQLException;
    //关闭事务
    void closeTransaction() throws SQLException;
    //设置事务隔离级别
    void setLevel(Integer level);
    //设置是否自动提交
    void setAutoCommit(Boolean autoCommit);
    //获取事务id
    String getTransactionId();
}
