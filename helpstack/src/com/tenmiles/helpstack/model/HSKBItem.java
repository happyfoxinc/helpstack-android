package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

public class HSKBItem {

	@SerializedName("subject")
	private String subject;
	
	@SerializedName("text")
	private String body;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("article_type")
	private int articleType;
	
	public HSKBItem() {
	}
	
	public HSKBItem(String id, String subject, String body) {
		this.id = id;
		this.subject = subject;
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
