package com.tenmiles.helpstack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.NewIssueFragment;
import com.tenmiles.helpstack.model.HSUser;

public class NewIssueActivity extends HSActivityParent {

	public static final String EXTRAS_USER = "user";
	public static final String RESULT_TICKET = "ticket";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_issue);

		if (savedInstanceState == null) {
			Bundle bundle = getIntent().getExtras();
			NewIssueFragment newIssueFragment = HSFragmentManager.getNewIssueFragment((HSUser)bundle.getSerializable(EXTRAS_USER));
			HSFragmentManager.putFragmentInActivity(this, R.id.container, newIssueFragment, "Issue");
		}
		
	}
	
	@Override
    public void configureActionBar(ActionBar actionBar) {
    	super.configureActionBar(actionBar);
    	
    	actionBar.setTitle("New Issue");
    	actionBar.setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		
		
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
		return super.onOptionsItemSelected(item);
	}
	
	public void finishSafe() {
		Intent intent = new Intent();
		setResult(HSActivityManager.resultCode_cancelled,intent);
		finish();
	}
	
	

}
