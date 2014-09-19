//  HSUtils
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

package com.tenmiles.helpstack.logic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HSUtils {
	
	private static final long TIME_MILLIS = 1000;
	private static final long TIME_MINUTE = 60*TIME_MILLIS;
	private static final long TIME_HOUR = 60*TIME_MINUTE;
	private static final long TIME_DAY = 24*TIME_HOUR;
	
	public static final String DATE_PATTERN_SHORT = "yyyy-MM-dd";
	public static final String DATE_PATTERN_MEDIUM = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN_USER_DISPLAY_SHORT = "dd-MMM, yyyy";
	
	public HSUtils() {
		
	}
	
	
	//TODO: optimize this function
	public  static String convertToHumanReadableTime(Date givenDate, long currentTimeLong) {
		
		Calendar currentTime = Calendar.getInstance();
		currentTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		currentTime.setTimeInMillis(currentTimeLong);
		Calendar givenTime = Calendar.getInstance();
		givenTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		givenTime.setTime(givenDate);
		// Step 1: To see if time difference is less than 24 hours of not
		long timeDiff = currentTime.getTimeInMillis() - givenTime.getTimeInMillis();
		if(timeDiff<=0) {
			// I am not sure why can we here
			return "Now";
		}
		
		String humanString = null;
		// Checking if timeDiff is less than 24 or not
		if((timeDiff/TIME_DAY)>=1) {
			// day
			int day = (int) (timeDiff/TIME_DAY);
			humanString = String.format(Locale.getDefault(),"%dd", day);
		}
		else {
			// checking if greater than hour
			if((timeDiff/TIME_HOUR)>=1) {
				humanString = String.format(Locale.getDefault(),"%dh", (timeDiff/TIME_HOUR));
			}
			else if((timeDiff/TIME_MINUTE)>=1){
				humanString = String.format(Locale.getDefault(),"%dm", (timeDiff/TIME_MINUTE));
			}
			else {
				humanString = String.format(Locale.getDefault(),"%ds", (timeDiff/TIME_MILLIS));
			}
		}
		
		
		return humanString;
	}
	
	public static void showAlertDialog (Context context, String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context).create();  
		dialog.setCancelable(true); 
		dialog.setMessage(message);
		dialog.setTitle(title);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show();  
	}
}
