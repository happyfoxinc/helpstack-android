package com.tenmiles.helpstack.logic;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;

public class HSSource {
	
	private HSGear gear;
	private Context mContext;
	private RequestQueue mRequestQueue;
	
	public HSSource(Context context) {
		this.mContext = context;
		setGear(HSHelpStack.getInstance(context).getGear());
		mRequestQueue = Volley.newRequestQueue(context);
	}

	public void requestKBArticle(HSKBItem section, OnFetchedArraySuccessListener success, ErrorListener error ) {
		
		if (gear.haveImplementedKBFetching()) {
			
			gear.fetchKBArticle(section,mRequestQueue,  new SuccessWrapper(success) {
				@Override
				public void onSuccess(Object[] successObject) {
					
					assert successObject != null  : "It seems requestKBArticle was not implemented in gear" ;

					// Do your work here, may be caching, data validation etc.
					super.onSuccess(successObject);
					
				}
			}, error);
		}
		else {
			
			try {
				HSArticleReader reader = new HSArticleReader(gear.getLocalArticleResourceId());
				success.onSuccess(reader.readArticlesFromResource(mContext));
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				error.onErrorResponse(new VolleyError("Unable to parse local article XML"));
			} catch (IOException e) {
				e.printStackTrace();
				error.onErrorResponse(new VolleyError("Unable to read local article XML"));
			}
		}
		
	}
	
	public void requestAllTickets(OnFetchedArraySuccessListener success, ErrorListener error ) {
		
		gear.fetchAllTicket(null, mRequestQueue, new SuccessWrapper(success) {
			@Override
			public void onSuccess(Object[] successObject) {
				
				assert successObject != null  : "It seems requestAllTickets was not implemented in gear" ;

				// Do your work here, may be caching, data validation etc.
				super.onSuccess(successObject);
				
			}
		}, error);
		
	}
	
	public void checkForUserDetailsValidity(String firstName, String lastName, String email,OnFetchedSuccessListener success, ErrorListener errorListener) {
		gear.registerNewUser(firstName, lastName, email, mRequestQueue, success, errorListener);
	}
	
	public void createNewTicket(HSUser user, String subject, String message, OnNewTicketFetchedSuccessListener successListener, ErrorListener errorListener) {
		gear.createNewTicket(user, subject, message, mRequestQueue, successListener, errorListener);
	}
	
	public void requestAllUpdatesOnTicket(HSTicket ticket, OnFetchedArraySuccessListener success, ErrorListener error ) {
		gear.fetchAllUpdateOnTicket(ticket, mRequestQueue, success, error);
	}

	public HSGear getGear() {
		return gear;
	}

	private void setGear(HSGear gear) {
		this.gear = gear;
	}
	
	public boolean isNewUser() {
		return true;
	}
	
	public HSUser getUser() {
		return null;
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

	public boolean haveImplementedTicketFetching() {
		return gear.haveImplementedTicketFetching();
	}
	
	public String getSupportEmailAddress() {
		return gear.getCompanySupportEmailAddress();
	}

	public void launchEmailAppWithEmailAddress(Activity activity) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	    emailIntent.setType("plain/text");
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getSupportEmailAddress()});
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getDeviceInformation(activity));
	    
	    activity.startActivity(Intent.createChooser(emailIntent, "Email"));
	}
	
	public String getDeviceInformation(Activity activity) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n\n\n");
		builder.append("==========================");
		builder.append("\nDevice Android version : ");
		builder.append(Build.VERSION.CODENAME);
		builder.append("\nDevice brand : ");
		builder.append(Build.BRAND);
		builder.append("\nApplication package :");
		try {
			builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(),0));
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
	
}
