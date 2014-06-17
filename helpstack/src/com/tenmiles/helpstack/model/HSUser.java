package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Nalin Chhajer
 *
 */
public class HSUser {

	@SerializedName("full_name")
	private String name;
	
	@SerializedName("email")
	private String emailAddress;
	
	@SerializedName("access_token")
	private String token;
	
	@SerializedName("user_id")
	private String userId;
	
	public HSUser(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
	}
}
