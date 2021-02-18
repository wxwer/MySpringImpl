package com.wang.mybatis.execute;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.wang.mybatis.bean.SqlResultCache;
import com.wang.mybatis.core.MapperCore;
import com.wang.mybatis.handler.PreparedStatementHandler;
import com.wang.mybatis.handler.ResultSetHandler;
import com.wang.mybatis.transaction.TransactionFactory;
import com.wang.mybatis.transaction.TransactionManager;

public class SimpleExecutor implements Executor{


    public MapperCore mapperCore;

    public DataSource dataSource;

    public TransactionManager transactionManager;

    public SqlResultCache sqlResultCache;

    public SimpleExecutor(MapperCore mapperCore,DataSource dataSource,boolean openTransaction,boolean openCache){
    	
        this.mapperCore =mapperCore;
        this.dataSource = dataSource;
        if(openCache){
            this.sqlResultCache = new SqlResultCache();
        }
        if(openTransaction){
            this.transactionManager = TransactionFactory.newTransaction(dataSource,Connection.TRANSACTION_REPEATABLE_READ,false);
        }else {
            this.transactionManager = TransactionFactory.newTransaction(dataSource,null,null);
        }
    }
    /*
    public <T> T getMapper(Class<T> type){
        MapperProxy mapperProxy = new MapperProxy(this);
        return (T)mapperProxy.getProxy(type);
    }
	*/
    public <E> List<E> select(Method method,Object[] args) throws Exception{
        String cacheKey = generateCacheKey(method,args);
        if(sqlResultCache != null && sqlResultCache.getCache(cacheKey) != null){
            System.out.println("this is cache");
            return (List<E>)sqlResultCache.getCache(cacheKey);
        }

        PreparedStatementHandler preparedStatementHandler = new PreparedStatementHandler(mapperCore,transactionManager,method,args);
        PreparedStatement preparedStatement = (PreparedStatement) preparedStatementHandler.generateStatement();
        ResultSet resultSet = null;
        preparedStatement.executeQuery();
        resultSet = preparedStatement.getResultSet();

        Class returnClass = mapperCore.getMethodDetails(method).getReturnType();
        if(returnClass == null || void.class.equals(returnClass)){
            preparedStatement.close();
            preparedStatementHandler.closeConnection();
            return null;
        }else {
            ResultSetHandler resultSetHandler = new ResultSetHandler(returnClass,resultSet);
            List<E> res = resultSetHandler.handle();
            if(sqlResultCache != null){
            	sqlResultCache.putCache(cacheKey,res);
            }
            preparedStatement.close();
            resultSet.close();
            preparedStatementHandler.closeConnection();
            return res;
        }
    }

    public int update(Method method,Object[] args)throws SQLException{
        PreparedStatementHandler preparedStatementHandler = null;
        PreparedStatement preparedStatement = null;
        Integer count = 0;

        if(sqlResultCache != null){
        	sqlResultCache.cleanCache();
        }
        try{
            preparedStatementHandler = new PreparedStatementHandler(mapperCore,transactionManager,method,args);
            preparedStatement = (PreparedStatement) preparedStatementHandler.generateStatement();
            count = preparedStatement.executeUpdate();
        }finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            preparedStatementHandler.closeConnection();
        }

        return count;
    }

    @Override
    public void commit() throws SQLException {
        transactionManager.commit();
    }

    @Override
    public void rollback() throws SQLException {
        transactionManager.rollback();
    }

    @Override
    public void close() throws SQLException {
        transactionManager.close();
    }

    private String generateCacheKey(Method method, Object args[]){
        StringBuilder cacheKey = new StringBuilder(method.getDeclaringClass().getName() + method.getName());
        for(Object o:args){
            cacheKey.append(o.toString());
        }
        return cacheKey.toString();
    }
}
