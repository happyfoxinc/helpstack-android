package com.tenmiles.helpstack.logic;

import android.content.Context;
import android.util.Log;

/**
 * 
 * Contains methods and function to set Gear, Themes and call Help.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSHelpStack {
	private static final String TAG = HSHelpStack.class.getSimpleName();

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
	
	private HSHelpStack(Context context) {
		this.mContext = context;
		init(context);
	}

	private void init(Context context) {
		
	}
	
}
