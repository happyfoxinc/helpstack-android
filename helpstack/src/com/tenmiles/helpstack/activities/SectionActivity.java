//  SectionActivity
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

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
	}

}
