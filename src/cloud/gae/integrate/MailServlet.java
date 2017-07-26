package cloud.gae.integrate;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

@SuppressWarnings("serial")
public class MailServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String author = req.getParameter("message-author");
		String content = req.getParameter("message-content");
		
		MailService mailService = MailServiceFactory.getMailService();
		MailService.Message msg = new MailService.Message(
				"cloud.app.tester@gmail.com",
				"cloud.app.tester@gmail.com",
				author + " has posted a new message.",
				content);
		try {
			mailService.send(msg);
		} catch (IOException e) {
			// ...
		}
	}
}
