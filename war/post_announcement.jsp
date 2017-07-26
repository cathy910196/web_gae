<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/main.css" type="text/css" rel="stylesheet"></link>
<title>Post Announcement</title>
</head>
<body>
	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null || !userService.isUserAdmin())
			response.sendRedirect("/");
	%>
	<script type="text/javascript">
		var attachment_count = 0;
		function another_attachment() {
			var div_more = document.getElementById("more");
			var div = document.createElement("div");
			var input = document.createElement("input");
			var br = document.createElement("br");
			div.setAttribute("style", "display:inline");
			div.innerHTML = "附檔 " + (++attachment_count) + " ：";
			input.setAttribute("type", "file");
			input.setAttribute("name", "attachment");
			div_more.appendChild(div);
			div_more.appendChild(input);
			div_more.appendChild(br);
			
		}
	</script>
	<div class="main_panel">
		<div>
			<a href="main.jsp" target="_self">返回主頁面</a>
		</div>
		<p />
		<div style="color: #388">新增公告</div>
		<p />
		<form action="/postannouncement" method="post" enctype="multipart/form-data">
			<div style="display: inline">標題：</div>
			<input type="text" name="title" style="width: 400px;"></input>
			<p />
			<div style="vertical-align: top; display: inline;">內容：</div>
			<textarea name="contents" style="height: 200px; width: 400px;"></textarea>
			<p />
			<div id="more"></div>
			<a href="javascript:another_attachment()">增加附檔</a>
			<p />
			<input type="submit" value="送出公告"></input>
		</form>
	</div>
</body>
</html>