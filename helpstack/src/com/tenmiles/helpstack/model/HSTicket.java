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

	public static HSTicket createATicket(String id, String subject) {
		HSTicket ticket = new HSTicket();
		ticket.ticketId = id;
		ticket.subject = subject;
		return ticket;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getTicketId() {
		return ticketId;
	}
	
}
