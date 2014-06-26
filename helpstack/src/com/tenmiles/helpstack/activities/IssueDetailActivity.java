package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.IssueDetailFragment;
import com.tenmiles.helpstack.model.HSTicket;

public class IssueDetailActivity extends HSActivityParent {
	
	public static final String EXTRAS_TICKET = "ticket";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);

		if (savedInstanceState == null) {
			IssueDetailFragment mIssueDetailFragment = HSFragmentManager.getIssueDetailFragment();
			HSFragmentManager.putFragmentInActivity(this, R.id.container, mIssueDetailFragment, "IssueDetail");
			HSTicket ticket = (HSTicket)getIntent().getExtras().getSerializable(EXTRAS_TICKET);
			mIssueDetailFragment.setTicket(ticket);
			getHelpStackActionBar().setTitle(ticket.getSubject());
		}
		
		
	}

	@Override
	public void configureActionBar(ActionBar actionBar) {
		super.configureActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.issue_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		if(id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
