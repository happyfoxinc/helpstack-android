package com.tenmiles.helpstack.activities;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * Contains a function call to start any activity used in HelpStack
 * 
 * @author Nalin Chhajer
 *
 */
public class HSActivityManager {

	public static void startHomeActivity(Context context) {
		Intent intent = new Intent(context, HomeActivity.class);
		context.startActivity(intent);
	}
	
	public static void startNewIssueActivity(Context context) {
		Intent intent = new Intent(context, NewIssueActivity.class);
		context.startActivity(intent);
	}
	
	public static void startNewUserActivity(Context context) {
		Intent intent = new Intent(context, NewUserActivity.class);
		context.startActivity(intent);
	}
	
	
}
