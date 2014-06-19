package com.tenmiles.helpstack.activities;

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
		configureActionBar(getSupportActionBar());
//		setSupportProgressBarIndeterminate(true);
	}

	public void configureActionBar(ActionBar actionBar) {
		
	}
}
