//  DownloadAttachmentUtility
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a cop
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

import com.tenmiles.helpstack.R;

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
		request.setDescription(ctx.getString(R.string.hs_attachments));
		if(Build.VERSION.SDK_INT >= 11) {
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
			request.allowScanningByMediaScanner();
		}
		
		long enqueue = dm.enqueue(request);
        Toast.makeText(ctx, 
        		ctx.getString(R.string.hs_attachments) + " " + title + ". " + ctx.getString(R.string.hs_notify_download_complete),
				Toast.LENGTH_LONG)
				.show();
        
        return enqueue;
	}
	
}
