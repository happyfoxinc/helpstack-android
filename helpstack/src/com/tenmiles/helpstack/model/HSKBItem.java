package com.tenmiles.helpstack.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class HSKBItem implements Serializable{
	
	public static final int TYPE_ARTICLE = 0;
	public static final int TYPE_SECTION = 1;

	@SerializedName("subject")
	private String subject;
	
	@SerializedName("text")
	private String body;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("article_type")
	private int articleType; // 0 - article, 1 - section
	
	public HSKBItem() {
	}
	
	public HSKBItem(String id, String subject, String body) {
		this.id = id;
		this.subject = subject;
		this.body = body;
		this.articleType = TYPE_ARTICLE;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public int getArticleType() {
		return articleType;
	}
	
	public void setArticleType(int type) {
		articleType = type;
	}
}
