//  HSHelpStack
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

package com.tenmiles.helpstack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tenmiles.helpstack.logic.HSGear;

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
	private boolean showCredits;
	
	private HSHelpStack(Context context) {
		this.mContext = context;
		init(context);
	}

	private void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		this.setShowCredits(true);
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
	
	public boolean getShowCredits() {
		return this.showCredits;
	}
	
	public void setShowCredits(boolean showCredits) {
		this.showCredits = showCredits;
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
