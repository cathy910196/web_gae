package cloud.gae.integrate.jdoclasses;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Attachment {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Announcement announcement;
	
	@Persistent
	private String filename;
	
	@Persistent
	private Blob file;
	
	@Persistent
	private String mimeType;
	
	public Attachment(String filename, Blob file, String mimeType){
		this.filename = filename;
		this.file = file;
		this.mimeType = mimeType;
	}
	
	public Key getKey(){
		return key;
	}
	
	public String getFilename(){
		return filename;
	}
	
	public Blob getFile(){
		return file;
	}
	
	public String getMimeType(){
		return mimeType;
	}
}
