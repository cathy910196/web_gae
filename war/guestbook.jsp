<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="cloud.gae.integrate.jdoclasses.Message"%>
<%@ page import="cloud.gae.integrate.jdoclasses.PMF"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/main.css" type="text/css" rel="stylesheet"></link>
<title>Guestbook</title>
</head>
<body>
	<div id="m" class="main_panel">
		<%
			TimeZone timeZoneTaiwan = TimeZone.getTimeZone("Asia/Taipei");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(timeZoneTaiwan);
		%>
		<div>
			<a href="main.jsp" target="_self">返回主頁面</a>
		</div>
		<p />
		<div>
			<form action="/postmessage" method="post">
				<input type="text" name="content" size="60" />
				<input type="submit" value="送出留言" />
			</form>
		</div>
		<p />
		<div class="guestbook_header"><a href="guestbook.jsp">留言板</a></div>
		<div>
			<%
				PersistenceManager pm = PMF.get().getPersistenceManager();
				String query = "select from " + Message.class.getName() + " order by date desc";
				List<Message> messages = (List<Message>) pm.newQuery(query).execute();
				for (Message msg : messages) {
			%>
			<div class="row">
				<div class="col"><%=sdf.format(msg.getDate())%></div>
				<div class="col"><%=msg.getAuthor()%> :</div>
				<div class="col"><%=msg.getContents()%></div>
			</div>
			<%
				}
			%>
		</div>
	</div>
</body>
</html>