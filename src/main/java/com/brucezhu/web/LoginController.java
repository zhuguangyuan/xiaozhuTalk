package com.brucezhu.web;

import com.brucezhu.cons.CommonConstant;
import com.brucezhu.domain.User;
import com.brucezhu.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 论坛管理，这部分功能由论坛管理员操作，
 * 包括：创建论坛版块、指定论坛版块管理员、用户锁定/解锁。
 */
@Controller
@RequestMapping("/login") // 到/login的请求由此控制器处理
public class LoginController extends BaseController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
     * 用户登陆
     * @param request
     * @param user
     * @return
     */
	@RequestMapping("/doLogin")
	public ModelAndView login(HttpServletRequest request, User user) {
		User dbUser = userService.getUserByUserName(user.getUserName());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("forward:/login.jsp");
		if (dbUser == null) {
			mav.addObject("errorMsg", "用户名不存在");
		} else if (!dbUser.getPassword().equals(user.getPassword())) {
			mav.addObject("errorMsg", "用户密码不正确");
		} else if (dbUser.getLocked() == User.USER_LOCK) {
			mav.addObject("errorMsg", "用户已经被锁定，不能登录。");
		} else {
			dbUser.setLastIp(request.getRemoteAddr());
			dbUser.setLastVisit(new Date());
			userService.loginSuccess(dbUser);
			setSessionUser(request,dbUser);
			String toUrl = (String)request.getSession().getAttribute(CommonConstant.LOGIN_TO_URL);
			request.getSession().removeAttribute(CommonConstant.LOGIN_TO_URL);

			//如果当前会话中没有保存登录之前的请求URL，则直接跳转到主页
			if(StringUtils.isEmpty(toUrl)){
				toUrl = "/index.html";
			}
			// 注意这里的redirect其实就是servlet的redirect(客户端url会改变)
			mav.setViewName("redirect:"+toUrl);
		}

		// 这里返回给DispatcherServlet,由他进行解析得到真正的视图(xxx.jsp),
		// 然后用视图对model里的数据进行渲染,从而得到html/xml/json/图片等等 返回给客户端
		return mav;
	}

	/**
	 * 登录注销
	 * @param session
	 * @return
	 */
	@RequestMapping("/doLogout")
	public String logout(HttpSession session) {
		session.removeAttribute(CommonConstant.USER_CONTEXT);

		// 注意此处返回的是一个String
		// DispatcherServlet会将其解析成一个ModelAndView
		return "forward:/index.jsp";
	}
}
