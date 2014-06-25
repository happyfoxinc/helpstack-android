package com.tenmiles.helpstack.model;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.http.entity.mime.content.InputStreamBody;

import android.content.Context;
import android.net.Uri;

public class HSUploadAttachment {

	private HSAttachment attachment;
	private Context mContext;
	
	public HSUploadAttachment(Context context, HSAttachment attachment) {
		this.mContext = context;
		this.attachment = attachment;
	}
	
	public InputStreamBody generateStreamToUpload() throws FileNotFoundException {
		InputStream stream = mContext.getContentResolver().openInputStream(Uri.parse(attachment.getUrl()));
		
		InputStreamBody body =  new InputStreamBody(stream, attachment.getMime_type(), 
				attachment.getFileName() == null?"attachment":attachment.getFileName());
		
		return body;
	}
	
	public HSAttachment getAttachment() {
		return attachment;
	}
	
}
