/**
 * 
 */
package com.tenmiles.helpstack.logic;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSUser;


/**
 * @author Nalin Chhajer
 *
 */
public abstract class HSGear {
	
	public HSGear() {
	}
	
	// TODO: Move all this to abstract method later
	
	/**
	 * 
	 * If u create a request add to queue and start it
	 * 
	 * @param section, it can be null for initial load of articles.
	 * @param success, return HFKBItem object
	 * @param error
	 */
	public void fetchKBArticle(HSKBItem section, RequestQueue queue,  OnFetchedArraySuccessListener success, ErrorListener error ) 
	{
		success.onSuccess(null);
	}
	
	/**
	 * 
	 * 
	 * @param userDetails
	 * @param success, return HFTicket object
	 * @param error
	 */
	public void fetchAllTicket(HSUser userDetails, OnFetchedArraySuccessListener success, ErrorListener error)
	{
		success.onSuccess(null);
	}
	
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
	public void registerNewUser(String firstName, String lastname, String emailAddress, OnFetchedSuccessListener success, ErrorListener error)
	{
		success.onSuccess(null);
	}
	
	
		
	public void setNotImplementingTicketsFetching(String companySupportEmailAddress) {
		implementsTicketFetching = false;
		this.companySupportEmailAddress = companySupportEmailAddress;
	}
	
	public void setNotImplementingKBFetching(int articleResid) {
		implementsKBFetching = false;
		this.articleResid = articleResid;
	}
	
	public boolean haveImplementedTicketFetching() {
		return implementsTicketFetching;
	}
	
	public boolean haveImplementedKBFetching() {
		return implementsKBFetching;
	}
	
	public int getLocalArticleResourceId() {
		return articleResid;
	}
	
	public String getCompanySupportEmailAddress() {
		return companySupportEmailAddress;
	}
	
	// If this is true, we don't call kb article functions, will open email app is required.
	private boolean implementsTicketFetching = true;
	
	private boolean implementsKBFetching = true;
	
	private int articleResid;
	
	private String companySupportEmailAddress;

}

