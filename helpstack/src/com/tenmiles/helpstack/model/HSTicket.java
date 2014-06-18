package com.tenmiles.helpstack.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Nalin Chhajer
 *
 */
public class HSTicket implements Serializable {

	@SerializedName("subject")
	private String subject;
	
	@SerializedName("ticket_id")
	private String ticketId;

}
