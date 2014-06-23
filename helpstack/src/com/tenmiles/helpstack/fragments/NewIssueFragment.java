package com.tenmiles.helpstack.fragments;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

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
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.activities.NewIssueActivity;
import com.tenmiles.helpstack.logic.HSAttachment;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnNewTicketFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;

public class NewIssueFragment extends HSFragmentParent {

	private final int REQUEST_CODE_PHOTO_PICKER = 100;
	
	public static final String EXTRAS_USER = NewIssueActivity.EXTRAS_USER;
	
	public static final String RESULT_TICKET = NewIssueActivity.RESULT_TICKET;
	
	public static NewIssueFragment createNewIssueFragment(HSUser user)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_USER, user);
		
		NewIssueFragment frag = new NewIssueFragment();
		
		frag.setArguments(args);
		return frag;
	}
	
	
	HSUser userDetails;
	
	
	
	EditText subjectField, messageField;
	ImageView imageView1;

	HSAttachment selectedAttachment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		setHasOptionsMenu(true);
		
		View rootView = inflater.inflate(R.layout.fragment_new_issue, container, false);
		
		this.subjectField = (EditText) rootView.findViewById(R.id.subjectField);
		
		this.messageField = (EditText) rootView.findViewById(R.id.messageField);
		
		this.imageView1 = (ImageView) rootView.findViewById(R.id.imageView1);
		this.imageView1.setOnClickListener(attachmentClickListener);
		
		
		// Read user value
		Bundle args = savedInstanceState;
		if (args == null) {
			args = getArguments();
		}
		
		userDetails = (HSUser) args.getSerializable(EXTRAS_USER);
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRAS_USER, userDetails);
		outState.putString("subject", subjectField.getText().toString());
		outState.putString("message", messageField.getText().toString());
		if (selectedAttachment != null) {
			outState.putSerializable("attachment", selectedAttachment);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState != null) {
			subjectField.setText(savedInstanceState.getString("subject"));
			messageField.setText(savedInstanceState.getString("message"));
			if (savedInstanceState.containsKey("attachment")) {
				selectedAttachment = (HSAttachment) savedInstanceState.getSerializable("attachment");
			}
		}
		
		resetAttachmentImage();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.issue_menu, menu);
		
		MenuItem doneMenu = menu.findItem(R.id.doneItem);
		MenuItemCompat.setShowAsAction(doneMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.doneItem) {
			
			getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(true);
			
			HSSource source = new HSSource(getActivity());
			
			source.createNewTicket(userDetails, getSubject(), getMessage() + HSSource.getDeviceInformation(getActivity())
					, new OnNewTicketFetchedSuccessListener() {
				
				@Override
				public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {
					
					getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(false);
					sendSuccessSignal(ticket);
				}
				
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
				
					Log.e("CreateTicket", error.toString());
					error.printStackTrace();
					if (error.networkResponse != null && error.networkResponse.data != null) {
						Log.e("CreateTicket", "reason");
						
						try {
							Log.e("CreateTicket", new String(error.networkResponse.data, "utf-8"));
							
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(false);
				}
			});
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
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
		        
		        cursor.close();
		        
		        selectedAttachment = HSAttachment.createAttachment(selectedImage.toString(), display_name, mime_type);
				
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
				selectedBitmap = downscaleAndReadBitmap(uri);
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
	
	public void sendSuccessSignal(HSTicket ticket) {
		Intent intent = new Intent();
		intent.putExtra(RESULT_TICKET, ticket);
		HSActivityManager.sendSuccessSignal(getActivity(), intent);
	}
}
