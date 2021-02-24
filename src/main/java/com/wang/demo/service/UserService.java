package com.wang.demo.service;

import com.alibaba.fastjson.JSON;
import com.wang.demo.dao.UserMapper;
import com.wang.demo.model.User;
import com.wang.demo.model.UserRequest;
import com.wang.spring.annotation.aop.Transactional;
import com.wang.spring.annotation.ioc.Autowired;
import com.wang.spring.annotation.ioc.Service;
import com.wang.spring.annotation.ioc.Value;
import com.wang.spring.constants.PropagationLevelConstant;

import redis.clients.jedis.Jedis;
@Service
public class UserService implements IUserService{
	
	@Autowired
	UserMapper userMapper;
	
	@Autowired
	Jedis jedis;
	
	@Autowired
	public UserService2 userservice2;
	
	@Override
	public boolean registerUser(UserRequest request) {
        try{
            userMapper.insertUser(request.getUserName(), request.getPassword(), System.currentTimeMillis());
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
		return false;
	}

	@Override
	public User loginUser(UserRequest request) {
		User user = userMapper.selectUser(request.getUserName());
		if(null==user || !user.getPassword().equals(request.getPassword())) {
			return null;
		}
		String key = "login:"+user.getUserName();
		jedis.set(key, JSON.toJSONString(user));
		System.out.println(user.getUserName()+" 登录成功");
		return user;
	}

	@Override
	public boolean isUserLogin(String username) {
		String key = "login:"+username;
		return jedis.exists(key);
	}

	@Override
	public boolean logout(String username) {
		// TODO Auto-generated method stub
		String key = "login:"+username;
		if(!jedis.exists(key)) {
			return false;
		}
		jedis.del(key);
		return true;
	}

	
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public User findUser1(Integer id1) throws Exception {
		// TODO Auto-generated method stub
		return userMapper.selectUser(id1);
	}
	
	@Override
	@Transactional
	public User findUser2(Integer id2) {
		// TODO Auto-generated method stub
		return userMapper.selectUser(id2);
	}

	@Override
	//@Transactional
	public User findUser(Integer id) throws Exception {
		// TODO Auto-generated method stub
		User user1 = findUser1(id);
		
		User user2 = findUser2(id);
		return user1;
		
	}
}
