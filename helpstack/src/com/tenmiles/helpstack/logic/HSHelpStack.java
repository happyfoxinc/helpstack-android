package com.tenmiles.helpstack.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 
 * Contains methods and function to set Gear, Themes and call Help.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSHelpStack {
	private static final String TAG = HSHelpStack.class.getSimpleName();
	public static final String LOG_TAG = HSHelpStack.class.getSimpleName();

	public static HSHelpStack getInstance(Context context) {
		if (singletonInstance == null) {
			synchronized (HSHelpStack.class) { // 1
				if (singletonInstance == null) // 2
				{
					Log.d(TAG, "New Instance");
					singletonInstance = new HSHelpStack(
							context.getApplicationContext()); // 3
				}

			}
		}
		return singletonInstance;
	}
	
	private static HSHelpStack singletonInstance = null;
	private Context mContext;
	
	private HSGear gear;
	private RequestQueue mRequestQueue;
	
	private HSHelpStack(Context context) {
		this.mContext = context;
		init(context);
	}

	private void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}
	
	public void setGear(HSGear gear) {
		this.gear = gear;
	}
	
	public HSGear getGear() {
		return this.gear;
	}
	
	public void showGear(Activity activity) {
		activity.startActivity(new Intent("com.tenmiles.helpstack.ShowHelp"));
	}
	
	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
	
	/**
	 * It is light weight call. Call this after calling setGear.
	 * 
	 * @param articleResId
	 */
	public void ovverideGearArticlesWithLocalArticlePath(int articleResId) {
		assert gear != null : "Some gear has to be set before overridding gear with local article path";
		gear.setNotImplementingKBFetching(articleResId);
	}
}
