package com.wang.demo.controller;

import com.wang.demo.model.ResponseEntity;
import com.wang.demo.model.User;
import com.wang.demo.model.UserRequest;
import com.wang.demo.service.IUserService;
import com.wang.spring.annotation.ioc.Autowired;
import com.wang.spring.annotation.mvc.Controller;
import com.wang.spring.annotation.mvc.RequestBody;
import com.wang.spring.annotation.mvc.RequestMapping;
import com.wang.spring.annotation.mvc.RequestParam;
import com.wang.spring.annotation.mvc.ResponseBody;
import com.wang.spring.constants.RequestMethod;
import com.wang.spring.mvc.ModelAndView;

@Controller
@RequestMapping(value = "/user")
public class TestController {
	
	@Autowired
	IUserService userService;
	
	@ResponseBody
	@RequestMapping(value = "/register",method = RequestMethod.POST)
	public ResponseEntity registerUser(@RequestBody UserRequest userRequest) {
		try {
			boolean isRegister = userService.registerUser(userRequest);
			if(isRegister) {
				return ResponseEntity.success(null, userRequest.getUserName()+"注册成功...");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ResponseEntity.fail("注册失败");
	}
	
	@ResponseBody
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	public ResponseEntity login(@RequestBody UserRequest userRequest){
		try {
			User user = userService.loginUser(userRequest);
			if(user!=null) {
				return ResponseEntity.success(user, userRequest.getUserName()+"登录成功");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ResponseEntity.fail("用户名或密码错误");
	}
	
	@ResponseBody
	@RequestMapping(value = "/isLogin",method = RequestMethod.GET)
	public ModelAndView isLogin(@RequestParam(value = "username") String username){
		try {
			boolean isLogin = userService.isUserLogin(username);
			if(isLogin) {
				return new ModelAndView("login_sucess.html").addModel("user", username);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return new ModelAndView("login_failed.html").addModel("user", username);
	}
	
	@ResponseBody
	@RequestMapping(value = "/logout",method = RequestMethod.GET)
	public ResponseEntity loginout(@RequestParam(value = "username") String username){
		try {
			boolean isLogout = userService.logout(username);
			if(isLogout) {
				return ResponseEntity.success(null, username+"注销成功");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ResponseEntity.fail(username+"注销失败");
	}
}
