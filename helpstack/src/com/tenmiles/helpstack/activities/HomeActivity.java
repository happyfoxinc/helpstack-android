package com.tenmiles.helpstack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.HomeFragment;
import com.tenmiles.helpstack.fragments.SearchFragment;

/**
 * 
 * Initial Activity that starts HomeActivity
 * 
 * @author Nalin Chhajer
 *
 */
public class HomeActivity extends HSActivityParent {

	private SearchFragment mSearchFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) { // Activity started first time
        	HomeFragment homeFrag = HSFragmentManager.getHomeFragment();
        	HSFragmentManager.putFragmentInActivity(this, R.id.container, homeFrag, "Home");
        }
        
        mSearchFragment = new SearchFragment();
        HSFragmentManager.putFragmentInActivity(this, R.id.search_container, mSearchFragment, "Search");
    }
    
    @Override
    public void configureActionBar(ActionBar actionBar) {
    	super.configureActionBar(actionBar);
    	
    	actionBar.setTitle("Help");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        
        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS|MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		SearchView searchView = new SearchView(this);
		MenuItemCompat.setActionView(searchItem, searchView);
		searchView.setQueryHint("Search");
		searchView.setSubmitButtonEnabled(true);

		searchView.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSearchFragment.searchStarted();
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String q) {
				
				mSearchFragment.doSearchForQuery(q);
				
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				mSearchFragment.doSearchForQuery(newText);
				return true;
			}
		});


		MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				mSearchFragment.setVisibility(true);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				mSearchFragment.setVisibility(false);
				return true;
			}
		});
		
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    	super.onActivityResult(arg0, arg1, arg2);
    }

}
