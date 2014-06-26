package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.SectionFragment;
import com.tenmiles.helpstack.model.HSKBItem;

/**
 * 
 * Displays SectionFragment
 * 
 * @author Nalin Chhajer
 *
 */
public class SectionActivity extends HSActivityParent {

	public static final String EXTRAS_SECTION_ITEM = "section_item";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section);

		if (savedInstanceState == null) {
			
			HSKBItem kbSectionItem = (HSKBItem)getIntent().getSerializableExtra(EXTRAS_SECTION_ITEM);
			SectionFragment sectionFragment = HSFragmentManager.getSectionFragment(this, kbSectionItem);
			HSFragmentManager.putFragmentInActivity(this, R.id.container, sectionFragment, "Section");
			getHelpStackActionBar().setTitle(kbSectionItem.getSubject());
			
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
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void configureActionBar(ActionBar actionBar) {
		super.configureActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

}
