package com.wang.spring.utils;

import java.util.Properties;

import com.wang.spring.constants.ConfigConstant;


public class ConfigUtil {

    /**
     * 加载配置文件的属性
     */
    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    
    /**
     * 获取 JDBC 驱动
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER);
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL);
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
    }

    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
    }

    /**
     *  是否使用连接池
     */
    public static Boolean isDataSourcePool() {
        return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.POOL_USEPOOL,false);
    }
    /**
     * 获取连接池最大连接数
     */
    public static Integer getDataSourcePoolMaxSize() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.POOL_MAXSIZE,50);
    }
    /**
     * 获取阻塞等待时间
     */
    public static Long getDataSourcePoolWaitTimeMill() {
        return PropsUtil.getLong(CONFIG_PROPS, ConfigConstant.POOL_WAITTIMEMILL,10000L);
    }
    
    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * 获取应用 JSP 路径
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    /**
     * 获取应用静态资源路径
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, "/asset/");
    }

    /**
     * 根据属性名获取 String 类型的属性值
     */
    public static String getString(String key) {
        return PropsUtil.getString(CONFIG_PROPS, key);
    }

    /**
     * 根据属性名获取 int 类型的属性值
     */
    public static int getInt(String key) {
        return PropsUtil.getInt(CONFIG_PROPS, key);
    }

    /**
     * 根据属性名获取 boolean 类型的属性值
     */
    public static boolean getBoolean(String key) {
        return PropsUtil.getBoolean(CONFIG_PROPS, key);
    }
    
    /**
     * 根据属性名获取 float 类型的属性值
     */
    public static Float getFloat(String key) {
        return PropsUtil.getFloat(CONFIG_PROPS, key);
    }
    
    /**
     * 根据属性名获取 double 类型的属性值
     */
    public static Double getDouble(String key) {
        return PropsUtil.getDouble(CONFIG_PROPS, key);
    }
    
    /**
     * 根据属性名获取 long 类型的属性值
     */
    public static Long getLong(String key) {
        return PropsUtil.getLong(CONFIG_PROPS, key);
    }
}
