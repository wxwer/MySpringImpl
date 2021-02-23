package com.wang.mybatis.transaction;

public class TransactionStatus {
	//是否需要开启事务
	public Boolean isNeed;
	//当前是否存在事务
	public Boolean isTrans;
	//事务隔离级别
	public Integer isolationLevel;
	//事务传播级别
	public Integer propagationLevel;
	//回滚异常类型
	public Class<? extends Throwable>[] rollbackFor;
	
}
