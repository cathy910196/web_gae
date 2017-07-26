package cloud.gae.integrate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.BufferedReader;


@SuppressWarnings("serial")
public class WeatherFetchServlet extends HttpServlet {
	//ch5
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		String city ;
		String condition ;
		String temp_c ;
		String humidity ;
		String icon ;

		PrintWriter out =resp.getWriter();
		URL url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20wind%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22chicago%2C%20il%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");

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
				JSONObject jobj2=jobj.getJSONObject("query");
				//JSONArray jarr=jobj2.getJSONArray("results");
				JSONObject jobj3=jobj2.getJSONObject("results");
				JSONObject jobj4=jobj3.getJSONObject("channel");
				JSONObject jobj5=jobj4.getJSONObject("item");
				JSONArray jarr=jobj5.getJSONArray("forest");
				for(int i=0;i<jarr.length();i++){
					//JSONObject jobj3=new JSONObject(jarr.get(i));
					JSONObject jobj6=jarr.getJSONObject(i);
					//out.println(jobj3.toString());
					out.println(jobj6.getString("text"));
				}
			}
			urlc.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}