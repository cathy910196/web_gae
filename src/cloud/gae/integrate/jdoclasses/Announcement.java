package cloud.gae.integrate.jdoclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Announcement implements Serializable{

	private static final long serialVersionUID = 3L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String author;
	
	@Persistent
	private String title;
	
	@Persistent
	private String contents;
	
	@Persistent(mappedBy = "announcement")
	private List<Attachment> attachments;
	
	@Persistent
	private Date date;
	
	public Announcement(String author, String title, String contents){
		this.author = author;
		this.title = title;
		this.contents = contents;
		this.attachments = new ArrayList<Attachment>();
		this.date = new Date();
	}
	
	public Key getKey(){
		return key;
	}
	
	public String getAuthor(){
		return author;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContents(){
		return contents;
	}

	public List<Attachment> getAttachments(){
		return attachments;
	}
	
	public Date getDate(){
		return date;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}
