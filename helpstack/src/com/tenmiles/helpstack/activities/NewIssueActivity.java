package com.tenmiles.helpstack.activities;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.NewIssueFragment;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnNewTicketFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;

public class NewIssueActivity extends HSActivityParent {

	public static final String EXTRAS_USER = "user";
	public static final String RESULT_TICKET = "ticket";
	
	NewIssueFragment newIssueFragment;
	HSUser userDetails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_issue);

		if (savedInstanceState == null) {
			NewIssueFragment issueFragment = new NewIssueFragment();
			HSFragmentManager.putFragmentInActivity(this, R.id.container, issueFragment, "Issue");
		}
		
		newIssueFragment = (NewIssueFragment) HSFragmentManager.getFragmentInActivity(this, "Issue");
		
		userDetails = (HSUser) getIntent().getExtras().getSerializable(EXTRAS_USER);
	}
	
	@Override
    public void configureActionBar(ActionBar actionBar) {
    	super.configureActionBar(actionBar);
    	
    	actionBar.setTitle("New User");
    	actionBar.setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.issue_menu, menu);
		
		MenuItem doneMenu = menu.findItem(R.id.doneItem);
		MenuItemCompat.setShowAsAction(doneMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finishSafe();
			return true;
		}
		else if (id == R.id.doneItem) {
			
			setSupportProgressBarIndeterminateVisibility(true);
			
			HSSource source = new HSSource(this);
			
			source.createNewTicket(userDetails, newIssueFragment.getSubject(), newIssueFragment.getMessage() , new OnNewTicketFetchedSuccessListener() {
				
				@Override
				public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {
					
					setSupportProgressBarIndeterminateVisibility(false);
					sendSuccessSignal(ticket);
				}
				
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
				
					Log.e("CreateTicket", error.toString());
					error.printStackTrace();
					if (error.networkResponse != null && error.networkResponse.data != null) {
						Log.e("CreateTicket", "reason");
						
						try {
							Log.e("CreateTicket", new String(error.networkResponse.data, "utf-8"));
							
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					setSupportProgressBarIndeterminateVisibility(false);
				}
			});
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void finishSafe() {
		Intent intent = new Intent();
		setResult(HSActivityManager.resultCode_cancelled,intent);
		finish();
	}
	
	public void sendSuccessSignal(HSTicket ticket) {
		Intent intent = getIntent();
		intent.putExtra(RESULT_TICKET, ticket);
		setResult(HSActivityManager.resultCode_sucess,intent);
		// Send a broadcast also, so others can read it.
		finish();
	}

}
