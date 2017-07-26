package cloud.gae.integrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class QueryServlet extends HttpServlet {

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
		

		//抓選項+key
		String delete = "";
		//Key key=KeyFactory.stringToKey(resp.getContentType())
		String key= "";

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
					//
					if (imageItem.getFieldName().equals("title"))
						title = Streams.asString(imgStream, "utf-8");
					else if (imageItem.getFieldName().equals("contents"))
						contents = Streams.asString(imgStream, "utf-8");
					//
					else if (imageItem.getFieldName().equals("key"))
						key=Streams.asString(imgStream, "utf-8");
					if (imageItem.getFieldName().equals("delete"))
						delete=Streams.asString(imgStream, "utf-8");
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
		delete= delete.replace("<", "&lt;").replace(">", "&gt;");
		
		if (title.equals("")) {
			pw.println("標題不能為空白");
		} else if (contents.equals("")) {
			pw.println("內容不能為空白");
		}else {
			//這邊應改把上面所得到的key與delete值判斷傳入正確的資料庫位置
			//首先，兩個選項
			//(1)修改留言板，修改好的資料會新增
			//這是原本課本的範例就是如此
			//(2)但這邊功課的第二的選項，修改資料，並且刪除原本的資料
			//這就有點麻煩了
			//首先要確定原先的資料庫key值，再新增資料後，針對那個key值去進行刪除，也教室刪除原先的資料
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			if(delete.equals("yes")){
				//取得鑑值
				Key keys=KeyFactory.stringToKey(key);
				
				Query query = pm.newQuery(Announcement.class);
				query.setFilter("key == keyParam");
				query.declareParameters(Key.class.getName() + " keyParam");
				List<Announcement> announcements = (List<Announcement>) query.execute(key);
				Announcement a = announcements.iterator().next();
				pm.deletePersistent(a);
			
			}
			
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
