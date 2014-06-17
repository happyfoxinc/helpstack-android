package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Nalin Chhajer
 *
 */
public class HSTicket {

	@SerializedName("subject")
	private String subject;
	
	@SerializedName("ticket_id")
	private String ticketId;

}
