<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="javax.jdo.Query"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="cloud.gae.integrate.jdoclasses.Announcement"%>
<%@ page import="cloud.gae.integrate.jdoclasses.Attachment"%>
<%@ page import="cloud.gae.integrate.jdoclasses.PMF"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/main.css" type="text/css" rel="stylesheet"></link>
<title>Query Announcement</title>
</head>
<body>
	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null || !userService.isUserAdmin())
			response.sendRedirect("/");
	%>
	
	<%
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		
			// 取得公告
			Key key = KeyFactory.stringToKey(request.getParameter("annou-key"));
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery(Announcement.class);
			query.setFilter("key == keyParam");
			query.declareParameters(Key.class.getName() + " keyParam");
			List<Announcement> announcements = (List<Announcement>) query.execute(key);
			Announcement a = announcements.iterator().next();
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
		<div style="color: #388">修改公告</div>
		<p />
		<form action="/queryservelt" method="post" enctype="multipart/form-data">
			<div style="display: inline">標題：</div>
			<input type="text" name="title" style="width: 400px;" value=<%=a.getTitle()%>></input>
			<p />
			<div style="vertical-align: top; display: inline;">內容：</div>
			<textarea name="contents" style="height: 200px; width: 400px;" ><%=a.getContents()%></textarea>
			<p />
			<div id="more"></div>
			
			
			公告修改後是否刪除原公告<br>
			<input type="radio" checked name="delete" value="yes">是，要刪<br>
			<input type="radio" name="delete" value="no">否，不要刪<br>
			
			<input type="hidden" name="key" value="<%=request.getParameter("annou-key")%>"></input>
			
			<a href="javascript:another_attachment()">增加附檔</a>
			<p />
			<input type="submit" value="送出修改公告"></input>
		</form>
	</div>
</body>
</html>