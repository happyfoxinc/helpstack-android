//  AttachmentDownloadReceiver
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

package com.tenmiles.helpstack.service;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.tenmiles.helpstack.R;

public class AttachmentDownloadReceiver extends BroadcastReceiver {

	private static final int NOTIFICATION_ID = 1008;
	private static final int PENDING_INTENT_REQUEST_CODE = 108;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String intendedPackage = intent.getPackage();
		String contextPackage = context.getPackageName();
		
        if (intendedPackage.equals(contextPackage) && DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
        	downloadCompleted(context, intent);
        }
        else if (intendedPackage.equals(contextPackage) && DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
        	notificationClicked(context, intent);
        }
	}
	
	private void notificationClicked(Context context, Intent intent) {
		Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	private void downloadCompleted(Context context, Intent intent) {
    	//Files are  ready
    	String filename = context.getString(R.string.hs_attachment);
    	String filepath = null;
    	String mediaType = null;
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    	long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        StringBuilder text = new StringBuilder();

		Query query = new Query();
		query.setFilterById(downloadId);
		Cursor c = dm.query(query);
		if (c.moveToFirst()) {
			int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
			filepath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
			mediaType = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
			if(status == DownloadManager.STATUS_SUCCESSFUL) {
				text.append(context.getString(R.string.hs_download_complete));
			}
			else {
				text.append(context.getString(R.string.hs_error_during_download));
			}
		}
        
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
    	notificationBuilder.setAutoCancel(true);
    	notificationBuilder.setContentText(text.toString());
    	notificationBuilder.setContentTitle(filename);
    	notificationBuilder.setSmallIcon(R.drawable.hs_notification_download_light_img);
    	notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
    	notificationBuilder.setContentIntent(getPendingIntent(context));
    	
    	notificationManager.notify(filename, NOTIFICATION_ID, notificationBuilder.build());
	}

	public PendingIntent getPendingIntent(Context context, String filename, String mediatype) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse(filename), mediatype);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendinIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, Intent.createChooser(i, context.getString(R.string.hs_open_with)), PendingIntent.FLAG_UPDATE_CURRENT);
    	return pendinIntent;
	}
	
	public PendingIntent getPendingIntent(Context context) {
		Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent pendinIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
    	return pendinIntent;
	}
	
}
