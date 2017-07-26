package cloud.gae.integrate;

import java.io.IOException;
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

import cloud.gae.integrate.jdoclasses.Announcement;
import cloud.gae.integrate.jdoclasses.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class DeleteAnnouncementServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null || !userService.isUserAdmin()){
			resp.sendRedirect("/");
			return;
		}

		// 取得該筆公告的鍵值
		Key key = KeyFactory.stringToKey(req.getParameter("annou-key"));
		
		// 取出該筆公告並且刪除
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Announcement.class);
		query.setFilter("key == keyParam");
		query.declareParameters(Key.class.getName() + " keyParam");
		List<Announcement> announcements = (List<Announcement>) query.execute(key);
		Announcement a = announcements.iterator().next();
		pm.deletePersistent(a);
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
