package com.tenmiles.helpstack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.HomeFragment;

/**
 * 
 * Initial Activity that starts HomeActivity
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
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, homeFrag)
                    .commit();
        }
    }
    
    @Override
    public void configureActionBar(ActionBar actionBar) {
    	super.configureActionBar(actionBar);
    	
    	actionBar.setTitle("Help and support");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

}
