
package com.brucezhu.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.brucezhu.domain.User;
import com.brucezhu.exception.UserExistException;
import com.brucezhu.service.UserService;

/**
 * 用户注册控制器
 */
@Controller                   
public class RegisterController extends BaseController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 用户登录
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView register(HttpServletRequest request,User user){
		ModelAndView view = new ModelAndView();
		view.setViewName("/success");
		try {
			userService.register(user);
		} catch (UserExistException e) {
			view.addObject("errorMsg", "用户名已经存在，请选择其它的名字。");
			view.setViewName("forward:/register.jsp");
		}
		setSessionUser(request,user);
		return view;
	}
}
