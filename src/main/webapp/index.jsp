<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
	// 如果客户端请求的URL中没有指定具体的html/jsp等,则根据在web.xml中的
	// 配置会调到这个文件执行,生成index.html返回给客户端
	// 客户端执行此javaScript代码,另外发一条http请求请求index.html
	// 此请求到达服务器后会被DispatcherServlet拦截,查找对应的Controller进行处理
	// 此项目中,会由ForumManageController中的listAllBoards()方法进行处理
	// console.log("大家好,今天是2018/11/29,服务器返回index.jsp生成的response到浏览器中并执行了此处的JavaScript");
    window.location.href=" <c:url value="/index.html"/>";
</script>

