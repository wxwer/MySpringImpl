package com.wang.demo.Service;
import com.wang.demo.dao.UserMapper;
import com.wang.demo.model.User;
import com.wang.spring.annotation.aop.Transaction;
import com.wang.spring.annotation.ioc.Autowired;
import com.wang.spring.annotation.ioc.Service;

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
		
		System.out.println("userMapper的查询结果1为："+userMapper.selectUsers(0));
		System.out.println("userMapper的查询结果2为："+userMapper.selectUsers(1));
		
	}
}
