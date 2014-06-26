package com.tenmiles.helpstack.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.fragments.HSFragmentParent;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSCachedTicket;
import com.tenmiles.helpstack.model.HSCachedUser;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUploadAttachment;
import com.tenmiles.helpstack.model.HSUser;

public class HSSource {
	private static final String TAG = HSSource.class.getSimpleName();
	
	private static final String HELPSTACK_DIRECTORY = "helpstack";
	private static final String HELPSTACK_TICKETS_FILE_NAME = "tickets";
	private static final String HELPSTACK_TICKETS_USER_DATA = "user_credential";
	
	private HSGear gear;
	private Context mContext;
	private RequestQueue mRequestQueue;
	
	private HSCachedTicket cachedTickets;
	private HSCachedUser cachedUser;
	
	public HSSource(Context context) {
		this.mContext = context;
		setGear(HSHelpStack.getInstance(context).getGear());
		mRequestQueue = HSHelpStack.getInstance(context).getRequestQueue();
		
		cachedTickets = new HSCachedTicket();
		cachedUser = new HSCachedUser();
		// read the ticket data from cache and maintain here
		doReadTicketsFromCache();
		doReadUserFromCache();
	}

	public void requestKBArticle(HSKBItem section, OnFetchedArraySuccessListener success, ErrorListener errorListener ) {
		
		if (gear.haveImplementedKBFetching()) {
			
			gear.fetchKBArticle(section,mRequestQueue,  new SuccessWrapper(success) {
				@Override
				public void onSuccess(Object[] successObject) {
					
					assert successObject != null  : "It seems requestKBArticle was not implemented in gear" ;

					// Do your work here, may be caching, data validation etc.
					super.onSuccess(successObject);
					
				}
			}, new ErrorWrapper("Fetching KB articles", errorListener));
		}
		else {
			
			try {
				HSArticleReader reader = new HSArticleReader(gear.getLocalArticleResourceId());
				success.onSuccess(reader.readArticlesFromResource(mContext));
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				throwError(errorListener, "Unable to parse local article XML");
			} catch (IOException e) {
				e.printStackTrace();
				throwError(errorListener, "Unable to read local article XML");
			}
		}
		
	}
	
	public void requestAllTickets(OnFetchedArraySuccessListener success, ErrorListener error ) {
		
		if (cachedTickets == null) {
			success.onSuccess(new HSTicket[0]);
		}
		else {
			success.onSuccess(cachedTickets.getTickets());
		}
		
	}
	
	public void checkForUserDetailsValidity(String firstName, String lastName, String email,OnFetchedSuccessListener success, ErrorListener errorListener) {
		gear.registerNewUser(firstName, lastName, email, mRequestQueue, success, new ErrorWrapper("Registering New User", errorListener));
	}
	
	public void createNewTicket(HSUser user, String subject, String message, HSAttachment[] attachment,  OnNewTicketFetchedSuccessListener successListener, ErrorListener errorListener) {
		
		HSUploadAttachment[] upload_attachments = convertAttachmentArrayToUploadAttachment(attachment);
		
		message = message + getDeviceInformation(mContext);
		
		if (gear.canUplaodMessageAsHtmlString()) {
			message = Html.toHtml(new SpannableString(message));
		}
		
		gear.createNewTicket(user, subject, message, upload_attachments, mRequestQueue, new NewTicketSuccessWrapper(successListener) {
			
			@Override
			public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {
				
				// Save ticket and user details in cache
				// Save properties also later.
				doSaveNewTicketPropertiesForGearInCache(ticket);
				doSaveNewUserPropertiesForGearInCache(udpatedUserDetail);
				super.onSuccess(udpatedUserDetail, ticket);
				
			}
		}, new ErrorWrapper("Creating New Ticket", errorListener));
		
	}
	
	public void requestAllUpdatesOnTicket(HSTicket ticket, OnFetchedArraySuccessListener success, ErrorListener errorListener ) {
		gear.fetchAllUpdateOnTicket(ticket,cachedUser.getUser(), mRequestQueue, success, new ErrorWrapper("Fetching updates on Ticket", errorListener));
	}
	
