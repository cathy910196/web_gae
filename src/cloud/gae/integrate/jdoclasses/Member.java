package cloud.gae.integrate.jdoclasses;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Member implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String email;

	@Persistent
	private Blob picture;
	
	@Persistent
	private String mimeType;

	public Member(String email) {
		this.email = email;
	}

	public Key getKey() {
		return key;
	}

	public String getEmail() {
		return email;
	}

	public Blob getPicture() {
		return picture;
	}
	
	public String getPictureMimeType() {
		return mimeType;
	}

	public void setPicture(Blob picture, String mimeType) {
		this.picture = picture;
		this.mimeType = mimeType;
	}
}
