package com.wang.demo.Service;

import javax.annotation.Resource;

import com.wang.spring.ioc.annotation.Autowired;
import com.wang.spring.ioc.annotation.Qualifier;
import com.wang.spring.ioc.annotation.Service;
import com.wang.spring.mvc.annotation.Controller;

@Service
public class UserPrintService{
	//@Autowired
	//@Qualifier("service1")
	@Resource
	IService userService;
	
	public void printUser() {
		userService.service();
	}
}
