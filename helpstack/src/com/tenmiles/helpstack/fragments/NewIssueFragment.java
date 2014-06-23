package com.tenmiles.helpstack.fragments;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.logic.HSAttachment;

public class NewIssueFragment extends HSFragmentParent {

	private final int REQUEST_CODE_PHOTO_PICKER = 100;
	
	public NewIssueFragment() {
		
	}
	
	EditText subjectField, messageField;
	ImageView imageView1;

	HSAttachment selectedAttachment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_issue, container, false);
		
		this.subjectField = (EditText) rootView.findViewById(R.id.subjectField);
		
		this.messageField = (EditText) rootView.findViewById(R.id.messageField);
		
		this.imageView1 = (ImageView) rootView.findViewById(R.id.imageView1);
		this.imageView1.setOnClickListener(attachmentClickListener);
		
		
		resetAttachmentImage();
		
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		switch(requestCode) { 
	    case REQUEST_CODE_PHOTO_PICKER:
	        if(resultCode == Activity.RESULT_OK){  
	            
				
				Uri selectedImage = intent.getData();
				
				//User had pick an image.
		        Cursor cursor = getActivity().getContentResolver().query(selectedImage, new String[] { 
		        		ImageColumns.DATA,
		        		ImageColumns.DISPLAY_NAME, 
		        		ImageColumns.MIME_TYPE }, null, null, null);
		        cursor.moveToFirst();
				
		        String display_name = cursor.getString(cursor.getColumnIndex(ImageColumns.DISPLAY_NAME));
		        String mime_type = cursor.getString(cursor.getColumnIndex(ImageColumns.MIME_TYPE));
		        String fileName = cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
		        
		        cursor.close();
		        
		        selectedAttachment = HSAttachment.createAttachment(display_name, fileName, mime_type);
				
				resetAttachmentImage();
	            
	        }
	    }
	}
	
	private OnClickListener attachmentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if (selectedAttachment == null) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PHOTO_PICKER);
			}
			else {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
				alertBuilder.setTitle("Attachment");
				alertBuilder.setIcon(R.drawable.ic_action_attachment);
				String[] attachmentOptions = {"Change","Remove"};
				alertBuilder.setItems(attachmentOptions, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PHOTO_PICKER);
						}
						
						else if (which == 1) {
							selectedAttachment = null;
							resetAttachmentImage();
						}
					}
				});
				alertBuilder.create().show();
			}
			
		}
	};
	

	
	private void resetAttachmentImage() {
		if (selectedAttachment == null) {
			this.imageView1.setImageResource(R.drawable.ic_action_attachment);
		}
		else {
			
			try {
				Uri uri = Uri.parse(selectedAttachment.getUrl());
				Bitmap selectedBitmap;
				InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
	            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
	            selectedBitmap = yourSelectedImage;
//				selectedBitmap = downscaleAndReadBitmap(uri);
				this.imageView1.setImageBitmap(selectedBitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	public String getSubject() {
		return subjectField.getText().toString();
	}
	
	public String getMessage() {
		return messageField.getText().toString();
	}
	
	private Bitmap downscaleAndReadBitmap(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);

    }
}
