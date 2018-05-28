//  NewIssueActivity
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.fragments.HSFragmentManager;
import com.tenmiles.helpstack.fragments.NewIssueFragment;
import com.tenmiles.helpstack.model.HSUser;

public class NewIssueActivity extends HSActivityParent {

    public static final String RESULT_TICKET = "ticket";
    public static final String EXTRAS_USER = "user";
    public static final String EXTRAS_SUBJECT = "subject";
    public static final String EXTRAS_MESSAGE = "message";
    public static final String EXTRAS_ATTACHMENT = "attachment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hs_activity_new_issue, savedInstanceState, R.string.hs_new_issue_title);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            NewIssueFragment newIssueFragment;
            if (getIntent().hasExtra(EXTRAS_USER)) {
                newIssueFragment = HSFragmentManager.getNewIssueFragment((HSUser) bundle.getSerializable(EXTRAS_USER));
            }
            else {
                newIssueFragment = HSFragmentManager.getNewIssueFragment(null);
            }
            HSFragmentManager.putFragmentInActivity(this, R.id.container, newIssueFragment, "Issue");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishSafe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishSafe() {
        Intent intent = new Intent();
        setResult(HSActivityManager.resultCode_cancelled,intent);
        finish();
    }

}
