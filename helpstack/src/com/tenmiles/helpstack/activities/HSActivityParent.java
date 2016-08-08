//  HSActivityParent
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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.gears.HSHappyfoxGear;

/**
 * This is base class of all Activity used in HelpStack
 *
 * @author Nalin Chhajer
 */
public class HSActivityParent extends AppCompatActivity {

    private static final String ACTION_BAR_TITLE = "Actionbar_title";
    private Toolbar mToolbar;

    // Handling actionbar title when activity changes so activity doesn't have to handle it.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(getSupportActionBar() != null)
        outState.putString(ACTION_BAR_TITLE, getSupportActionBar().getTitle().toString());
    }

    protected void setContentView(int layoutResId, Bundle savedInstanceState, int title) {
        super.setContentView(R.layout.hs_activity_base);
        mToolbar =  (Toolbar)findViewById(R.id.toolbar);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LinearLayout lLayout = (LinearLayout) findViewById(R.id.lyt_base);
        View view = layoutInflater.inflate(layoutResId, null);

        if(getSupportActionBar() != null){
            ActionBar actionbar = getSupportActionBar();
            setTitle(savedInstanceState, title);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        } else{
            mToolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);
            setTitle(savedInstanceState, title);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lLayout.addView(view);
    }

    private void setTitle(Bundle savedInstanceState, int title) {
        ActionBar actionbar = getSupportActionBar();
        if (savedInstanceState != null) {
            actionbar.setTitle(savedInstanceState.getString(ACTION_BAR_TITLE));
        } else if(title != 0){
            actionbar.setTitle(getString(title));
        } else{
            actionbar.setTitle(getString(R.string.hs_help_title));
        }
    }
}
