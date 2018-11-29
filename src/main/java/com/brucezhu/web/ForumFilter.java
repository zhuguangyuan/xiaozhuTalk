package com.brucezhu.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import static com.brucezhu.cons.CommonConstant.*;
import com.brucezhu.domain.User;

/**
 * 过滤需要登录才能访问的URI请求
 * 并将此请求跳转到登录界面
 */
public class ForumFilter implements Filter {
    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ForumFilter.class.getName () ) ;

    private static final String FILTERED_REQUEST = "@@session_context_filtered_request";

	// ① 不需要登录即可访问的URI资源
	private static final String[] INHERENT_ESCAPE_URIS = {
	        "/index.jsp", "/index.html",
			"/register.jsp", "/register.html",
            "/login.jsp", "/login/doLogin.html",
            "/board/listBoardTopics-", "/board/listTopicPosts-" };

	// ② 执行过滤
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(request != null){
			HttpServletRequest httpRequest1 = (HttpServletRequest) request;
			logger.error("\n\n=外部===" + httpRequest1.getRequestURI());
		}

		// ②-1 保证该过滤器在一次请求中只被调用一次
		if (request != null && request.getAttribute(FILTERED_REQUEST) != null) {
			chain.doFilter(request, response);
		} else {
			// ②-2 设置过滤标识，防止一次请求多次过滤
			request.setAttribute(FILTERED_REQUEST, Boolean.TRUE);
			HttpServletRequest httpRequest = (HttpServletRequest) request;
//            logger.error("\n\n====" + httpRequest.getRequestURI());

            User userContext = this.getSessionUser(httpRequest);

			// ②-3 用户未登录, 且当前URI资源需要登录才能访问
			if (userContext == null
					&& !isURILogin(httpRequest.getRequestURI(), httpRequest)) {
				String toUrl = httpRequest.getRequestURL().toString();
				if (!StringUtils.isEmpty(httpRequest.getQueryString())) {
					toUrl += "?" + httpRequest.getQueryString();
				}

				// ②-4将用户的请求URL保存在session中，用于登录成功之后，跳到目标URL
				httpRequest.getSession().setAttribute(LOGIN_TO_URL, toUrl);

				// ②-5转发到登录页面
                // 将model(request/response) 及 View(/login.jsp)传回给ispatcherServlet
                // 然后由他完成解析及渲染 得到login.html返回给客户端
				request.getRequestDispatcher("/login.jsp").forward(request, response);
				return;
			}
			chain.doFilter(request, response);
		}
	}




   /**
    * 当前URI资源是否需要登录才能访问
    * @param requestURI
    * @param request
    * @return
    */
	private boolean isURILogin(String requestURI, HttpServletRequest request) {
		if (request.getContextPath().equalsIgnoreCase(requestURI)
				|| (request.getContextPath() + "/").equalsIgnoreCase(requestURI)){
            return true;
        }
		for (String uri : INHERENT_ESCAPE_URIS) {
			if (requestURI != null && requestURI.indexOf(uri) >= 0) {
				return true;
			}
		}
		return false;
	}

    /**
     * 注意这个方法在baseController里有
     * 但是此过滤器并没有继承baseController
     * 所以需要自己写一个
     * @param request
     * @return
     */
	protected User getSessionUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(USER_CONTEXT);
	}

    public void init(FilterConfig filterConfig) throws ServletException {

    }

	public void destroy() {

    }
}
