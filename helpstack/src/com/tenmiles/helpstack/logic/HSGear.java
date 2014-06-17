/**
 * 
 */
package com.tenmiles.helpstack.logic;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSUser;


/**
 * @author Nalin Chhajer
 *
 */
public abstract class HSGear {
	
	public HSGear(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 
	 * @param section, it can be null for initial load of articles.
	 * @param success, return HFKBItem object
	 * @param error
	 */
	public abstract void fetchKBArticle(HSKBItem section, OnFetchedArraySuccessListener success, ErrorListener error );
	
	/**
	 * 
	 * 
	 * @param userDetails
	 * @param success, return HFTicket object
	 * @param error
	 */
	public abstract void fetchAllTicket(HSUser userDetails, OnFetchedArraySuccessListener success, ErrorListener error);
	
	
	/**
	 * 
	 * 
	 * 
	 * @param firstName
	 * @param lastname
	 * @param emailAddress
	 * @param success, return HFUser object
	 * @param error
	 */
	public abstract void registerNewUser(String firstName, String lastname, String emailAddress, OnFetchedSuccessListener success, ErrorListener error);
	
	
	
		
	public void setNotImplementingTicketsFetching(String companySupportEmailAddress) {
		implementsTicketFetching = false;
		this.companySupportEmailAddress = companySupportEmailAddress;
	}
	
	
	// If this is true, we don't call kb article functions, will open email app is required.
	private boolean implementsTicketFetching = true;
	
	
	private String companySupportEmailAddress;
	
	protected Context mContext;

}

