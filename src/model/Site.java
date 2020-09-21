package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Entity(name="sites")
public class Site {
	private int siteID;
	private User owner;
	private String sitename;
	
	@Id
	@Column(name="site_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getSiteID() {
		return siteID;
	}
	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}
	@ManyToOne
	@JoinColumn(name="user_id")
	public User getOwner() {
		return owner;
	}
	public void setOwner(User user) {
		owner = user;
	}
	@Column(name="name", nullable = false)
	public String getSitename() {
		return sitename;
	}
	public void setSitename(String sitename) {
		this.sitename = sitename;
	}
}
