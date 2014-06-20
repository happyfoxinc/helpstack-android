package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.SectionFragment;
import com.tenmiles.helpstack.model.HSKBItem;

public class SectionActivity extends HSActivityParent {

	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section);
		
		HSKBItem kbSectionItem = (HSKBItem)getIntent().getSerializableExtra("section_item");

		if (savedInstanceState == null) {
			
			SectionFragment sectionFragment = new SectionFragment();
			sectionFragment.kbItem = kbSectionItem;
			this.actionBar.setTitle(kbSectionItem.getSubject());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, sectionFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.section, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void configureActionBar(ActionBar actionBar) {
		// TODO Auto-generated method stub
		super.configureActionBar(actionBar);
		this.actionBar = actionBar;
	}

}
