package com.wang.demo.Service;

import com.wang.spring.ioc.annotation.Component;

@Component
public class Jedis1{
	String host="localhost";
	String port="6379";
	public Jedis1() {
		// TODO Auto-generated constructor stub
	}
	public Jedis1(String host,String port) {
		// TODO Auto-generated constructor stub
		this.host=host;
		this.port=port;
	}
	@Override
	public String toString() {
		return host+":"+port;
	}
}
