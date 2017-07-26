<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/user.css" type="text/css" rel="stylesheet"></link>
<title>User</title>
</head>
<body>
	<div class="user_panel">
		<%
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
		%>
		<!-- 使用到 Google 認證 -->
		<ul class="sign">
			<%
				if (user != null) {
			%>
			<li>
				<a href="<%=userService.createLogoutURL("/") %>" target="_parent">登出</a>
			</li>
			<li>
				<a href="/upload_picture.jsp" target="_parent">上傳照片</a>
			</li>
			<%
				} else {
			%>
			<li>
				<a href="<%=userService.createLoginURL("/") %>" target="_parent">登入</a>
			</li>
			<%
				}
			%>
		</ul>
		<div class="picture">
			<img src="/userpicture"></img>			
		</div>
		<div class="username">
			<%
				if (user != null) {
			%>
			<%=user.getNickname()%>
			<%
				} else {
			%>
			Anonymous
			<%
				}
			%>
		</div>
		<p></p>
		<div class="weather">
		天氣~~~
			<%
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Key key = KeyFactory.createKey("Weather", "URLFetch-Weather");
				Entity weather = null;
				try {
					weather = datastore.get(key);
		
					String city = (String) weather.getProperty("city");
					String condition = (String) weather.getProperty("condition");
					String temp = (String) weather.getProperty("temp");
					String humidity = (String) weather.getProperty("humidity");
					//String icon = (String) weather.getProperty("icon");
					String timestamp = (String) weather.getProperty("timestamp");
			%>
			
			<div class="city">天氣：<%=city%></div>
			
			<div class="condition"></div>
			<div><%=condition%></div>
			<div><%=temp%></div>
			<div><%=humidity%></div>
			<p />
			<div>更新時間：</div>
			<div><%=timestamp%></div>
			<%
				} catch (EntityNotFoundException e) {
				}
			%>
		</div>
	</div>
</body>
</html>