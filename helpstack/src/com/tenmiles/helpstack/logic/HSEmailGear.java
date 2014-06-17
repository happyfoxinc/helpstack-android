package com.tenmiles.helpstack.logic;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSUser;



public class HSEmailGear extends HSGear {

	HSArticleReader reader;
	public HSEmailGear(Context context, String supportEmailAddress, int localArticleResId) 
	{
		super(context);
		reader = new HSArticleReader(localArticleResId);
		setNotImplementingTicketsFetching(supportEmailAddress);
	}
	
	@Override
	public void fetchKBArticle(HSKBItem section, OnFetchedArraySuccessListener success,
			ErrorListener error) {
		try {
			success.onSuccess(reader.readArticlesFromResource(mContext));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			error.onErrorResponse(new VolleyError("Unable to parse local article XML"));
		} catch (IOException e) {
			e.printStackTrace();
			error.onErrorResponse(new VolleyError("Unable to read local article XML"));
		}
	}
	
	@Override
	public void fetchAllTicket(HSUser userDetails,
			OnFetchedArraySuccessListener success, ErrorListener error) {
		success.onSuccess(null);
	}

	@Override
	public void registerNewUser(String firstName, String lastname,
			String emailAddress, OnFetchedSuccessListener success,
			ErrorListener error) {
		success.onSuccess(null);
	}
}
