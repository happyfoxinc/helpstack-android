package com.tenmiles.helpstack.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.tenmiles.helpstack.activities.HSActivityParent;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSUser;

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
	

	public static NewIssueFragment getNewIssueFragment(HSUser user) {
		return NewIssueFragment.createNewIssueFragment(user);
	}

	public static IssueDetailFragment getIssueDetailFragment() {
		return new IssueDetailFragment();
	}
	
	public static SearchFragment getSearchFragment() {
		SearchFragment fragment = new SearchFragment();
		return fragment;
	}
	
	public static HSFragmentParent getFragmentInActivity(HSActivityParent activity,String tag) {
		FragmentManager fragMgr = activity.getSupportFragmentManager();
		return (HSFragmentParent) fragMgr.findFragmentByTag(tag);
	}
	
	public static SectionFragment getSectionFragment(HSActivityParent activity, HSKBItem kbItem)
	{
		SectionFragment sectionFragment = new SectionFragment();
		sectionFragment.sectionItemToDisplay = kbItem;
		return sectionFragment;
	}
	
	public static ArticleFragment getArticleFragment(HSActivityParent activity, HSKBItem kbItem)
	{
		ArticleFragment sectionFragment = new ArticleFragment();
		sectionFragment.kbItem = kbItem;
		return sectionFragment;
	}
	
	public static ImageAttachmentDisplayFragment getImageAttachmentDisplayFragment(HSActivityParent activity, String url)
	{
		ImageAttachmentDisplayFragment fragment = new ImageAttachmentDisplayFragment();
		fragment.image_url = url;
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
