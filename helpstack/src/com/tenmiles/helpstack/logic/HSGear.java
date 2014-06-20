/**
 * 
 */
package com.tenmiles.helpstack.logic;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
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
	public void fetchKBArticle(HSKBItem section, RequestQueue queue,  OnFetchedArraySuccessListener success, ErrorListener errorListener ) 
	{
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}
	
	/**
	 * 
	 * 
	 * @param userDetails
	 * @param success, return HFTicket object
	 * @param error
	 */
	public void fetchAllTicket(HSUser userDetails,RequestQueue queue, OnFetchedArraySuccessListener success, ErrorListener errorListener)
	{
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
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
	public void registerNewUser(String firstName, String lastname, String emailAddress, RequestQueue queue, OnFetchedSuccessListener success, ErrorListener errorListener)
	{
		success.onSuccess(HSUser.createNewUserWithDetails(firstName, lastname, emailAddress));
	}
	
	/**
	 * 
	 * 
	 * @param user
	 * @param message
	 * @param body
	 * @param successListener
	 * @param errorListener
	 */
	public void createNewTicket(HSUser user, String message, String body, RequestQueue queue, OnNewTicketFetchedSuccessListener successListener, ErrorListener errorListener ) {
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}
	
	/**
	 * 
	 * 
	 * @param userDetails
	 * @param success, return HFTicket object
	 * @param error
	 */
	public void fetchAllUpdateOnTicket(HSTicket ticket, HSUser user, RequestQueue queue, OnFetchedArraySuccessListener success, ErrorListener errorListener)
	{
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}
	
	/***
	 * 
	 * @param message
	 * @param ticket
	 * @param user
	 * @param queue
	 * @param success
	 * @param errorListener
	 */
	public void addReplyOnATicket(String message, HSTicket ticket, HSUser user, RequestQueue queue, OnFetchedSuccessListener success, ErrorListener errorListener) {
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
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

