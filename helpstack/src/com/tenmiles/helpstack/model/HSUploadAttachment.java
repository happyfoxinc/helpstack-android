//  HSUploadAttachment
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
        InputStream stream = generateInputStreamToUpload();
		
		InputStreamBody body =  new InputStreamBody(stream, attachment.getMime_type(), 
				attachment.getFileName() == null?"attachment":attachment.getFileName());
		
		return body;
	}

    public InputStream generateInputStreamToUpload() throws FileNotFoundException {
        InputStream stream = mContext.getContentResolver().openInputStream(Uri.parse(attachment.getUrl()));
        return stream;
    }


    public HSAttachment getAttachment() {
		return attachment;
	}
	
}
