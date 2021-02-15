package com.wang.demo.Service;

import java.util.List;
import com.wang.demo.model.User;
import com.wang.spring.constants.RequestMethod;
import com.wang.spring.ioc.annotation.Autowired;
import com.wang.spring.ioc.annotation.Qualifier;
import com.wang.spring.mvc.ModelAndView;
import com.wang.spring.mvc.annotation.Controller;
import com.wang.spring.mvc.annotation.PathVariable;
import com.wang.spring.mvc.annotation.RequestBody;
import com.wang.spring.mvc.annotation.RequestMapping;
import com.wang.spring.mvc.annotation.RequestParam;
import com.wang.spring.mvc.annotation.ResponseBody;

@Controller
public class TestController {
	@Autowired
	UserPrintService userPrintService;
	@Autowired
	Service2 userService;
	
	@Autowired
	Service1 service1;
	
	@ResponseBody
	@RequestMapping(value = "/test",method = RequestMethod.GET)
	public User test(@RequestParam(value = "name",defaultValue = "wangxed") String name) {
		User user = new User(name+"  ");
		//userPrintService.printUser();
		service1.service();
		return user;
	}
	@RequestMapping(value = "/users/{id}",method = RequestMethod.GET)
	public ModelAndView getUserList(@RequestParam(value = "name",defaultValue = "root") String name,@PathVariable(value = "id",defaultValue = "23") Integer id) {
        List<User> userList = userService.getAllUser();
        userService.service();
        System.out.println("user_id is "+id);
        return new ModelAndView("test.html").addModel("user", name);
    }
	@RequestMapping(value = "/config")
	public void testConfig() {
        userService.service();
    }
}
