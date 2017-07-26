package cloud.gae.integrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cloud.gae.integrate.jdoclasses.Member;
import cloud.gae.integrate.jdoclasses.PMF;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class UserPictureServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user != null){
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery(Member.class);
			query.setFilter("email == emailParam");
			query.declareParameters("String emailParam");
			
			List<Member> memberList = (List<Member>) query.execute(user.getEmail());
			if (!memberList.isEmpty()) {
				Member member = memberList.get(0);
				resp.setContentType(member.getPictureMimeType());
				resp.getOutputStream().write(member.getPicture().getBytes());
				return;
			}
		}
		
		resp.setContentType("image/png");
		resp.getOutputStream().write(GetNoPic());
	}
	
	private byte[] GetNoPic() throws FileNotFoundException, IOException {
		File file = new File("images/no_pic.png");
		InputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("檔案開啟不完全 " + file.getName());
		}
		is.close();

		return bytes;
	}
}
