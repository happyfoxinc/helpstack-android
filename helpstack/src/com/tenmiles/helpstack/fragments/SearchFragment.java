package com.tenmiles.helpstack.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tenmiles.helpstack.R;

/**
 * Search Fragment
 * 
 */
public class SearchFragment extends HSFragmentParent {

	private View rootView;

	public SearchFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		rootView =  inflater.inflate(R.layout.fragment_search, container, false);
		
		setVisibility(false);
		
		return rootView;
	}

	public void searchStarted() {
		// TODO Auto-generated method stub
		Log.v("search","started");
	}

	public void doSearchForQuery(String q) {
		// TODO Auto-generated method stub
		Log.v("search","do querry");
	}

	public void setVisibility(boolean visible) {
		if (visible) {
			rootView.setVisibility(View.VISIBLE);
		}
		else {
			rootView.setVisibility(View.GONE);
		}
	}

}
