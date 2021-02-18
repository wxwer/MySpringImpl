package com.wang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionManager {
    /**获取链接*/
    Connection getConnection() throws SQLException;
    /**提交*/
    void commit() throws SQLException;
    /**回滚*/
    void rollback() throws SQLException;
    /**关闭*/
    void close() throws SQLException;
}
