//  HSHelpStack
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

package com.tenmiles.helpstack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tenmiles.helpstack.activities.HomeActivity;
import com.tenmiles.helpstack.logic.HSGear;
import com.tenmiles.helpstack.logic.HSSource;

/**
 * 
 * Contains methods and function to set Gear and show Help.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSHelpStack {
	private static final String TAG = HSHelpStack.class.getSimpleName();
	public static final String LOG_TAG = HSHelpStack.class.getSimpleName();

    /**
     *
     * @param context Context
     * @return singleton instance of this class.
     */
	public static HSHelpStack getInstance(Context context) {
		if (singletonInstance == null) {
			synchronized (HSHelpStack.class) { // 1
				if (singletonInstance == null) { // 2
					Log.d(TAG, "New Instance");
					singletonInstance = new HSHelpStack(context.getApplicationContext()); // 3
				}
			}
		}
		return singletonInstance;
	}

    /**
     *
     * Sets which gear to use in HelpStack. It has to be set before calling any show* functions.
     *
     * @param gear Gear
     */
	public void setGear(HSGear gear) {
		this.gear = gear;
	}

    /**
     *
     * @return gear which HelpStack has to use.
     */
	public HSGear getGear() {
		return this.gear;
	}

    /**
     *
     * Starts a Help activity. It shows all FAQ and also let user report new issue if not found in FAQ.
     *
     * @param activity Activity
     */
	public void showHelp(Activity activity) {
		activity.startActivity(new Intent(this.mContext, HomeActivity.class));
	}

    /**
     * Call this, if you want to override gear method of article handling, in this case, you can provide articles locally and let HelpStack choose from it.
     *
     * It is light weight call. Call this after calling setGear.
     *
     * @param articleResId Article Resource ID
     */
    public void overrideGearArticlesWithLocalArticlePath(int articleResId) {
        assert gear != null : "Some gear has to be set before overriding gear with local article path";
        gear.setNotImplementingKBFetching(articleResId);
    }

    /**
     *
     * Shows a credit @ bottom of the page.
     *
     * @param showCredits Show Credits or not
     */
    public void setShowCredits(boolean showCredits) {
        this.showCredits = showCredits;
    }

    /**
     *
     * @return if credit can be shown.
     */
    public boolean getShowCredits() {
        return this.showCredits;
    }

    /**
     *
     * clears all files.
     */
    public void clear(Context context) {
        HSSource source = HSSource.getInstance(context);
        source.deleteAllFiles();
    }

    /**
     *
     *
     *
     * @return RequestQueue object which was created during initialization. It is used by all the activity to store and perform network operation.
     */
	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
	

    ////////////////////////////////////////////////////
    /////////////   Private Variables   ///////////////
    ///////////////////////////////////////////////////

    private static HSHelpStack singletonInstance = null;
    private Context mContext;

    private HSGear gear;
    private RequestQueue mRequestQueue;
    private boolean showCredits;

    private HSHelpStack(Context context) {
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        this.setShowCredits(true);
    }
}

