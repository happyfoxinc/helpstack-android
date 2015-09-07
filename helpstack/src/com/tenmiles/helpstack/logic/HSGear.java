//  HSGear
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

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUploadAttachment;
import com.tenmiles.helpstack.model.HSUser;


/**
 * @author Nalin Chhajer
 *
 */
public abstract class HSGear {
	
	public HSGear() {
	}
	
	/**
     *
     * @param cancelTag Cancel Tag
     * @param section Section
     * @param queue Queue
     * @param success Success listener
     * @param errorListener Error listener
     */
	public void fetchKBArticle(String cancelTag, HSKBItem section, RequestQueue queue,  OnFetchedArraySuccessListener success, ErrorListener errorListener ) {
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}

    /**
     *
     * @param cancelTag Cancel Tag
     * @param firstName First Name
     * @param lastname Last Name
     * @param emailAddress Email Address
     * @param queue Queue
     * @param success Success Listener
     * @param errorListener Error Listener
     */
	public void registerNewUser(String cancelTag, String firstName, String lastname, String emailAddress, RequestQueue queue, OnFetchedSuccessListener success, ErrorListener errorListener) {
		success.onSuccess(HSUser.createNewUserWithDetails(firstName, lastname, emailAddress));
	}
	
	/**
	 *
     * HSUploadAttachment.getAttachment() can contain mime/Type and filename
	 * 
	 * @param user User
	 * @param subject Subject
	 * @param body Body
	 * @param successListener Success Listener
	 * @param errorListener Error Listener
	 */
	public void createNewTicket(String cancelTag, HSUser user, String subject, String body, HSUploadAttachment[] attachments, RequestQueue queue, OnNewTicketFetchedSuccessListener successListener, ErrorListener errorListener) {
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}

    /**
     *
     * @param cancelTag Cancel Tag
     * @param ticket Ticket
     * @param user User
     * @param queue Queue
     * @param success Success Listener
     * @param errorListener Error Listener
     */
	public void fetchAllUpdateOnTicket(String cancelTag, HSTicket ticket, HSUser user, RequestQueue queue, OnFetchedArraySuccessListener success, ErrorListener errorListener) {
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}
	
	/***
	 * 
	 * @param message Message
	 * @param ticket Ticket
	 * @param user User
	 * @param queue Queue
	 * @param success Success Listener
	 * @param errorListener Error Listener
	 */
	public void addReplyOnATicket(String cancelTag, String message, HSUploadAttachment[] attachments, HSTicket ticket, HSUser user, RequestQueue queue, OnFetchedSuccessListener success, ErrorListener errorListener) {
		errorListener.onErrorResponse(new VolleyError("Not implemented method"));
	}

    /**
     * Set this parameter, if gear is not implementing handling of Issues. Doing this, default email client will be open with given support Email Address.
     * Then there is no need to implement issues fetching related methods.
     *
     * Default:   it is considered that gear is gonna implement ticket fetching.
     *
     * @param companySupportEmailAddress Company Support Email Address
     */
	public void setNotImplementingTicketsFetching(String companySupportEmailAddress) {
		implementsTicketFetching = false;
		this.companySupportEmailAddress = companySupportEmailAddress;
	}

    /**
     *
     * @return Company support email address set in {@link #setNotImplementingTicketsFetching(java.lang.String) setNotImplementingTicketsFetching}
     */
    public String getCompanySupportEmailAddress() {
        return companySupportEmailAddress;
    }
    /**
     * Returns if gear have implemented Ticket Fetching. Modify this parameter using {@link #setNotImplementingTicketsFetching(java.lang.String) setNotImplementingTicketsFetching}
     *
     * Default:  true
     *
     */
    public boolean haveImplementedTicketFetching() {
        return implementsTicketFetching;
    }
    /**
     * Set this parameter, if gear is not implementing handling of FAQ. Doing this, FAQ will be fetched from article path.
     * Then there is no need to implement issues fetching related methods.
     *
     * Default:   it is considered that gear is gonna implement email fetching.
     *
     * @param articleResid Article Resource ID
     */
	public void setNotImplementingKBFetching (int articleResid) {
		implementsKBFetching = false;
		this.articleResid = articleResid;
	}

    /**
     *
     * @return Local article id set in {@link #setNotImplementingKBFetching(int) setNotImplementingKBFetching}
     */
    public int getLocalArticleResourceId() {
        return articleResid;
    }

    /**
     * Returns if gear have implemented KB Fetching. Modify this parameter using {@link #setNotImplementingKBFetching(int) setNotImplementingKBFetching}
     *
     * Default:  true
     *
     */
    public boolean haveImplementedKBFetching() {
        return implementsKBFetching;
    }

    /**
     *
     * If this parameter is set, message written by user in chat screen and new issue screen will be returned in HTML format.
     *
     * Default:  false
     *
     * @param htmlEnabled HTML Enabled
     */
	public void uploadMessageAsHtmlString(boolean htmlEnabled) {
		this.supportHtmlMessage = htmlEnabled;
	}

    /**
     * returns if gear can upload message as html string.
     *
     * Default:  false
     *
     */
	public boolean canUploadMessageAsHtmlString() {
		return supportHtmlMessage;
	}

    /**
     *
     * Sets maximum number of attachment gears can handle.
     *
     * Default:  1
     *
     * @param numberOfAttachmentGearCanHandle Number of Attachments that the Gear can handle
     */
	public void setNumberOfAttachmentGearCanHandle (int numberOfAttachmentGearCanHandle) {
		this.numberOfAttachmentGearCanHandle = numberOfAttachmentGearCanHandle;
	}

    /**
     *
     * @return maximum number of attachment gear can handle.
     * Default:  is 1
     */
	public int getNumberOfAttachmentGearCanHandle() {
		return numberOfAttachmentGearCanHandle;
	}


    /**
     * if true, gear don't have to return back HSTicketUpdate object, after a reply is been added.
     *
     * Default: false, means it is necessary for the gear to return HSTicketUpdate object, after a reply is been added.
     *
     * @param canIgnore Can Ignore
     */
    public void ignoreTicketUpdateInformationAfterAddingReply(boolean canIgnore) {
        this.ignoreTicketUpdateInformationAfterAddingReply = canIgnore;
    }

    /**
     *
     * @return if the HSTicketUpdate object, after a reply is added should be used in UI.
     *
     */
    public boolean canIgnoreTicketUpdateInformationAfterAddingReply() {
        return this.ignoreTicketUpdateInformationAfterAddingReply;
    }


    ////////////////////////////////////////////////////
    /////////////   Private Variables   ///////////////
    ///////////////////////////////////////////////////

	private int numberOfAttachmentGearCanHandle = 1;
	
	// If this is true, we don't call kb article functions, will open email app is required.
	private boolean implementsTicketFetching = true;
	
	private boolean implementsKBFetching = true;
	
	private int articleResid;
	
	private String companySupportEmailAddress;
	
	private boolean supportHtmlMessage = false;

    private boolean ignoreTicketUpdateInformationAfterAddingReply = false;

}
