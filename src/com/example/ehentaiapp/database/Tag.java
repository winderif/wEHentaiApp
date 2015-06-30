package com.example.ehentaiapp.database;

import java.io.Serializable;
import java.util.Date;

public class Tag implements Serializable {
	private long id;
	
	private String tag;
	private Date lastRead;
	private Date latestPost;
	private int latestCount;
	private boolean subscribed;
	private boolean hasNew;
	
	public Tag() {
	}

	public Tag(long id, String tag, Date lastRead, Date latestPost,
			int latestCount, boolean subscribed, boolean hasNew) {
		this.setId(id);
		this.setTag(tag);
		this.setLastRead(lastRead);
		this.setLatestPost(latestPost);
		this.setLatestCount(latestCount);
		this.setSubscribed(subscribed);
		this.setHasNew(hasNew);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Date getLastRead() {
		return lastRead;
	}

	public void setLastRead(Date lastRead) {
		this.lastRead = lastRead;
	}

	public Date getLatestPost() {
		return latestPost;
	}

	public void setLatestPost(Date latestPost) {
		this.latestPost = latestPost;
	}

	public int getLatestCount() {
		return latestCount;
	}

	public void setLatestCount(int latestCount) {
		this.latestCount = latestCount;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public boolean isHasNew() {
		return hasNew;
	}

	public void setHasNew(boolean hasNew) {
		this.hasNew = hasNew;
	}

}
