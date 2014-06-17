package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewIssueFragment extends HSFragmentParent {

	public NewIssueFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_issue, container, false);
		return rootView;
	}
	
}
