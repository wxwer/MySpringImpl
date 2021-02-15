package com.wang.demo.Service;
import com.wang.demo.model.User;
import com.wang.spring.ioc.annotation.Service;

@Service("service1")
public class Service1 implements IService{
	@Override
	public User getUser(String name) {
		return new User("wang");
	}

	@Override
	public void service() {
		System.out.println("这是第一个实现");
		
	}
}
