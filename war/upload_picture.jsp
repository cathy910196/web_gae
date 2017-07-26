<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/user.css" type="text/css" rel="stylesheet"></link>
<title>Upload Picture</title>
</head>
<body>
	<div class="uploadpicture">
		<div class="header">上傳照片</div>
		<p />
		<form action="/uploadpicture" method="post" enctype="multipart/form-data">
			<input type="file" name="userPicture" size="30"><p />
			<input type="submit" value="上傳">
		</form>
	</div>
</body>
</html>