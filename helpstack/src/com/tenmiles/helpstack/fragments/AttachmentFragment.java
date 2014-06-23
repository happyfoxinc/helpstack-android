package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AttachmentFragment extends HSFragmentParent{
	
	public AttachmentFragment () {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_attachment, container, false);
		return rootView;
	}
}
