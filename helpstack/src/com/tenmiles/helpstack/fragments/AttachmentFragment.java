package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.model.HSAttachment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class AttachmentFragment extends HSFragmentParent{
	
	WebView webView;
	
	public AttachmentFragment () {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_attachment, container, false);
		webView = (WebView)rootView.findViewById(R.id.webView1);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		HSAttachment attachment = (HSAttachment)getActivity().getIntent().getExtras().getSerializable("attachment");
		webView.loadUrl(attachment.getUrl());
		
		return rootView;
	}
}