	public void addReplyOnATicket(String message,HSAttachment[] attachments,  HSTicket ticket,  OnFetchedSuccessListener success, ErrorListener errorListener) {
		
		if (gear.canUplaodMessageAsHtmlString()) {
			message = Html.toHtml(new SpannableString(message));
		}
		
		gear.addReplyOnATicket(message, convertAttachmentArrayToUploadAttachment(attachments),  ticket, getUser(), mRequestQueue, success, new ErrorWrapper("Adding reply to a ticket", errorListener));
	}

	public HSGear getGear() {
		return gear;
	}

	private void setGear(HSGear gear) {
		this.gear = gear;
	}
	
	public boolean isNewUser() {
		return cachedUser.getUser() == null;
	}
	
	public HSUser getUser() {
		return cachedUser.getUser();
	}

	public boolean haveImplementedTicketFetching() {
		return gear.haveImplementedTicketFetching();
	}
	
	public String getSupportEmailAddress() {
		return gear.getCompanySupportEmailAddress();
	}
	
	/***
	 * 
	 * Depending on the setting set on gear, it launches new ticket activity.
	 * 
	 * if email : launches email [Done]
	 * else: 
	 * if user logged in : launches user details [Done] 
	 * else: launches new ticket [Done]
	 * 
	 * @param fragment
	 * @param requestCode
	 */
	public void launchCreateNewTicketScreen(HSFragmentParent fragment, int requestCode) {
		
		if (haveImplementedTicketFetching()) {
			if(isNewUser()) {
				HSActivityManager.startNewUserActivity(fragment, requestCode);
			}else {
				HSActivityManager.startNewIssueActivity(fragment, getUser(), requestCode);
			}
		}
		else {
			launchEmailAppWithEmailAddress(fragment.getActivity());
		}
		
	}

