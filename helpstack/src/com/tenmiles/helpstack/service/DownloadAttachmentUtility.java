package com.tenmiles.helpstack.service;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

public class DownloadAttachmentUtility {

	public static long downloadAttachment(Context ctx, String url, String title) {
		DownloadManager dm;
		
		dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
		
		DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
		request.setTitle(title);
		request.setDescription("Attachments");
		if(Build.VERSION.SDK_INT >= 11) {
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
			request.allowScanningByMediaScanner();
		}
		
		long enqueue = dm.enqueue(request);
        Toast.makeText(ctx, 
				"Downloading Attachment "+title + ". You will be notified when download finish.",
				Toast.LENGTH_LONG)
				.show();
        
        return enqueue;
	}
	
}
