package com.tenmiles.helpstack.activities;

import com.tenmiles.helpstack.R;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

/**
 * This is base class of all Activity used in HelpStack
 * 
 * @author Nalin Chhajer
 *
 */
public class HSActivityParent extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// Handling actionbar title when activity changes so activity doesnot have to handle it.
		if (savedInstanceState != null) {
			getHelpStackActionBar().setTitle(savedInstanceState.getString("Actionbar_title"));
		}
		getSupportActionBar().setIcon(R.color.hs_transparent_color);
		configureActionBar(getSupportActionBar());
	}

	public void configureActionBar(ActionBar actionBar) {
	}
	
	// Handling actionbar title when activity changes so activity doesnot have to handle it.
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("Actionbar_title", getHelpStackActionBar().getTitle().toString());
	}
	
	public ActionBar getHelpStackActionBar() {
		return getSupportActionBar();
	}
}
