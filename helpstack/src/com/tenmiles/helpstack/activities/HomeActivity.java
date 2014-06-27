package com.tenmiles.helpstack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.HomeFragment;

/**
 * 
 * Initial Activity of HelpStack. It displays Tickets and FAQ. Handles search also.
 * 
 * @author Nalin Chhajer
 *
 */
public class HomeActivity extends HSActivityParent {
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        if (savedInstanceState == null) { // Activity started first time
        	HomeFragment homeFrag = HSFragmentManager.getHomeFragment();
        	HSFragmentManager.putFragmentInActivity(this, R.id.container, homeFrag, "Home");
        }
       
    }
    
    @Override
    public void configureActionBar(ActionBar actionBar) {
    	super.configureActionBar(actionBar);
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	actionBar.setTitle(getString(R.string.help_title)); 
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
        else if (id == android.R.id.home) {
        	finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    	super.onActivityResult(arg0, arg1, arg2);
    }

}
