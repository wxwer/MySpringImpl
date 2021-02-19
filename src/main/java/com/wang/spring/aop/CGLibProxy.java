package com.wang.spring.aop;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.wang.mybatis.transaction.TransactionFactory;
import com.wang.mybatis.transaction.TransactionManager;
import com.wang.spring.annotation.aop.Transaction;
import com.wang.spring.common.MyProxy;
import com.wang.spring.constants.AdviceTypeConstant;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
/**
 * 使用CGLib实现的代理类，对目标进行增强
 * @author Administrator
 *
 */
public class CGLibProxy implements MethodInterceptor,MyProxy{
	//目标代理方法和增强列表的映射
	Map<Method, Map<String, List<Advice>>> methodAdvicesMap=null;
	//事务管理器
	TransactionManager transactionManager = TransactionFactory.newTransaction();
	
	public CGLibProxy(Map<Method, Map<String, List<Advice>>> methodAdvicesMap) {
		// TODO Auto-generated constructor stub
		this.methodAdvicesMap=methodAdvicesMap;
	}
	/**
	 * 通过CGLib库生成代理类
	 * @param cls
	 * @return
	 */
	@Override
	public Object getProxy(Class<?> cls) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(cls);
		enhancer.setCallback(this);
		return enhancer.create();
	}
	/**
	 * 拦截目标代理方法，对目标方法进行增强
	 */
	@Override
	public Object intercept(Object object, Method method, Object[] arg2, MethodProxy methodProxy) throws Throwable {
		Object result=null;
		//判断是否增强
		Map<String, List<Advice>> advices =  methodAdvicesMap.get(method);
		try {
			//开启事务
			beginTransaction(object, method);
			//前置增强
			invokeAdvice(method,advices,AdviceTypeConstant.BEFORE);
			//环绕增强
			if(isAdviceNeed(method) && advices!=null && advices.containsKey(AdviceTypeConstant.AROUND)) {
				List<Advice> aroundAdvices = advices.get(AdviceTypeConstant.AROUND);
				if(aroundAdvices!=null && !aroundAdvices.isEmpty()) {
					Advice advice = aroundAdvices.get(0);
					JoinPoint joinPoint = new JoinPoint(object, methodProxy, arg2);
					Method adviceMethod = advice.getAdviceMethod();
					Object aspect = advice.getAspect();
					result=adviceMethod.invoke(aspect,joinPoint);
				}
				else {
					result= methodProxy.invokeSuper(object, arg2);
				}
			}
			else {
				result= methodProxy.invokeSuper(object, arg2);
			}
			//后置增强
			invokeAdvice(method,advices, AdviceTypeConstant.AFTER);
			commitTransaction(object, method);
		} 
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			//回滚事务
			rollbackTransaction(object, method);
			//异常增强
			invokeAdvice(method,advices, AdviceTypeConstant.AFTERTHROWING);
		}
		finally {
			closeTransaction(object, method);
			//返回前增强
			invokeAdvice(method,advices, AdviceTypeConstant.AFTERRETURNING);
		}
		return result;
	}
	//执行通知增强
	private void invokeAdvice(Method method,Map<String, List<Advice>> advices,String type) throws Throwable {
		if(isAdviceNeed(method) && advices!=null && advices.containsKey(type)) {
			List<Advice> adviceList=advices.get(type);
			if(adviceList!=null && !adviceList.isEmpty()) {
				for(Advice advice:adviceList) {
					Method adviceMethod = advice.getAdviceMethod();
					Object aspect = advice.getAspect();
					adviceMethod.invoke(aspect);
				}
			}
		}
		
	}
	//判断是否增强
	private boolean isAdviceNeed(Method method) {
		return methodAdvicesMap.containsKey(method);
	}
	
	//判断是否需要事务管理
	private boolean isTrasactionNeed(Object object,Method method) {
		if(transactionManager!=null && (object.getClass().isAnnotationPresent(Transaction.class) || method.isAnnotationPresent(Transaction.class))) {
			return true;
		}
		return false;
	}
	
	private void beginTransaction(Object object,Method method) throws SQLException {
		if(isTrasactionNeed(object, method)) {
			transactionManager.beginTransaction();
			System.out.println("开启事务，id="+transactionManager.getTransactionId());
		}
	}
	private void commitTransaction(Object object,Method method) throws SQLException {
		if(isTrasactionNeed(object, method)) {
			transactionManager.commit();
			System.out.println("提交事务，id="+transactionManager.getTransactionId());
		}
	}
	private void rollbackTransaction(Object object,Method method) throws SQLException {
		if(isTrasactionNeed(object, method)) {
			transactionManager.rollback();
			System.out.println("回滚事务，id="+transactionManager.getTransactionId());
		}
	}
	private void closeTransaction(Object object,Method method) throws SQLException {
		if(isTrasactionNeed(object, method)) {
			System.out.println("释放事务，id="+transactionManager.getTransactionId());
			transactionManager.closeTransaction();
		}
	}
}
