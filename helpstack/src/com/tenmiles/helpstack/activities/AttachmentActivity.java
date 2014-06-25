package com.tenmiles.helpstack.activities;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.R.id;
import com.tenmiles.helpstack.R.layout;
import com.tenmiles.helpstack.R.menu;
import com.tenmiles.helpstack.fragments.AttachmentFragment;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.LocalAttachmentFragment;
import com.tenmiles.helpstack.fragments.NewUserFragment;
import com.tenmiles.helpstack.model.HSAttachment;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class AttachmentActivity extends HSActivityParent {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attachment);

		boolean isLocalAttachment = getIntent().getExtras().getBoolean("isLocalAttachment");
		
		if (savedInstanceState == null) {
			if(!isLocalAttachment) {
				AttachmentFragment attachmentFragment = new AttachmentFragment();
				HSFragmentManager.putFragmentInActivity(this, R.id.container, attachmentFragment, "attachment");
			} else {
				LocalAttachmentFragment attachmentFragment = new LocalAttachmentFragment();
				HSFragmentManager.putFragmentInActivity(this, R.id.container, attachmentFragment, "attachment");
			}
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
		getMenuInflater().inflate(R.menu.attachment, menu);
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
		return super.onOptionsItemSelected(item);
	}

}
