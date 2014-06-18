package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IssueDetailFragment extends HSFragmentParent 
{

	public IssueDetailFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_issue_detail, null);
		
		return rootView;
	}
	
}
