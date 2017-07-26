package cloud.gae.integrate.jdoclasses;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Message {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String author;
	
	@Persistent
	private String contents;
	
	@Persistent
	private Date date;
	
	public Message(String author, String contents){
		this.author = author;
		this.contents = contents;
		this.date = new Date();
	}
	
	public Key getKey(){
		return key;
	}
	
	public String getAuthor(){
		return author;
	}
	
	public String getContents(){
		return contents;
	}
	
	public Date getDate(){
		return date;
	}
}
