package com.tenmiles.helpstack.logic;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.Volley;
import com.tenmiles.helpstack.model.HSKBItem;

public class HSSource {
	
	private HSGear gear;
	private Context mContext;
	private RequestQueue mRequestQueue;
	
	public HSSource(Context context, HSGear gear) {
		this.mContext = context;
		setGear(gear);
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
		
		gear.fetchAllTicket(null, new SuccessWrapper(success) {
			@Override
			public void onSuccess(Object[] successObject) {
				
				assert successObject != null  : "It seems requestAllTickets was not implemented in gear" ;

				// Do your work here, may be caching, data validation etc.
				super.onSuccess(successObject);
				
			}
		}, error);
		
	}

	public HSGear getGear() {
		return gear;
	}

	private void setGear(HSGear gear) {
		this.gear = gear;
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

	public boolean isNewUser() {
		return true;
	}
	
}
