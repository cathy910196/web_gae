package cloud.gae.integrate;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;


import cloud.gae.integrate.jdoclasses.Member;
import cloud.gae.integrate.jdoclasses.PMF;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UploadPictureServlet extends HttpServlet {

	private List<String> allowedImageTypes = null;

	public void init() {
		allowedImageTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null){
			resp.sendRedirect("/");
			return;
		}
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();

		String mimeType = null;
		byte[] picture = null;

		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter = null;
		FileItemStream imageItem = null;

		try {
			iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				imageItem = iter.next();
				InputStream imgStream = imageItem.openStream();

				if (!imageItem.getName().equals("")) {
					mimeType = imageItem.getContentType();
					picture = IOUtils.toByteArray(imgStream);
				}
			}
		} catch (FileUploadException e) {
			// ...
		}

		if (mimeType != null && !allowedImageTypes.contains(mimeType)) {
			pw.println("圖片格式僅支援 Jpeg, Png, Gif");
		} else {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery(Member.class);
			query.setFilter("email == emailParam");
			query.declareParameters("String emailParam");
			
			List<Member> memberList = (List<Member>) query.execute(user.getEmail());
			Member member = null;
			if (memberList.isEmpty()) {
				member = new Member(user.getEmail());
			} else {
				member = memberList.get(0);
			}
			if(picture != null){
				member.setPicture(new Blob(TransformPicture(picture)), mimeType);
			}
			pm.makePersistent(member);
			pm.close();
			resp.sendRedirect("/");
		}
	}
	
	private byte[] TransformPicture(byte[] pic){
		ImagesService imagesService = ImagesServiceFactory.getImagesService();

        Image oldImage = ImagesServiceFactory.makeImage(pic);
        Transform resize = ImagesServiceFactory.makeResize(150, 150);
        Image newImage = imagesService.applyTransform(resize, oldImage);
        
        return newImage.getImageData();
	}
}
