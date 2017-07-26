package cloud.gae.integrate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;



@SuppressWarnings("serial")
public class stest extends HttpServlet {
	//ch5
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
	
		PrintWriter out =resp.getWriter();
		URL url = new URL("http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b");

		try {

			HttpURLConnection urlc=(HttpURLConnection)url.openConnection();
			urlc.setConnectTimeout(60000);
			if(urlc.getResponseCode()==HttpURLConnection.HTTP_OK){
				BufferedReader br=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
				StringBuilder sb=new StringBuilder();
				String l;
				while((l=br.readLine())!=null){
					sb.append(l+"\n");
					
				}
				br.close();
				//out.println(sb.toString());
				JSONObject jobj =new JSONObject(sb.toString());
				JSONObject jobj2=jobj.getJSONObject("result");
				JSONArray jarr=jobj2.getJSONArray("results");

				for(int i=0;i<jarr.length();i++){
					//JSONObject jobj3=new JSONObject(jarr.get(i));
					JSONObject jobj3=jarr.getJSONObject(i);
					//out.println(jobj3.toString());
					out.println("form "+jobj3.getString("Station")+"to "+jobj3.getString("Destination"));
				}
			}
			urlc.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}