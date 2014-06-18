package com.tenmiles.helpstack.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class HSKBItem implements Serializable{
	
	transient public static final int TYPE_ARTICLE = 0;
	transient public static final int TYPE_SECTION = 1;

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
	}
	
	public static HSKBItem createForSection(String id, String subject) {
		HSKBItem item = new HSKBItem();
		item.id = id;
		item.subject = subject;
		item.articleType = TYPE_SECTION;
		
		return item;
	}
	
	public static HSKBItem createForArticle(String id, String subject, String text) {
		HSKBItem item = new HSKBItem();
		item.id = id;
		item.subject = subject;
		item.body = text;
		item.articleType = TYPE_ARTICLE;
		return item;
	}

	public String getSubject() {
		return subject;
	}
	
	public String getId() {
		return id;
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
