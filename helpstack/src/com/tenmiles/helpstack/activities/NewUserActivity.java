package com.tenmiles.helpstack.activities;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.R.layout;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.NewIssueFragment;
import com.tenmiles.helpstack.fragments.NewUserFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

public class NewUserActivity extends HSActivityParent {

	NewUserFragment newUserFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);
		
		if (savedInstanceState == null) {
			newUserFragment = new NewUserFragment();
			HSFragmentManager.putFragmentInActivity(this, R.id.container, newUserFragment, "NewUser");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_issue, menu);
		
		MenuItem nextMenu = menu.findItem(R.id.nextbutton);
		MenuItemCompat.setShowAsAction(nextMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.nextbutton) {
			
			HSActivityManager.startNewIssueActivity(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
