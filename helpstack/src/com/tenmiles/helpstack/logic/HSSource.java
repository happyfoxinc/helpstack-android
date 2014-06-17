package com.tenmiles.helpstack.logic;

import com.android.volley.Response.ErrorListener;
import com.tenmiles.helpstack.model.HSKBItem;

public class HSSource {
	
	private HSGear gear;
	
	public HSSource(HSGear gear) {
		setGear(gear);
	}

	public void requestKBArticle(HSKBItem section, OnFetchedArraySuccessListener success, ErrorListener error ) {
		
		gear.fetchKBArticle(section, new SuccessWrapper(success) {
			@Override
			public void onSuccess(Object[] successObject) {
				
				assert successObject != null  : "It seems requestKBArticle was not implemented in gear" ;

				// Do your work here, may be caching, data validation etc.
				super.onSuccess(successObject);
				
			}
		}, error);
		
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
			lastListner.onSuccess(successObject);
		}
		
	}
	
}
