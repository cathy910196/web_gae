package cloud.gae.integrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;


import cloud.gae.integrate.jdoclasses.Announcement;
import cloud.gae.integrate.jdoclasses.Attachment;
import cloud.gae.integrate.jdoclasses.PMF;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class PostAnnouncementServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		//查看是否為管理員
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null || !userService.isUserAdmin()){
			resp.sendRedirect("/");
			return;
		}
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();

		String author = "";
		String title = "";
		String contents = "";
		List<Attachment> attachments = new ArrayList<Attachment>();

		ServletFileUpload upload = new ServletFileUpload();
		upload.setHeaderEncoding("utf-8");
		FileItemIterator iter = null;
		FileItemStream imageItem = null;

		try {
			iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				imageItem = iter.next();
				InputStream imgStream = imageItem.openStream();

				if (imageItem.isFormField()) {
					if (imageItem.getFieldName().equals("title"))
						title = Streams.asString(imgStream, "utf-8");
					else if (imageItem.getFieldName().equals("contents"))
						contents = Streams.asString(imgStream, "utf-8");
				} else if (!imageItem.getName().equals("")) {
					Attachment attachment = new Attachment(
							imageItem.getName(),
							new Blob(IOUtils.toByteArray(imgStream)),
							imageItem.getContentType());
					attachments.add(attachment);
				}
			}
		} catch (FileUploadException e) {
			// ...
		}

		title = title.replace("<", "&lt;").replace(">", "&gt;");
		contents = contents.replace("<", "&lt;").replace(">", "&gt;");
		
		if (title.equals("")) {
			pw.println("標題不能為空白");
		} else if (contents.equals("")) {
			pw.println("內容不能為空白");
		} else {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Announcement announcement = new Announcement(author, title,
					contents);
			announcement.setAttachments(attachments);
			pm.makePersistent(announcement);
			pm.close();
			
			// 清除 memcache
			Cache cache = null;
			try {
				cache = CacheManager.getInstance().getCacheFactory()
						.createCache(Collections.emptyMap());
			} catch (CacheException e) {
				// ...
			}
			cache.remove("annou-cache");
			
			resp.sendRedirect("/main.jsp");
		}
	}
}
