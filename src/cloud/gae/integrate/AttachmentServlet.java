package cloud.gae.integrate;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cloud.gae.integrate.jdoclasses.Attachment;
import cloud.gae.integrate.jdoclasses.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class AttachmentServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		Key key = KeyFactory.stringToKey(req.getParameter("attach-key"));
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Attachment.class);
		query.setFilter("key == keyParam");
		query.declareParameters(Key.class.getName() + " keyParam");
		List<Attachment> attachments = (List<Attachment>) query.execute(key);
		Attachment a = attachments.iterator().next();
		pm.close();

		resp.setCharacterEncoding("utf-8");
		resp.setContentType(a.getMimeType());
		resp.addHeader("Content-Disposition", "filename=" + a.getFilename());
		resp.getOutputStream().write(a.getFile().getBytes());
	}
}
