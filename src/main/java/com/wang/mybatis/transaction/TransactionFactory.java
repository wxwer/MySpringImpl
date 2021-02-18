package com.wang.mybatis.transaction;

import javax.sql.DataSource;
public class TransactionFactory {
	private static TransactionManager transaction = null;
    public static TransactionManager newTransaction(DataSource dataSource, Integer level, Boolean autoCommmit){
    	synchronized (TransactionManager.class) {
			if(transaction==null) {
				synchronized (TransactionManager.class) {
					transaction = new SimpleTransactionManager(dataSource,level,autoCommmit);
				}
			}
		}
    	return transaction;
    }
}
