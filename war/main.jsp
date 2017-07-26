<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="net.sf.jsr107cache.Cache"%>
<%@ page import="net.sf.jsr107cache.CacheException"%>
<%@ page import="net.sf.jsr107cache.CacheFactory"%>
<%@ page import="net.sf.jsr107cache.CacheManager"%>
<%@ page import="cloud.gae.integrate.jdoclasses.Member"%>
<%@ page import="cloud.gae.integrate.jdoclasses.Announcement"%>
<%@ page import="cloud.gae.integrate.jdoclasses.PMF"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/main.css" type="text/css" rel="stylesheet"></link>
<title>Main</title>
</head>
<body>
	<div class="main_panel">
		<%
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			if (user == null)
				user = (User) session.getAttribute("auth");

			String searchKeyword = request.getParameter("keyword");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));

			Cache cache = null;
			try {
				cache = CacheManager.getInstance().getCacheFactory()
						.createCache(Collections.emptyMap());
			} catch (CacheException e) {
			}
		%>
		<div>
			<form class="search" action="main.jsp" method="post">
				<div>搜尋公告</div>
				<input type="text" size="30" name="keyword"></input> <input
					type="submit" value="搜尋"></input>
			</form>
			<div class="guestbook">
				<a href="guestbook.jsp">留言板</a>
			</div>
		</div>
		<p />
		<div class="board_header">
			<%
				if (searchKeyword != null) {
			%>
			<a href="main.jsp" target="_self">返回公告欄</a><span> 搜尋關鍵字：<%=searchKeyword%></span>
			<%
				} else {
			%>
			<div>公告欄</div>
			<%
				}
			%>
		</div>
		<%
			List<Map<String, String>> list = null;
			if (cache.containsKey("annou-cache") && searchKeyword == null) {
				list = (List<Map<String, String>>) cache.get("annou-cache");
			} else {
				list = new ArrayList<Map<String, String>>();
				PersistenceManager pm = PMF.get().getPersistenceManager();
				String query = "select from " + Announcement.class.getName()
						+ " order by date desc";
				List<Announcement> announcements = (List<Announcement>) pm
						.newQuery(query).execute();
				for (Announcement a : announcements) {
					if (searchKeyword != null
							&& !a.getTitle().contains(searchKeyword))
						continue;

					Map<String, String> each = new HashMap<String, String>();
					each.put("key", KeyFactory.keyToString(a.getKey()));
					each.put("title", a.getTitle());
					each.put("date", sdf.format(a.getDate()));

					list.add(each);
				}
				if (searchKeyword == null)
					cache.put("annou-cache", list);
			}
		%>
		<div>
			<%
				for (Map<String, String> each : list) {
			%>
			<div class="row">
				<%
					if (user != null && userService.isUserAdmin()) {
				%>
				<div class="col">
					<a href="deleteannouncement?annou-key=<%=each.get("key")%>">刪除</a>
					<a href="query_announ.jsp?annou-key=<%=each.get("key")%>">修改</a>

				</div>
				<%
					}
				%>
				<div class="col"><%=each.get("date")%></div>
				<div class="col">
					<a href="view_announcement.jsp?annou-key=<%=each.get("key")%>"><%=each.get("title")%></a>
				</div>
			</div>
			<%
				}
			%>
		</div>
		<%
			if (user != null && userService.isUserAdmin()) {
		%>
		<p />
		<a href="post_announcement.jsp">新增公告</a>
		
		<%
			}
		%>
	</div>
</body>
</html>