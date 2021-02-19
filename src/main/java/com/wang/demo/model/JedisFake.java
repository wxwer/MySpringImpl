package com.wang.demo.model;

import com.wang.spring.annotation.ioc.Component;

@Component
public class JedisFake{
	String host="localhost";
	String port="6379";
	public JedisFake() {
		// TODO Auto-generated constructor stub
	}
	public JedisFake(String host,String port) {
		// TODO Auto-generated constructor stub
		this.host=host;
		this.port=port;
	}
	@Override
	public String toString() {
		return host+":"+port;
	}
}
