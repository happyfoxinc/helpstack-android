package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

public class HSKBItem {
	
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

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
