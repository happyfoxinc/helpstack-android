package com.tenmiles.helpstack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.HomeFragment;

public class HomeActivity extends HSActivityParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hs_activity_home, savedInstanceState, R.string.hs_help_title);

        if (savedInstanceState == null) { // Activity started first time
            HomeFragment homeFragment = HSFragmentManager.getHomeFragment();
            HSFragmentManager.putFragmentInActivity(this, R.id.container, homeFragment, "Home");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        } else if (id == android.R.id.home) {
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
