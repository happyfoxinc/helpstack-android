package com.tenmiles.helpstack.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tenmiles.helpstack.fragments.HSFragmentParent;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;

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
	
	public static void startNewIssueActivity(HSFragmentParent context, HSUser user, int requestCode) {
		Intent intent = new Intent(context.getActivity(), NewIssueActivity.class);
		intent.putExtra(NewIssueActivity.EXTRAS_USER, user);
		context.startActivityForResult(intent, requestCode);
	}

	public static void startSectionActivity(HSFragmentParent context, HSKBItem kbItem, int requestCode) {
		Intent intent = new Intent(context.getActivity(), SectionActivity.class);
		intent.putExtra(SectionActivity.EXTRAS_SECTION_ITEM, kbItem);
		context.startActivityForResult(intent, requestCode);
	}
	
	public static void startArticleActivity(HSFragmentParent context, HSKBItem kbItem, int requestCode) {
		Intent intent = new Intent(context.getActivity(), ArticleActivity.class);
		intent.putExtra(ArticleActivity.EXTRAS_ARTICLE_ITEM, kbItem);
		context.startActivityForResult(intent, requestCode);
	}
	
	public static void startNewUserActivity(HSFragmentParent context, int requestCode) {
		Intent intent = new Intent(context.getActivity(), NewUserActivity.class);
		context.startActivityForResult(intent, requestCode);
	}
	
	public static void startIssueDetailActivity(Activity context, HSTicket ticket) {
		Intent intent = new Intent(context, IssueDetailActivity.class);
		intent.putExtra(IssueDetailActivity.EXTRAS_TICKET, ticket);
		context.startActivity(intent);
	}
	
	public static void startImageAttachmentDisplayActivity(Activity context, String url, String title) {
		Intent intent = new Intent(context, ImageAttachmentDisplayActivity.class);
		intent.putExtra(ImageAttachmentDisplayActivity.EXTRAS_STRING_URL, url);
		intent.putExtra(ImageAttachmentDisplayActivity.EXTRAS_TITLE, title);
		context.startActivity(intent);
	}
	
	public static void finishSafe(Activity context) {
		Intent intent = new Intent();
		context.setResult(HSActivityManager.resultCode_cancelled,intent);
		context.finish();
	}
	
	public static void sendSuccessSignal(Activity context, Intent result) {
		context.setResult(HSActivityManager.resultCode_sucess,result);
		context.finish();
	}
	
}
