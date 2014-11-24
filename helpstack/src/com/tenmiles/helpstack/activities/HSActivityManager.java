//  HSActivityManager
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

package com.tenmiles.helpstack.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.tenmiles.helpstack.fragments.HSFragmentParent;
import com.tenmiles.helpstack.fragments.NewIssueFragment;
import com.tenmiles.helpstack.model.HSAttachment;
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
        if(user != null) {
            intent.putExtra(NewIssueActivity.EXTRAS_USER, user);
        }
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

	public static void startNewUserActivity(HSFragmentParent context, int requestCode, String subject, String message, HSAttachment[] attachmentArray) {
		Intent intent = new Intent(context.getActivity(), NewUserActivity.class);
        intent.putExtra(NewIssueActivity.EXTRAS_SUBJECT, subject);
        intent.putExtra(NewIssueActivity.EXTRAS_MESSAGE, message);
        if (attachmentArray != null) {
        	Gson json = new Gson();
        	intent.putExtra(NewIssueActivity.EXTRAS_ATTACHMENT, json.toJson(attachmentArray));
        }
        
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
