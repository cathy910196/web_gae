<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<title>Veiw Announcement</title>
</head>
<body>
	<div class="main_panel">
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
		<div>
			<a href="main.jsp" target="_self">返回主頁面</a>
		</div>
		<p />
		<div class="post_header">公告</div>
		<div class="post_title">
			標題：<%=a.getTitle()%></div>
		<div class="post_date">
			時間：<%=sdf.format(a.getDate())%></div>
		<div class="post_contents">
			<pre><%=a.getContents()%></pre>
		</div>
		<div class="post_attachments">
			<%
				if (a.getAttachments().size() > 0) {
			%>
			附件：
			<%
				for (Attachment each : a.getAttachments()) {
			%>
			<div>
				<a href="/attachment?attach-key=<%=KeyFactory.keyToString(each.getKey())%>"><%=each.getFilename()%></a>
			</div>
			<%
				}
			%>
			<%
				}
			%>
		</div>
	</div>
</body>
</html>