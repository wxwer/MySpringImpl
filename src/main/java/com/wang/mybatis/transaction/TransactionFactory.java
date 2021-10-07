package com.wang.mybatis.transaction;

import com.wang.mybatis.datasource.DataSource;
import com.wang.mybatis.datasource.NormalDataSource;
import com.wang.mybatis.datasource.PoolDataSource;
import com.wang.spring.utils.ConfigUtil;

/**
 * TransactionManager工厂类
 * @author Administrator
 *
 */
public class TransactionFactory {
	private static volatile TransactionManager transaction = null;
	/**
	 * 生成一个TransactionManager实例，并且是单例的
	 * @param level
	 * @param autoCommmit
	 * @return
	 */
    public static TransactionManager newTransaction(Integer level, Boolean autoCommmit){
    	if(transaction==null) {
			synchronized (TransactionManager.class) {
				 if(transaction==null){
					DataSource dataSource = null;
					//根据配置决定是否使用数据库连接池
					if(ConfigUtil.isDataSourcePool()) {
						dataSource  =new PoolDataSource(ConfigUtil.getJdbcDriver(),  ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword(),
								ConfigUtil.getDataSourcePoolMaxSize(),ConfigUtil.getDataSourcePoolWaitTimeMill());
					}
					else {
						dataSource = new  NormalDataSource(ConfigUtil.getJdbcDriver(),  ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword());
					}
					 
					transaction = new SimpleTransactionManager(dataSource,level,autoCommmit);
					return transaction;
				}
			}
		}
    	return transaction;
    }
    
    public static TransactionManager newTransaction(){
    	 if(transaction==null) {
			synchronized (TransactionManager.class) {
				 if(transaction==null){
					DataSource dataSource = null;
					if(ConfigUtil.isDataSourcePool()) {
						dataSource  =new PoolDataSource(ConfigUtil.getJdbcDriver(),  ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword(),
								ConfigUtil.getDataSourcePoolMaxSize(),ConfigUtil.getDataSourcePoolWaitTimeMill());
					}
					else {
						dataSource = new  NormalDataSource(ConfigUtil.getJdbcDriver(),  ConfigUtil.getJdbcUrl(),  ConfigUtil.getJdbcUsername(),  ConfigUtil.getJdbcPassword());
					}
					transaction = new SimpleTransactionManager(dataSource);
					return transaction;
				}
			}
		}
    	return transaction;
    }
}
