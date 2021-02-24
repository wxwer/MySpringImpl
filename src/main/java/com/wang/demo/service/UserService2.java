package com.wang.demo.service;

import com.wang.demo.dao.UserMapper;
import com.wang.spring.annotation.ioc.Autowired;
import com.wang.spring.annotation.ioc.Service;


@Service
public class UserService2 {
	@Autowired
	public UserService userService;
	
	@Autowired
	UserMapper userMapper;
}
