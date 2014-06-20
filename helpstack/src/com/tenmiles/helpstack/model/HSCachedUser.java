package com.tenmiles.helpstack.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class HSCachedUser implements Serializable {

	@SerializedName ("data")
	private HSUser user;
	
	public HSCachedUser() {
	}
	
	public HSUser getUser() {
		return user;
	}
	
	public void setUser(HSUser user) {
		this.user = user;
	}
	
}
