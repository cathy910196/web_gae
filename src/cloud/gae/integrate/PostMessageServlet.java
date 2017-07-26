package cloud.gae.integrate;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cloud.gae.integrate.jdoclasses.Message;
import cloud.gae.integrate.jdoclasses.PMF;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class PostMessageServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// 檢查留言內容是否空白
		String content = req.getParameter("content");
        if(content.equals("")){
        	resp.sendRedirect("/guestbook.jsp");
        	return;
        }
        content = content.replace("<", "&lt;").replace(">", "&gt;");
        
        // 取得使用者資訊
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
        String author = "Anonymous";
        if(user != null){
        	author = user.getNickname();
        }
        
        // 將留言寫入datastore
        Message msg = new Message(author, content);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(msg);
        } finally {
            pm.close();
        }

        // 把留言跟名字送出 (Mail + TaskQueue)
        Queue queue = QueueFactory.getDefaultQueue();	
		queue.add(TaskOptions.Builder.withUrl("/mail").method(Method.POST)
				.param("message-content", content).param("message-author", author));
        
        resp.sendRedirect("/guestbook.jsp");
	}
}
