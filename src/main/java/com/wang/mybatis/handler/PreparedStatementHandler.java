package com.wang.mybatis.handler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.wang.mybatis.core.MapperCore;
import com.wang.mybatis.core.MethodDetails;
import com.wang.mybatis.core.SqlSource;
import com.wang.mybatis.transaction.TransactionManager;


public class PreparedStatementHandler {
    /**
     * 全局核心mapper解析类
     */
    private MapperCore mapperCore;

    /**
     * 待执行的方法
     */
    private Method method;

    /**
     * 事务
     */
    private TransactionManager transactionManager;

    /**
     * 真实连接
     */
    private Connection connection;

    /**
     * 方法输入参数
     */
    private Object[] args;

    public PreparedStatementHandler(MapperCore mapperCore, TransactionManager transactionManager,Method method, Object[] args)throws SQLException {
        this.mapperCore = mapperCore;
        this.method = method;
        this.transactionManager = transactionManager;
        this.args = args;
        this.connection = transactionManager.getConnection();
    }

    /**
     * @Author xiabing
     * @Desc 参数处理核心方法 todo
     **/
    public PreparedStatement generateStatement() throws SQLException{
    	MethodDetails methodDetails = mapperCore.getMethodDetails(method);
        SqlSource sqlSource = methodDetails.getSqlSource();
        Class<?>[] clazzes = methodDetails.getParameterTypes();
        List<String> paramNames = methodDetails.getParameterNames();
        List<String> params = sqlSource.getParam();
        List<Integer> paramInjectTypes = sqlSource.getParamInjectType();
        
        String sql = sqlSource.getSql();
        String parsedSql = parseSql(sql, clazzes, paramNames, params, paramInjectTypes, args);
        PreparedStatement preparedStatement = connection.prepareStatement(parsedSql);
        preparedStatement = typeInject(preparedStatement,clazzes,paramNames,params,paramInjectTypes,args);
        return preparedStatement;
    }
    
    private String parseSql(String sql,Class<?>[] clazzes,List<String> paramNames,List<String> params,List<Integer> paramInjectTypes,Object[] args) {
    	StringBuilder sqlBuilder = new StringBuilder(sql);
    	Integer index = sqlBuilder.indexOf("?");
    	Integer i = 0;
    	while (index>0 && i<paramInjectTypes.size()) {
			if(paramInjectTypes.get(i)==1) {
				i++;
				continue;
			}
			String param = params.get(i);
			Integer paramIndex = paramNames.indexOf(param);
			Object arg = args[paramIndex];
			Class<?> type = clazzes[paramIndex];
			String injectValue = "";
			if(String.class.equals(type)) {
				injectValue = "'"+(String) arg + "'";
			}
			else if (Integer.class.equals(type)) {
				injectValue = Integer.toString((Integer) arg);
			}
			else if (Float.class.equals(type)) {
				injectValue = Float.toString((Float) arg);
			}
			else if (Double.class.equals(type)) {
				injectValue = Double.toString((Double) arg);
			}
			sqlBuilder.replace(index, index+1, injectValue);
			index = sqlBuilder.indexOf("?");
			i++;
		}
		return sqlBuilder.toString();
    	
    }
    
    /**
    *preparedStatement构建
     * @Param preparedStatement 待构建的preparedStatement
     * @Param clazzes 该方法中参数类型数组
     * @Param paramNames 该方法中参数名称列表,若有@Param注解，则为此注解的值，默认为类名首字母小写
     * @Param params 待注入的参数名，如user.name或普通类型如name
     * @Param args 真实参数值
     **/
    private PreparedStatement typeInject(PreparedStatement preparedStatement,Class<?>[] clazzes,List<String> paramNames,List<String> params,List<Integer> paramInjectTypes,Object[] args)throws SQLException{

        for(int i = 0; i < paramNames.size(); i++){
            String paramName = paramNames.get(i);
            Class type = clazzes[i];
            int injectIndex = params.indexOf(paramName);
            if(paramInjectTypes.get(injectIndex)==0) {
            	continue;
            }
            if(String.class.equals(type)){
            	/**此处是判断sql中是否有待注入的名称({name})和方法内输入对象名(name)相同，若相同，则直接注入*/
                if(injectIndex >= 0){
                    preparedStatement.setString(injectIndex+1,(String)args[i]);
                }
            }else if(Integer.class.equals(type) || int.class.equals(type)){
                if(injectIndex >= 0){
                    preparedStatement.setInt(injectIndex+1,(Integer)args[i]);
                }
            }else if(Float.class.equals(type) || float.class.equals(type)){
                if(injectIndex >= 0){
                    preparedStatement.setFloat(injectIndex+1,(Float)args[i]);
                }
            }
            else if(Double.class.equals(type) || double.class.equals(type)){
                if(injectIndex >= 0){
                    preparedStatement.setDouble(injectIndex+1,(Double)args[i]);
                }
            }
        }
        return preparedStatement;
    }

    public void closeConnection() throws SQLException{
        transactionManager.close();
    }
    /*
    public static void main(String[] args) {
    	String sql = "select * from user where id=? and name= ?";
    	Class<?>[] clazzes = {Integer.class,String.class};
    	List<String> paramNames = new ArrayList<>();
    	paramNames.add("id");
    	paramNames.add("name");
    	
    	List<String> params = new ArrayList<>();
    	params.add("id");
    	params.add("name");
    	List<Integer> paramInjectTypes = new ArrayList<>();
    	paramInjectTypes.add(0);
    	paramInjectTypes.add(0);
    	Object[] args2 = {11,"wang"};
    	String parsedSql = PreparedStatementHandle.parseSql(sql, clazzes, paramNames, params, paramInjectTypes, args2);
    	System.out.println(parsedSql);
    }
    */
}
