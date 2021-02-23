package com.wang.mybatis.executor;

/**
 * 执行器工厂
 * @author Administrator
 *
 */
public class ExecutorFactory {
	public static Executor getExecutor(){
		Executor executor = new SimpleExecutor(false, false);
		return executor;
	}
}
