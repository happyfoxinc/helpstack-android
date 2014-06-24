package com.tenmiles.helpstack.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

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
	
	public static HSTicketUpdate createUpdateByStaff(String updateId, String name, String text, Date update_time, HSAttachment[] attachments) {
		HSTicketUpdate update = new HSTicketUpdate();
		update.updateBy = TYPE_STAFF;
		update.text = text;
		update.updateId = updateId;
		update.name = name;
		update.updateTime = update_time;
		update.attachments = attachments;
		return update;
	}
	
	public static HSTicketUpdate createUpdateByUser(String updateId, String name, String text, Date update_time, HSAttachment[] attachments) {
		HSTicketUpdate update = new HSTicketUpdate();
		update.updateBy = TYPE_USER;
		update.text = text;
		update.updateId = updateId;
		update.name = name;
		update.updateTime = update_time;
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
	
	public boolean isAttachmentEmtpy() {
		if(attachments == null) {
			return true;
		} else {
			if(attachments.length == 0) {
				return true;
			}
		}
		return false;	
	}
	
 }
