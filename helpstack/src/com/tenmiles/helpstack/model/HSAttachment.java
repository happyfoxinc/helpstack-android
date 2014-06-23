package com.tenmiles.helpstack.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class HSAttachment implements Serializable{
	
	@SerializedName("file_name")
	private String fileName;
	
	@SerializedName("mime_type")
	private String mime_type;
	
	@SerializedName("url")
	private String url;
	
	public HSAttachment() {
	}

	public static HSAttachment createAttachment(String url, String fileName, String mime_type)
	{
		HSAttachment attachment = new HSAttachment();
		
		attachment.fileName = fileName;
		attachment.mime_type = mime_type;
		attachment.url = url;
		
		return attachment;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getMime_type() {
		return mime_type;
	}
	
}
