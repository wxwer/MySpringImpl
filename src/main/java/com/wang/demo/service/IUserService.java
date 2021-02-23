package com.wang.demo.service;
import com.wang.demo.model.UserRequest;
import com.wang.demo.model.User;

public interface IUserService {
	boolean registerUser(UserRequest request);
	
	User loginUser(UserRequest request);
	
	boolean isUserLogin(String username);
	
	boolean logout(String username);
	
	User findUser(Integer id) throws Exception;
	
	User findUser1(Integer id1) throws Exception;
	
	User findUser2(Integer id2);
}
