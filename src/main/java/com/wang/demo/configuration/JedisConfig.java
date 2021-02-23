package com.wang.demo.configuration;
import com.wang.spring.annotation.ioc.Bean;
import com.wang.spring.annotation.ioc.Configuration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
	@Bean
	public Jedis getJedis() {
		JedisPoolConfig config = new JedisPoolConfig();
	    config.setMaxTotal(100);
	    config.setMaxIdle(50);
	    config.setMinIdle(10);
	    //设置连接时的最大等待毫秒数
	    config.setMaxWaitMillis(10000);
	    //设置在获取连接时，是否检查连接的有效性
	    config.setTestOnBorrow(true);
	    //设置释放连接到池中时是否检查有效性
	    config.setTestOnReturn(true);

	    //在连接空闲时，是否检查连接有效性
	    config.setTestWhileIdle(true);

	    //两次扫描之间的时间间隔毫秒数
	    config.setTimeBetweenEvictionRunsMillis(30000);
	    //每次扫描的最多的对象数
	    config.setNumTestsPerEvictionRun(10);
	    //逐出连接的最小空闲时间，默认是180000（30分钟）
	    config.setMinEvictableIdleTimeMillis(60000);
	    String redisHost = "" ;//Jedis服务器地址
	    Integer port = 6379;
	    String password = "";
	    Jedis resource = new JedisPool(config, redisHost, 6379, 30000, password, 0).getResource();
	    return resource;
	}
	
	
}
