package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewUserFragment extends HSFragmentParent {

	
	public NewUserFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);
		return rootView;
	}
	
}

