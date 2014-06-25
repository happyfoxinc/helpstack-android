package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.ArticleFragment;
import com.tenmiles.helpstack.model.HSKBItem;

public class ArticleActivity extends HSActivityParent {

	HSKBItem kbItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		if (savedInstanceState == null) {
			
			ArticleFragment articleFragment = new ArticleFragment();
			this.kbItem = (HSKBItem)getIntent().getSerializableExtra("item");
			articleFragment.kbItem = this.kbItem;
			getSupportActionBar().setTitle(this.kbItem.getSubject());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, articleFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.article, menu);
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
