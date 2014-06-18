package com.tenmiles.helpstack.activities;

import com.tenmiles.helpstack.model.HSUser;

import android.app.Activity;
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
	
	public final static int resultCode_sucess = Activity.RESULT_OK;
	public final static int resultCode_cancelled = Activity.RESULT_CANCELED;

	public static void startHomeActivity(Context context) {
		Intent intent = new Intent(context, HomeActivity.class);
		context.startActivity(intent);
	}
	
	public static void startNewIssueActivity(Activity context, HSUser user, int requestCode) {
		Intent intent = new Intent(context, NewIssueActivity.class);
		intent.putExtra(NewIssueActivity.EXTRAS_USER, user);
		context.startActivityForResult(intent, requestCode);
	}
	
	public static void startNewUserActivity(Activity context, int requestCode) {
		Intent intent = new Intent(context, NewUserActivity.class);
		context.startActivityForResult(intent, requestCode);
	}
	
	public static void startIssueDetailActivity(Activity context) {
		Intent intent = new Intent(context, IssueDetailActivity.class);
		context.startActivity(intent);
	}
	
	
}
