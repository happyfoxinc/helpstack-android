package com.tenmiles.helpstack.logic;

import java.util.ArrayList;
import java.util.Calendar;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;
import com.tenmiles.helpstack.model.HSUploadAttachment;
import com.tenmiles.helpstack.model.HSUser;

public class HSTestDataGear extends HSGear 
{
	
	private String newTicketBody;
	private HSUploadAttachment[] newTicketattachment;

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
	public void createNewTicket(HSUser user, String message, String body, HSUploadAttachment[] attachment, 
			RequestQueue queue,
			OnNewTicketFetchedSuccessListener successListener,
			ErrorListener errorListener) {
		
		this.newTicketBody = body;
		this.newTicketattachment = attachment;
		successListener.onSuccess(HSUser.appendCredentialOnUserDetail(user, "3", null), HSTicket.createATicket("4", message));
	}
	
	@Override
	public void addReplyOnATicket(String message, HSUploadAttachment[] attachments, HSTicket ticket, HSUser user,  
			RequestQueue queue, OnFetchedSuccessListener success,
			ErrorListener errorListener) {
		
		success.onSuccess(HSTicketUpdate.createUpdateByUser(null, user.getFullName(), message, Calendar.getInstance().getTime(), null));
	}
	
	@Override
	public void fetchAllUpdateOnTicket(HSTicket ticket,HSUser user,  RequestQueue queue,
			OnFetchedArraySuccessListener success, ErrorListener errorListener) {
		
		if (ticket.getTicketId().equals("1")) {
			HSTicketUpdate[] updateArray = new HSTicketUpdate[2];
			Calendar delayTime = Calendar.getInstance();
			delayTime.add(Calendar.MINUTE, -30);
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1","John", "I have not received my order yet. Order id is 23405", delayTime.getTime(), null);
			updateArray[1] = HSTicketUpdate.createUpdateByStaff("2", "Staff", "We have confirmed, it is on the way and you will receive it in 2 days", Calendar.getInstance().getTime(), null);
			success.onSuccess(updateArray);
		}
		else if (ticket.getTicketId().equals("2")) {
			HSTicketUpdate[] updateArray = new HSTicketUpdate[1];
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1","John", "Where are you located.", Calendar.getInstance().getTime(), null);
			success.onSuccess(updateArray);
		}
		else if (ticket.getTicketId().equals("4")) {
			HSTicketUpdate[] updateArray = new HSTicketUpdate[1];
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1", user.getFullName(), this.newTicketBody, Calendar.getInstance().getTime(), null);
			success.onSuccess(updateArray);
		}
		else {
			errorListener.onErrorResponse(new VolleyError("Not Found"));
		}
		
		
		
	}
}
