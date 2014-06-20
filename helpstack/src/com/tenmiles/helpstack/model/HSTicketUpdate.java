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
	
	
	// Date, Attachments etc will come here
	
	public static HSTicketUpdate createUpdateByStaff(String updateId, String name, String text, Date update_time) {
		HSTicketUpdate update = new HSTicketUpdate();
		update.updateBy = TYPE_STAFF;
		update.text = text;
		update.updateId = updateId;
		update.name = name;
		update.updateTime = update_time;
		return update;
	}
	
	public static HSTicketUpdate createUpdateByUser(String updateId, String name, String text, Date update_time) {
		HSTicketUpdate update = new HSTicketUpdate();
		update.updateBy = TYPE_USER;
		update.text = text;
		update.updateId = updateId;
		update.name = name;
		update.updateTime = update_time;
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
}
