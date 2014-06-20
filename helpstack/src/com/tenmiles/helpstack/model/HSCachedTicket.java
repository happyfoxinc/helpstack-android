package com.tenmiles.helpstack.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

public class HSCachedTicket implements Serializable {

	@SerializedName("data")
	HSTicket[] tickets;
	
	public HSCachedTicket() {
		tickets = new HSTicket[0];
	}
	
	public HSTicket[] getTickets() {
		return tickets;
	}
	
	public void setTickets(HSTicket[] tickets) {
		this.tickets = tickets;
	}

	public void addTicketAtStart(HSTicket ticket) {
		// append the data . and save in cache 
		ArrayList<HSTicket> ticketsList = new ArrayList<HSTicket>();
		ticketsList.add(ticket);
		ticketsList.addAll(Arrays.asList(tickets));
		
		HSTicket[] ticketsArray = new HSTicket[0];
		ticketsArray = ticketsList.toArray(ticketsArray);
		
		setTickets(ticketsArray);
	}
	
}
