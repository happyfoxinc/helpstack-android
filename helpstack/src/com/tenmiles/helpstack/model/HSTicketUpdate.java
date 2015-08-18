//  HSTicketUpdate
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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class HSTicketUpdate implements Serializable {
	
	public static final int TYPE_STAFF = 0;
	public static final int TYPE_USER = 1;

	@SerializedName("update_id")
	private String updateId;
	
	@SerializedName("text")
	private String text;
	
	@SerializedName("update_by")
	private int updateBy; // 0 - Staff, 1 - User
	
	@SerializedName("update_time")
	private Date updateTime;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("attachments")
	private HSAttachment[] attachments;
	
	// Date, Attachments etc will come here
	
	public static HSTicketUpdate createUpdateByStaff(String updateId, String name, String text, Date updateTime, HSAttachment[] attachments) {
		HSTicketUpdate update = new HSTicketUpdate();
		update.updateBy = TYPE_STAFF;
		update.text = text;
		update.updateId = updateId;
		update.name = name;
		update.updateTime = updateTime;
		update.attachments = attachments;
		return update;
	}
	
	public static HSTicketUpdate createUpdateByUser(String updateId, String name, String text, Date updateTime, HSAttachment[] attachments) {
		HSTicketUpdate update = new HSTicketUpdate();
		update.updateBy = TYPE_USER;
		update.text = text;
		update.updateId = updateId;
		update.name = name;
		update.updateTime = updateTime;
		update.attachments = attachments;
		return update;
	}
	
	public boolean isStaffUpdate() {
		return updateBy == TYPE_STAFF;
	}
	
	public boolean isUserUpdate() {
		return updateBy == TYPE_USER;
	}
	
	public String getText() {
		return text;
	}
	
	public Date getUpdatedTime() {
		return updateTime;
	}
	
	public HSAttachment[] getAttachments() {
		return attachments;
	}
	
	public boolean isAttachmentEmpty() {
		if(attachments == null || attachments.length == 0) {
			return true;
		}
		return false;
	}
	
 }
