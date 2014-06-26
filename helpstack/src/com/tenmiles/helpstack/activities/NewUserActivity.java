package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.NewUserFragment;

public class NewUserActivity extends HSActivityParent {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);
		
		if (savedInstanceState == null) {
			NewUserFragment newUserFragment = new NewUserFragment();
			HSFragmentManager.putFragmentInActivity(this, R.id.container, newUserFragment, "NewUser");
		}
		
	}
	
	@Override
	public void configureActionBar(ActionBar actionBar) {
		super.configureActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.new_user_title));
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			HSActivityManager.finishSafe(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
