//  HSKBItem
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

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
