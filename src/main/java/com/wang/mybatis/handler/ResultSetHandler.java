package com.wang.mybatis.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Blob;
/**
 * 对结果集进行解析，完成对象关系映射
 * @author Administrator
 *
 */
public class ResultSetHandler {

    //转换的目标类型
    Class<?> typeReturn;

    //待转换的ResultSet
    ResultSet resultSet;

    Boolean hasSet;

    public ResultSetHandler(Class<?> typeReturn,ResultSet resultSet){
        this.resultSet = resultSet;
        this.typeReturn = typeReturn;
    }
    /**
     * 将结果集resultSet映射为一个对象列表
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> handle() throws Exception{
        List<T> res = new ArrayList<>();
        while (resultSet.next()){
        	/** 若返回是基础数据类型 */
            if(String.class.equals(typeReturn)){
                String val = resultSet.getString(1);
                if(val != null){
                    res.add((T)val);
                }
            }
            else if(Integer.class.equals(typeReturn) || int.class.equals(typeReturn)){
                Integer val = resultSet.getInt(1);
                if(val != null){
                    res.add((T)val);
                }
            }
            else if(Float.class.equals(typeReturn) || float.class.equals(typeReturn)){
                Float val = resultSet.getFloat(1);
                if(val != null){
                    res.add((T)val);
                }
                
            }
            else if(Double.class.equals(typeReturn) || double.class.equals(typeReturn)){
            	Double val = resultSet.getDouble(1);
                if(val != null){
                    res.add((T)val);
                }
            }
            else {
            	Object val = generateObjFromResultSet(resultSet, typeReturn);
                if(val != null){
                    res.add((T)val);
                }
            }
        }
        return res;
    }
    /**
     * 生成具体对象，并对属性进行填充
     * @param resultSet
     * @param clazz
     * @return
     * @throws Exception
     */
    private Object generateObjFromResultSet(ResultSet resultSet,Class<?> clazz) throws Exception{
        Constructor[] constructors = clazz.getConstructors();
        Constructor usedConstructor = null;
        for(Constructor constructor:constructors){
            if(constructor.getParameterCount() == 0){
                usedConstructor = constructor;
                break;
            }
        }
        if(constructors == null) {
            throw new RuntimeException(typeReturn + " is not empty constructor");
        }
        Object object = usedConstructor.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fname = field.getName();
            Type type = field.getGenericType();
            if (type.equals(String.class)) {
                String column = resultSet.getString(fname);
                field.set(object, column);
            }
            else if (type.equals(Integer.class)) {
                Integer column = resultSet.getInt(fname);
                field.set(object, column);
            }
            else if (type.equals(Long.class)) {
                Long column = resultSet.getLong(fname);
                field.set(object, column);
            }
            else if (type.equals(Float.class)) {
            	Float column = resultSet.getFloat(fname);
                field.set(object, column);
            }
            else if (type.equals(Double.class)) {
            	Double column = resultSet.getDouble(fname);
                field.set(object, column);
            }
            else if (type.equals(BigDecimal.class)) {
            	BigDecimal column = resultSet.getBigDecimal(fname);
                field.set(object, column);
            }
            else if (type.equals(Blob.class)) {
            	Blob column = (Blob) resultSet.getBlob(fname);
                field.set(object, column);
            }
            else if (type.equals(Boolean.class)) {
            	Boolean column = resultSet.getBoolean(fname);
                field.set(object, column);
            }
            else if (type.equals(Date.class)) {
            	Date column = resultSet.getDate(fname);
                field.set(object, column);
            }
            else if (type.equals(byte[].class)) {
            	byte[] column = resultSet.getBytes(fname);
                field.set(object, column);
            }
        }
        return object;
    }
}