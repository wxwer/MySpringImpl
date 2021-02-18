package com.wang.demo.Service;
import com.wang.demo.model.User;
import com.wang.spring.aop.annotation.Transaction;
import com.wang.spring.ioc.annotation.Autowired;
import com.wang.spring.ioc.annotation.Service;

@Service("service1")
public class Service1 implements IService{
	@Autowired
	Service2 service2;
	
	@Autowired
	UserMapper userMapper;
	
	@Override
	public User getUser(String name) {
		return new User("wang");
	}
	@Transaction
	@Override
	public void service() {
		System.out.println(service2.getAllUser());
		System.out.println("这是第一个实现");
		
		System.out.println("userMapper的查询结果为："+userMapper.selectUsers(0));
		
	}
}
