package com.wang.spring.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropsUtil {

  /**
   * 加载属性文件
   */
  public static Properties loadProps(String fileName) {
      Properties props = null;
      InputStream is = null;
      try {
          is = ClassUtil.getClassLoader().getResourceAsStream(fileName);
          if (is == null) {
              throw new FileNotFoundException(fileName + " file is not found");
          }
          props = new Properties();
          props.load(is);
      } catch (IOException e) {
      } finally {
          if (is != null) {
              try {
                  is.close();
              } catch (IOException e) {
              }
          }
      }
      return props;
  }

  /**
   * 获取 String 类型的属性值（默认值为空字符串）
   */
  public static String getString(Properties props, String key) {
      return props.getProperty(key);
  }

  /**
   * 获取 String 类型的属性值（可指定默认值）
   */
  public static String getString(Properties props, String key, String defaultValue) {
      String value = defaultValue;
      if (props.containsKey(key)) {
          value = props.getProperty(key);
      }
      return value;
  }

  /**
   * 获取 int 类型的属性值（默认值为 0）
   */
  public static int getInt(Properties props, String key) {
      return Integer.parseInt(props.getProperty(key));
  }

  /**
   * 获取 int 类型的属性值（可指定默认值）
   */
  public static int getInt(Properties props, String key, int defaultValue) {
      int value = defaultValue;
      if (props.containsKey(key)) {
          value = Integer.parseInt(props.getProperty(key));
      }
      return value;
  }

  /**
   * 获取 boolean 类型属性（默认值为 false）
   */
  public static boolean getBoolean(Properties props, String key) {
      return Boolean.parseBoolean(props.getProperty(key));
  }

  /**
   * 获取 boolean 类型属性（可指定默认值）
   */
  public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
      boolean value = defaultValue;
      if (props.containsKey(key)) {
          value = Boolean.parseBoolean(props.getProperty(key));
      }
      return value;
  }
  
  /**
   * 获取 boolean 类型属性（默认值为 false）
   */
  public static Long getLong(Properties props, String key) {
      return Long.parseLong(props.getProperty(key));
  }

  /**
   * 获取 boolean 类型属性（可指定默认值）
   */
  public static Long getLong(Properties props, String key, Long defaultValue) {
      Long value = defaultValue;
      if (props.containsKey(key)) {
          value =Long.parseLong(props.getProperty(key));
      }
      return value;
  }
  
  /**
   * 获取 float 类型属性
   */
  public static Float getFloat(Properties props, String key) {
      return Float.parseFloat(props.getProperty(key));
  }

  /**
   * 获取 float 类型属性（可指定默认值）
   */
  public static Float getFloat(Properties props, String key, Float defaultValue) {
	  Float value = defaultValue;
      if (props.containsKey(key)) {
          value =Float.parseFloat(props.getProperty(key));
      }
      return value;
  }
  
  /**
   * 获取 double 类型属性
   */
  public static Double getDouble(Properties props, String key) {
      return Double.parseDouble(props.getProperty(key));
  }

  /**
   * 获取 Double 类型属性（可指定默认值）
   */
  public static Double getDouble(Properties props, String key, Double defaultValue) {
	  Double value = defaultValue;
      if (props.containsKey(key)) {
          value =Double.parseDouble(props.getProperty(key));
      }
      return value;
  }
  
}
