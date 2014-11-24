//  HSAttachment
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

public class HSAttachment implements Serializable{
	
	private static final long serialVersionUID = 8417480406914032499L;

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
