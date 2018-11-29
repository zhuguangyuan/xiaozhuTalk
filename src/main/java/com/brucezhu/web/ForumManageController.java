
package com.brucezhu.web;

import com.brucezhu.domain.Board;
import com.brucezhu.domain.User;
import com.brucezhu.service.ForumService;
import com.brucezhu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 论坛管理，这部分功能由论坛管理员操作，
 * 包括：创建论坛版块、指定论坛版块管理员、用户锁定/解锁。
 */
@Controller
public class ForumManageController extends BaseController {
	private ForumService forumService;
	private UserService userService;

	@Autowired
	public void setForumService(ForumService forumService) {
		this.forumService = forumService;
	}
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 列出所有的论坛模块
	 * 1.若浏览器中直接输入baseURL/index.html将会被映射到此函数进行处理
	 * 2.若浏览器中直接输入baseURL,不指定具体html,则根据web.xml中的配置跳转到index.jsp,
	 * 	然后index.jsp在jsp容器中执行后生成index.html返回给浏览器,浏览器执行里边的JavaScript,
	 * 	window.location.href=" <c:url value="/index.html"/>";
	 * 	重新发送一条请求baseURL/index.html的请求,继而走1的步骤
	 * 若浏览器中直接输入baseURL/index.jsp,则index.jsp将会被执行,下序步骤和2相似.
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView listAllBoards() {
		ModelAndView view =new ModelAndView();
		List<Board> boards = forumService.getAllBoards();
		view.addObject("boards", boards);
		view.setViewName("/listAllBoards");
		return view;
	}

	/**
	 *  添加一个论坛板块
	 * @return
	 */
	@RequestMapping(value = "/addBoardPage", method = RequestMethod.GET)
	public String addBoardPage() {
		return "/addBoard";
	}

	/**
	 * 添加一个论坛板块
	 * @param board
	 * @return
	 */
	@RequestMapping(value = "/addBoard", method = RequestMethod.POST)
	public String addBoard(Board board) {
		forumService.addBoard(board);
		return "/addBoardSuccess";
	}

	/**
	 * 删除版块
	 */
	@RequestMapping(value = "/removeBoard", method = RequestMethod.GET)
	public String removeBoard(@RequestParam("boardIds") String boardIds) {
		String[] arrIds = boardIds.split(",");
		for (int i = 0; i < arrIds.length; i++) {
			forumService.removeBoard(new Integer(arrIds[i]));
		}
		String targetUrl = "/index.html";
		return "redirect:"+targetUrl;
	}

	/**
	 * 指定论坛管理员的页面
	 * @return
	 */
	@RequestMapping(value = "/setBoardManagerPage", method = RequestMethod.GET)
	public ModelAndView setBoardManagerPage() {
		ModelAndView view =new ModelAndView();
		List<Board> boards = forumService.getAllBoards();
		List<User> users = userService.getAllUsers();
		view.addObject("boards", boards);
		view.addObject("users", users);
		view.setViewName("/setBoardManager");
		return view;
	}
	
    /**
     * 设置版块管理
     * @return
     */
	@RequestMapping(value = "/setBoardManager", method = RequestMethod.POST)
	public ModelAndView setBoardManager(@RequestParam("userName") String userName
			,@RequestParam("boardId") String boardId) {
		ModelAndView view =new ModelAndView();
		User user = userService.getUserByUserName(userName);
		if (user == null) {
			view.addObject("errorMsg", "用户名(" + userName
					+ ")不存在");
			view.setViewName("/fail");
		} else {
			Board board = forumService.getBoardById(Integer.parseInt(boardId));
			user.getManBoards().add(board);
			userService.update(user);
			view.setViewName("/success");
		}
		return view;
	}

	/**
	 * 用户锁定及解锁管理页面
	 * @return
	 */
	@RequestMapping(value = "/userLockManagePage", method = RequestMethod.GET)
	public ModelAndView userLockManagePage() {
		ModelAndView view =new ModelAndView();
		List<User> users = userService.getAllUsers();
		view.setViewName("/userLockManage");
		view.addObject("users", users);
		return view;
	}

	/**
	 * 用户锁定及解锁设定
	 * @return
	 */
	@RequestMapping(value = "/userLockManage", method = RequestMethod.POST)
	public ModelAndView userLockManage(@RequestParam("userName") String userName
			,@RequestParam("locked") String locked) {
		ModelAndView view =new ModelAndView();
        User user = userService.getUserByUserName(userName);
		if (user == null) {
			view.addObject("errorMsg", "用户名(" + userName
					+ ")不存在");
			view.setViewName("/fail");
		} else {
			user.setLocked(Integer.parseInt(locked));
			userService.update(user);
			view.setViewName("/success");
		}
		return view;
	}
}
