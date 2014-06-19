package com.tenmiles.helpstack.logic;

import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;
import com.tenmiles.helpstack.model.HSUser;

public class HSTestDataGear extends HSGear 
{
	
	private String newTicketBody;

	public HSTestDataGear(int articleResId) {
		setNotImplementingKBFetching(articleResId);
	}
	
	@Override
	public void fetchAllTicket(HSUser userDetails, RequestQueue queue,
			OnFetchedArraySuccessListener success, ErrorListener errorListener) {
		
		ArrayList<HSTicket> ticketList = new ArrayList<HSTicket>();
		
		{
			HSTicket ticket = HSTicket.createATicket("1", "Test");
			ticketList.add(ticket);
		}
		
		{
			HSTicket ticket = HSTicket.createATicket("2", "Test 2");
			ticketList.add(ticket);
		}
		
		
		HSTicket[] ticketArray = new HSTicket[0];
		success.onSuccess(ticketList.toArray(ticketArray));
		
	}
	
	@Override
	public void registerNewUser(String firstName, String lastname,
			String emailAddress, RequestQueue queue,
			OnFetchedSuccessListener success, ErrorListener errorListener) {
		
		success.onSuccess(HSUser.createNewUserWithDetails(firstName, lastname, emailAddress));
	}
	
	@Override
	public void createNewTicket(HSUser user, String message, String body,
			RequestQueue queue,
			OnNewTicketFetchedSuccessListener successListener,
			ErrorListener errorListener) {
		
		this.newTicketBody = body;
		successListener.onSuccess(HSUser.appendCredentialOnUserDetail(user, "3", null), HSTicket.createATicket("4", message));
	}
	
	
	@Override
	public void fetchAllUpdateOnTicket(HSTicket ticket, RequestQueue queue,
			OnFetchedArraySuccessListener success, ErrorListener errorListener) {
		
		if (ticket.getTicketId().equals("1")) {
			HSTicketUpdate[] updateArray = new HSTicketUpdate[2];
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1", "I have not received my order yet. Order id is 23405");
			updateArray[1] = HSTicketUpdate.createUpdateByUser("2", "We have confirmed, it is on the way and you will receive it in 2 days");
			success.onSuccess(updateArray);
		}
		else if (ticket.getTicketId().equals("2")) {
			HSTicketUpdate[] updateArray = new HSTicketUpdate[1];
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1", "Where are you located.");
			success.onSuccess(updateArray);
		}
		else if (ticket.getTicketId().equals("4")) {
			HSTicketUpdate[] updateArray = new HSTicketUpdate[1];
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1", this.newTicketBody);
			success.onSuccess(updateArray);
		}
		else {
			errorListener.onErrorResponse(new VolleyError("Not Found"));
		}
		
		
		
	}
}
