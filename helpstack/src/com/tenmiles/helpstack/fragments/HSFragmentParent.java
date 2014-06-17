package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.activities.HSActivityParent;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

/**
 * 
 * This is the parent class for any Fragment used in HelpStack.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSFragmentParent extends Fragment {

	ActionBar getActionBar() {
		HSActivityParent act = (HSActivityParent) getActivity();
		return act.getSupportActionBar();
	}
	
}
