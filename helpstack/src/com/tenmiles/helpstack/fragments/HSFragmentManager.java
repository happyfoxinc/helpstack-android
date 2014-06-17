package com.tenmiles.helpstack.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.tenmiles.helpstack.activities.HSActivityParent;

/**
 * 
 * Contatins functions that help in creating fragment used in HelpStack.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSFragmentManager {

	public static HomeFragment getHomeFragment() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}
	
	public static SearchFragment getSearchFragment() {
		SearchFragment fragment = new SearchFragment();
		return fragment;
	}
	
	public static void putFragmentInActivity(HSActivityParent activity, int resid, HSFragmentParent frag, String tag) {
		// above is proper.
		FragmentManager fragMgr = activity.getSupportFragmentManager();
		FragmentTransaction xact = fragMgr.beginTransaction();
		xact.replace(resid, frag, tag);
		xact.commit();	
	}
	
	public static void putFragmentBackStackInActivity(HSActivityParent activity, int resid, HSFragmentParent frag, String tag) {
		// above is proper.
		FragmentManager fragMgr = activity.getSupportFragmentManager();
		FragmentTransaction xact = fragMgr.beginTransaction();
		xact.replace(resid, frag);
		xact.addToBackStack(tag);
		xact.commit();	
	}
	
}
