package com.wang.spring.constants;
/**
 * 配置参数名常量
 * @author Administrator
 *
 */
public class ConfigConstant {
    //配置文件的名称
    public static final String CONFIG_FILE = "application.properties";

    //数据源
    public static final String JDBC_DRIVER = "myspring.datasource.jdbc.driver";
    public static final String JDBC_URL = "myspring.datasource.jdbc.url";
    public static final String JDBC_USERNAME = "myspring.datasource.jdbc.username";
    public static final String JDBC_PASSWORD = "myspring.datasource.jdbc.password";
    
    //数据库连接池
    public static final String POOL_USEPOOL ="myspring.datasource.pool.usePool";
    public static final String POOL_MAXSIZE ="myspring.datasource.pool.maxSize";
    public static final String POOL_WAITTIMEMILL ="myspring.datasource.pool.waitTimeMill";
    //java源码地址
    public static final String APP_BASE_PACKAGE = "myspring.app.base_package";
    //jsp页面路径
    public static final String APP_JSP_PATH = "myspring.app.jsp_path";
    //静态资源路径
    public static final String APP_ASSET_PATH = "myspring.app.asset_path";
}
