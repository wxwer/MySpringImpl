package com.wang.demo.Service;

import java.util.ArrayList;
import java.util.List;

import com.wang.demo.model.User;
import com.wang.spring.ioc.annotation.Autowired;
import com.wang.spring.ioc.annotation.Service;

import redis.clients.jedis.Jedis;

@Service("service2")
public class Service2 implements IService{
	@Autowired
	Jedis jedis;
	
	@Override
	public void service() {
		// TODO Auto-generated method stub
		System.out.println("这是实现2");
		System.out.println("Jedis is "+jedis);
	}
	public List<User> getAllUser(){
		User user1 = new User("wang");
		User user2 = new User(4, "Li", 45);
		List<User> users  =new ArrayList<>();
		users.add(user1);
		users.add(user2);
		return users;
	}
	@Override
	public User getUser(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
