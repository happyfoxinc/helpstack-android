package com.tenmiles.helpstack.fragments;

import com.tenmiles.helpstack.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NewIssueFragment extends HSFragmentParent {

	public NewIssueFragment() {
		
	}
	
	EditText subjectField, messageField;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_issue, container, false);
		
		this.subjectField = (EditText) rootView.findViewById(R.id.subjectField);
		
		this.messageField = (EditText) rootView.findViewById(R.id.messageField);
		return rootView;
	}
	
	public String getSubject() {
		return subjectField.getText().toString();
	}
	
	public String getMessage() {
		return messageField.getText().toString();
	}
	
}
