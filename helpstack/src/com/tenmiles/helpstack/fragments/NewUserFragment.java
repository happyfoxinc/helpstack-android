package com.tenmiles.helpstack.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.logic.OnFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSUser;

public class NewUserFragment extends HSFragmentParent {

	private static final int NEW_TICKET_REQUEST_CODE = 1003;
	
	public NewUserFragment() {
		
	}
	
	EditText firstNameField, lastNameField, emailField;
	
	HSSource gearSource;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		
		setHasOptionsMenu(true);
		
		View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);
		
		this.firstNameField = (EditText) rootView.findViewById(R.id.firstname);
		this.lastNameField = (EditText) rootView.findViewById(R.id.lastname);
		this.emailField = (EditText) rootView.findViewById(R.id.email);
		
		gearSource = new HSSource(getActivity());
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("first_name", firstNameField.getText().toString());
		outState.putString("last_name", lastNameField.getText().toString());
		outState.putString("email", emailField.getText().toString());
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null) {
			
		}
		else {
			this.firstNameField.setText(savedInstanceState.getString("first_name"));
			this.lastNameField.setText(savedInstanceState.getString("last_name"));
			this.emailField.setText(savedInstanceState.getString("email"));
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.new_issue, menu);
		
		MenuItem nextMenu = menu.findItem(R.id.nextbutton);
		MenuItemCompat.setShowAsAction(nextMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.nextbutton) {
			
			if(getFirstName().trim().length() == 0 || getLastName().trim().length() == 0 || getEmailAdddress().trim().length() == 0) {
				HSUtils.showAlertDialog(getActivity(), "Error", "Please enter all the fields to register successfully");
				return false;
			}
			
			if(!android.util.Patterns.EMAIL_ADDRESS.matcher(getEmailAdddress()).matches()) {
				HSUtils.showAlertDialog(getActivity(), "Invalid Email", "Please enter a valid email address");
				return false;
			}
			
			getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(true);
			gearSource.checkForUserDetailsValidity("NEW_USER", getFirstName(), getLastName(), 
					getEmailAdddress(), new OnFetchedSuccessListener() {
				
				@Override
				public void onSuccess(Object successObject) {
					getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(false);
					startNewIssueActivity((HSUser)successObject);
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					
					getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(false);
					
				}
			});
			
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == NEW_TICKET_REQUEST_CODE) {
			if (resultCode == HSActivityManager.resultCode_sucess) {
				HSActivityManager.sendSuccessSignal(getActivity(), data);
			}	
		}
	}
	
	@Override
	public void onDetach() {
		gearSource.cancelOperation("NEW_USER");
		super.onDetach();
	}
	
	public void startNewIssueActivity(HSUser user) {
		HSActivityManager.startNewIssueActivity(this, user, NEW_TICKET_REQUEST_CODE);
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

