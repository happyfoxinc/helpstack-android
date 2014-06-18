package com.tenmiles.helpstack.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Nalin Chhajer
 *
 */
public class HSUser implements Serializable {

	@SerializedName("first_name")
	private String first_Name;
	
	@SerializedName("last_name")
	private String last_Name;
	
	@SerializedName("email")
	private String emailAddress;
	
	@SerializedName("access_token")
	private String token;
	
	@SerializedName("user_id")
	private String userId;
	
	public HSUser() {
	}
	
	public static HSUser createNewUserWithDetails(String first_name, String last_name, String email) {
		HSUser user = new HSUser();
		user.first_Name = first_name;
		user.last_Name = last_name;
		user.emailAddress = email;
		return user;
	}
}
