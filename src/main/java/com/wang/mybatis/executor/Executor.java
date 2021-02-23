package com.wang.mybatis.executor;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import com.wang.mybatis.transaction.TransactionStatus;

/**
 * 执行器接口
 * @author Administrator
 *
 */
public interface Executor {
	//执行select语句
	<E> List<E> select(Method method,Object[] args) throws Exception;
	//执行update,delete,insert语句
	int update(Method method,Object[] args)throws SQLException;
	//提交事务
    void commit(TransactionStatus status) throws SQLException;
    //回滚事务
    void rollback() throws SQLException;
    //释放连接
    void close() throws SQLException;
}
