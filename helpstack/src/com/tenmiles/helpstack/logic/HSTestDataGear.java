//  HSTestDataGear
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package com.tenmiles.helpstack.logic;

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
	public void registerNewUser(String cancelTag, String firstName, String lastname,
			String emailAddress, RequestQueue queue,
			OnFetchedSuccessListener success, ErrorListener errorListener) {
		
		success.onSuccess(HSUser.createNewUserWithDetails(firstName, lastname, emailAddress));
	}
	
	@Override
	public void createNewTicket(String cancelTag, HSUser user, String message, String body, HSUploadAttachment[] attachment, 
			RequestQueue queue,
			OnNewTicketFetchedSuccessListener successListener,
			ErrorListener errorListener) {
		
		this.newTicketBody = body;
		this.newTicketattachment = attachment;
		successListener.onSuccess(HSUser.appendCredentialOnUserDetail(user, "3", null), HSTicket.createATicket("4", message));
	}
	
	@Override
	public void addReplyOnATicket(String cancelTag, String message, HSUploadAttachment[] uploadattachments, HSTicket ticket, HSUser user,  
			RequestQueue queue, OnFetchedSuccessListener success,
			ErrorListener errorListener) {
		HSAttachment[] attachments = new HSAttachment[uploadattachments.length];
		for (int i = 0; i < attachments.length; i++) {
			attachments[i] = uploadattachments[i].getAttachment();
		}
		
		success.onSuccess(HSTicketUpdate.createUpdateByUser(null, user.getFullName(), message, Calendar.getInstance().getTime(), attachments));
	}
	
	@Override
	public void fetchAllUpdateOnTicket(String cancelTag, HSTicket ticket,HSUser user,  RequestQueue queue,
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
			HSTicketUpdate[] updateArray = new HSTicketUpdate[2];
			Calendar delayTime = Calendar.getInstance();
			updateArray[0] = HSTicketUpdate.createUpdateByUser("1", user.getFullName(), this.newTicketBody, delayTime.getTime(), null);
			updateArray[1] = HSTicketUpdate.createUpdateByStaff("2", "Staff", "We will get back to shortly", Calendar.getInstance().getTime(), null);
			success.onSuccess(updateArray);
		}
		else {
			errorListener.onErrorResponse(new VolleyError("Not Found"));
		}
		
		
		
	}
}
