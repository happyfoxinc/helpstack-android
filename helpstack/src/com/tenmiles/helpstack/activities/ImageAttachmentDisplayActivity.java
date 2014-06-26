package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;

public class ImageAttachmentDisplayActivity extends HSActivityParent {

	public static final String EXTRAS_STRING_URL = "html_content";
	public static final String EXTRAS_TITLE = "title";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_attachment_display);

		if (savedInstanceState == null) {
			String url = getIntent().getExtras().getString(EXTRAS_STRING_URL);
			String title = getIntent().getExtras().getString(EXTRAS_TITLE);
			getSupportActionBar().setTitle(title);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, HSFragmentManager.getImageAttachmentDisplayFragment(this, url)).commit();
		}
	}
	
	@Override
	public void configureActionBar(ActionBar actionBar) {
		super.configureActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
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
