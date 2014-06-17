package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

public class HSTicketUpdate {
	
	public static final int TYPE_STAFF = 0;
	public static final int TYPE_USER = 1;

	@SerializedName("update_id")
	private String updateId;
	
	@SerializedName("text")
	private String text;
	
	@SerializedName("update_by")
	private int updateBy; // 0 - Staff, 1 - User
	
	// Date, Attachments etc will come here
}
