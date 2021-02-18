package com.wang.mybatis.execute;

import java.sql.SQLException;

public interface Executor {

    //<T> T getMapper(Class<T> type);

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
