package com.tenmiles.helpstack.logic;

import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;


public interface OnNewTicketFetchedSuccessListener 
{	
	public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket);
}