	public void launchEmailAppWithEmailAddress(Activity activity) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	    emailIntent.setType("plain/text");
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getSupportEmailAddress()});
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getDeviceInformation(activity));
	    
	    activity.startActivity(Intent.createChooser(emailIntent, "Email"));
	}
	
	private static String getDeviceInformation(Context activity) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n\n\n");
		builder.append("========");
		builder.append("\nDevice Android version : ");
		builder.append(Build.VERSION.SDK_INT);
		builder.append("\nDevice brand : ");
		builder.append(Build.MODEL);
		builder.append("\nApplication package :");
		try {
			builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(),0).packageName);
		} catch (NameNotFoundException e) {
			builder.append("NA");
		}
		builder.append("\nApplication version :");
		try {
			builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(),0).versionCode);
		} catch (NameNotFoundException e) {
			builder.append("NA");
		}
		
		return builder.toString();
	}
	
	
	
	/////////////////////////////////////////////////
	////////     Utility Functions  /////////////////
	/////////////////////////////////////////////////
	
	/**
	 * Opens a file and read its content. Return null if any error occured or file not found
	 * @param file
	 * @return
	 */
	private String readJsonFromFile(File file) {
		
		if (!file.exists()) {
			return null;
		}
		
		String json = null;
		FileInputStream inputStream;
		
		try {
			StringBuilder datax = new StringBuilder();
			inputStream = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader ( inputStream ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close ( ) ;
            
            json = datax.toString();
            return json;
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeJsonIntoFile (File file, String json) {
		FileOutputStream outputStream;

		try {
		  outputStream = new FileOutputStream(file);
		  outputStream.write(json.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	protected void doSaveNewTicketPropertiesForGearInCache(HSTicket ticket) {
		
		cachedTickets.addTicketAtStart(ticket);
		
		Gson gson = new Gson();
		String ticketsgson = gson.toJson(cachedTickets);
		
		
		File ticketFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_FILE_NAME);
		
		writeJsonIntoFile(ticketFile, ticketsgson);
		
	}
	
	protected void doSaveNewUserPropertiesForGearInCache(HSUser user) {
		
		cachedUser.setUser(user);
		
		Gson gson = new Gson();
		String userjson = gson.toJson(cachedUser);
		
		
		File userFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_USER_DATA);
		
		writeJsonIntoFile(userFile, userjson);
		
	}
	
	protected void doReadTicketsFromCache() {
		
		File ticketFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_FILE_NAME);
		
		String json = readJsonFromFile(ticketFile);
		
		if (json != null) {
			Gson gson = new Gson();
			cachedTickets = gson.fromJson(json, HSCachedTicket.class);
		}
	}
	
	
	
	protected void doReadUserFromCache() {
		
		File userFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_USER_DATA);
		
		String json = readJsonFromFile(userFile);
		
		if (json != null) {
			Gson gson = new Gson();
			cachedUser = gson.fromJson(json, HSCachedUser.class);
		}
	}
	
	protected File getProjectDirectory() {
		
		File projDir = new File(mContext.getFilesDir(), HELPSTACK_DIRECTORY);
		if (!projDir.exists())
		    projDir.mkdirs();
		
		return projDir;
	}
	
	private class NewTicketSuccessWrapper implements OnNewTicketFetchedSuccessListener
	{

		private OnNewTicketFetchedSuccessListener lastListner;

		public NewTicketSuccessWrapper(OnNewTicketFetchedSuccessListener lastListner) {
			this.lastListner = lastListner;
		}
		
		@Override
		public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {
			if (lastListner != null)
				lastListner.onSuccess(udpatedUserDetail, ticket);
		}
		
	}
	
	protected HSUploadAttachment[] convertAttachmentArrayToUploadAttachment(HSAttachment[] attachment) {
		
		HSUploadAttachment[] upload_attachments = new HSUploadAttachment[0];
		
		if (attachment != null && attachment.length > 0) {
			int attachmentCount = gear.getNumberOfAttachmentGearCanHandle();
			assert attachmentCount >=  attachment.length : "Gear cannot handle more than "+attachmentCount+" attachmnets";
			upload_attachments = new HSUploadAttachment[attachment.length];
			for (int i = 0; i < upload_attachments.length; i++) {
				upload_attachments[i] = new HSUploadAttachment(mContext, attachment[i]);
			}	
		}
		
		return upload_attachments;
	}
	
	private class SuccessWrapper implements OnFetchedArraySuccessListener
	{

		private OnFetchedArraySuccessListener lastListner;

		public SuccessWrapper(OnFetchedArraySuccessListener lastListner) {
			this.lastListner = lastListner;
		}
		
		@Override
		public void onSuccess(Object[] successObject) {
			if (lastListner != null)
				lastListner.onSuccess(successObject);
		}
		
	}
	
	private class ErrorWrapper implements ErrorListener {

		private ErrorListener errorListener;
		private String methodName;

		public ErrorWrapper(String methodName, ErrorListener errorListener) {
			this.errorListener = errorListener;
			this.methodName = methodName;
		}
		
		@Override
		public void onErrorResponse(VolleyError error) {
			printErrorDescription(methodName, error);
			this.errorListener.onErrorResponse(error);
		}
	}
	
	public static void throwError(ErrorListener errorListener, String error) {
		VolleyError volleyError = new VolleyError(error);
		printErrorDescription(null, volleyError);
		errorListener.onErrorResponse(volleyError);
	}
	
	private static void printErrorDescription (String methodName, VolleyError error)
	{
		if (methodName == null) {
			Log.e(HSHelpStack.LOG_TAG, "Error occurred in HelpStack");
		}
		else {
			Log.e(HSHelpStack.LOG_TAG, "Error occurred when executing " + methodName);
		}
		
		Log.e(HSHelpStack.LOG_TAG, error.toString());
		if (error.getMessage() != null) {
			Log.e(HSHelpStack.LOG_TAG, error.getMessage());
		}
		
		if (error.networkResponse != null && error.networkResponse.data != null) {
			try {
				Log.e(HSHelpStack.LOG_TAG, new String(error.networkResponse.data, "utf-8"));
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		error.printStackTrace();
	}
}
