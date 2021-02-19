package com.wang.demo.service;
import com.wang.demo.model.UserRequest;
import com.wang.demo.model.User;

public interface IUserService {
	boolean registerUser(UserRequest request);
	
	User loginUser(UserRequest request);
	
	boolean isUserLogin(String username);
	
}
