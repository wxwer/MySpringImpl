package com.wang.demo.Service;

import javax.annotation.Resource;

import com.wang.spring.annotation.ioc.Autowired;
import com.wang.spring.annotation.ioc.Qualifier;
import com.wang.spring.annotation.ioc.Service;
import com.wang.spring.annotation.mvc.Controller;

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
