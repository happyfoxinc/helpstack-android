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
		String package_name = intent.getPackage();
		String cpackage = context.getPackageName();
		
        if (package_name.equals(cpackage) && DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
        	downloadCompleted(context, intent);
        }
        else if (package_name.equals(cpackage) && DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
        	notificationClicked(context,intent);
        }
	}
	
	private void notificationClicked(Context context, Intent intent) {
		Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	private void downloadCompleted(Context context, Intent intent) {
		StringBuilder text = new StringBuilder();
    	//Files are  ready
    	String filename = "Attachment";
    	String filepath = null;
    	String mediaType = null;
    	DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    	long downloadId = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		Query query = new Query();
		query.setFilterById(downloadId);
		Cursor c = dm.query(query);
		if (c.moveToFirst()) {
			int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
			filepath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
			mediaType = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
			if(status == DownloadManager.STATUS_SUCCESSFUL) {
				text.append("Download complete");
				
			}
			else {
				text.append("Error during download");
			}
		}
        
        
        
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(context);
    	notificationbuilder.setAutoCancel(true);
    	notificationbuilder.setContentText(text.toString());
    	notificationbuilder.setContentTitle(filename);
    	notificationbuilder.setSmallIcon(R.drawable.notification_download_complete_icon);
    	notificationbuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
    	notificationbuilder.setContentIntent(getPendingIntent(context));
    	
    	notificationManager.notify(filename,NOTIFICATION_ID, notificationbuilder.build());
	}

	public PendingIntent getPendingIntent(Context context, String filename, String mediatype) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse(filename), mediatype);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendinIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, Intent.createChooser(i, "Open with"), PendingIntent.FLAG_UPDATE_CURRENT);
    	return pendinIntent;
	}
	
	public PendingIntent getPendingIntent(Context context) {
		Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent pendinIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
    	return pendinIntent;
	}
	
}
