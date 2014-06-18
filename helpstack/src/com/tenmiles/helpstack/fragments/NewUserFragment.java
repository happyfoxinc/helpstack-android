package com.tenmiles.helpstack.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tenmiles.helpstack.R;

public class NewUserFragment extends HSFragmentParent {

	
	public NewUserFragment() {
		
	}
	
	EditText firstNameField, lastNameField, emailField;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);
		
		this.firstNameField = (EditText) rootView.findViewById(R.id.firstname);
		this.lastNameField = (EditText) rootView.findViewById(R.id.lastname);
		this.emailField = (EditText) rootView.findViewById(R.id.email);
		
		return rootView;
	}
	
	public String getFirstName() {
		return firstNameField.getText().toString();
	}
	
	public String getLastName() {
		return lastNameField.getText().toString();
	}
	
	public String getEmailAdddress() {
		return emailField.getText().toString();
	}
	
}

