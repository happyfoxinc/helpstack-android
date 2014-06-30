package com.tenmiles.helpstack.fragments;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter.OnChildItemClickListener;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.logic.OnFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;
import com.tenmiles.helpstack.service.DownloadAttachmentUtility;

public class IssueDetailFragment extends HSFragmentParent 
{

	private final int REQUEST_CODE_PHOTO_PICKER = 100;
	
	public IssueDetailFragment() {
	}
	
	private ExpandableListView mExpandableListView;
	private LocalAdapter mAdapter;
	private Button sendButton;
	private EditText replyEditTextView;
	private ImageView mAttachmentButton;
	
	private HSSource gearSource;
	private HSTicket ticket;
	private HSTicketUpdate[] fetchedUpdates;
	private HSAttachment selectedAttachment;
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_issue_detail, null);
		
		
		replyEditTextView = (EditText) rootView.findViewById(R.id.replyEditText);
		sendButton = (Button)rootView.findViewById(R.id.button1);
		sendButton.setOnClickListener(sendReplyListener);
		
		mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList);
		mAttachmentButton = (ImageView) rootView.findViewById(R.id.attachmentbutton);
		
		mAttachmentButton.setOnClickListener(attachmentClickListener);
		
        mAdapter = new LocalAdapter(getActivity());
        
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        
        gearSource = new HSSource(getActivity());
		
        mAdapter.setOnChildItemClickListener(listChildClickListener);
        
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null ) {
			refreshUpdateFromServer();
		}
		else {
			fetchedUpdates = (HSTicketUpdate[]) savedInstanceState.getSerializable("updates");
			ticket = (HSTicket) savedInstanceState.getSerializable("ticket");
			selectedAttachment = (HSAttachment) savedInstanceState.getSerializable("selectedAttachment");
			replyEditTextView.setText(savedInstanceState.getString("replyEditTextView"));
			
			
			refreshList();
			resetAttachmentImage();
			
			if (fetchedUpdates == null) {
				// may be due to orientation change during last fetch operation
				refreshUpdateFromServer();
			}
		}
		
		refreshList();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("updates", fetchedUpdates);
		outState.putSerializable("ticket", ticket);
		outState.putSerializable("selectedAttachment", selectedAttachment);
		outState.putSerializable("replyEditTextView", replyEditTextView.getText().toString());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if(requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == Activity.RESULT_OK)
		{      
			
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
	};
	
	@Override
	public void onDetach() {
		gearSource.cancelOperation("REPLY_TO_A_TICKET");
		gearSource.cancelOperation("ALL_UPDATES");
		super.onDetach();
	}
	
	private void refreshUpdateFromServer() {
		
		getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		
		gearSource.requestAllUpdatesOnTicket("ALL_UPDATES", ticket, new OnFetchedArraySuccessListener() {
			
			@Override
			public void onSuccess(Object[] successObject) {
				
				fetchedUpdates = (HSTicketUpdate[]) successObject;
				
				refreshList();
				
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				scrollListToBottom();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				HSUtils.showAlertDialog(getActivity(), "Error", "Error in fetching ticket updates");
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
		});
	}
	
	private OnChildItemClickListener listChildClickListener = new OnChildItemClickListener() {
		
		@Override
		public boolean onChildListItemLongClick(int groupPosition,
				int childPosition, String type, Object map) {
			return false;
		}
		
		@Override
		public void onChildListItemClick(int groupPosition, int childPosition,
				String type, Object map) {
			showAttachments(((HSTicketUpdate)map).getAttachments());
		}
		
		@Override
		public void onChildCheckedListner(int groupPosition, int childPosition,
				String type, Object map, boolean checked) {
			
		}
	};
	

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
	
	private OnClickListener sendReplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			String message = replyEditTextView.getText().toString();
			if(message.trim().length() == 0) {
				return;
			}
			
			getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
			sendButton.setEnabled(false);
			
			HSAttachment[] attachmentArray = null;
			
			if (selectedAttachment != null) {
				attachmentArray = new HSAttachment[1];
				attachmentArray[0] = selectedAttachment;
			}
			
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(replyEditTextView.getWindowToken(), 0);
				
			gearSource.addReplyOnATicket("REPLY_TO_A_TICKET", message, attachmentArray, ticket, new OnFetchedSuccessListener() {
				
				@Override
				public void onSuccess(Object successObject) {
					sendButton.setEnabled(true);
					HSTicketUpdate update = (HSTicketUpdate) successObject;
					
					ArrayList<HSTicketUpdate> updateList = new ArrayList<HSTicketUpdate>();
					updateList.addAll(Arrays.asList(fetchedUpdates));
					updateList.add(update);
					
					HSTicketUpdate[] updateArray = new HSTicketUpdate[0];
					fetchedUpdates = updateList.toArray(updateArray);
					
					refreshList();
					
					selectedAttachment = null;
					replyEditTextView.setText("");
					resetAttachmentImage();
					
					
					getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
					
					scrollListToBottom();
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					HSUtils.showAlertDialog(getActivity(), "Error", "Error in posting your reply, please check your network connection");
					sendButton.setEnabled(true);
					getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				}
			});
		}
	};
	
	private void expandAll() {
		int count = mAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			mExpandableListView.expandGroup(i);
		}
	}
	
	private void refreshList() {
		
		mAdapter.clearAll();
		
		if (fetchedUpdates != null) {
			mAdapter.addParent(1, "");
			for (int i = 0; i < fetchedUpdates.length; i++) {
				mAdapter.addChild(1, fetchedUpdates[i]);
			}
		}
		
		mAdapter.notifyDataSetChanged();
		
		expandAll();
	}

	private void showAttachments(final HSAttachment[] attachmentsArray) {
		
		if (attachmentsArray.length == 1) {
			HSAttachment attachmentToShow = attachmentsArray[0];
			openAttachment(attachmentToShow);
			return;
		}
		
		ArrayList<String> attachments = new ArrayList<String>();
		for(HSAttachment attachment : attachmentsArray) {
			attachments.add(attachment.getFileName());
		}
		String[] attachmentNames = attachments.toArray(new String[attachments.size()]);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.attachment_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Attachments");
        final AlertDialog dialog = alertDialog.create();
        
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,attachmentNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HSAttachment attachmentToShow = attachmentsArray[position];
				openAttachment(attachmentToShow);
				dialog.dismiss();
			}
		});
        
        dialog.show();
	}


	private class LocalAdapter extends HSBaseExpandableListAdapter 
	{
		public LocalAdapter(Context context) {
			super(context);
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {
				holder = new ChildViewHolder();
				if (getChildType(groupPosition, childPosition) == 0) {
					convertView = mLayoutInflater.inflate(R.layout.expandable_child_issue_detail_staff_reply, null);
				}
				else {
					convertView = mLayoutInflater.inflate(R.layout.expandable_child_issue_detail_user_reply, null);
				}
				
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				holder.nameField = (TextView) convertView.findViewById(R.id.name);
				holder.timeField = (TextView) convertView.findViewById(R.id.time);
				holder.attachmentButton = (ImageView) convertView.findViewById(R.id.attachment_icon);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			
			// This is a dummy view as only 1 group is gonna be used.
			final HSTicketUpdate update = (HSTicketUpdate) getChild(groupPosition, childPosition);
			
			holder.textView1.setText(update.getText().trim());
			if(update.isUserUpdate()) {
				holder.nameField.setText("Me");
			}else {
				if(update.name != null) {
					holder.nameField.setText(update.name);
				} else {
					holder.nameField.setText("Staff");
				}
			}
			
			if(update.isAttachmentEmtpy()) {
				holder.attachmentButton.setVisibility(View.INVISIBLE);
			}else {
				holder.attachmentButton.setVisibility(View.VISIBLE);
				holder.attachmentButton.setFocusable(true);
				holder.attachmentButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						sendChildClickEvent(groupPosition, childPosition, "attachment", update);
					}
				});
			}
			
			Date updatedTime = update.getUpdatedTime();
			
			String dateString = HSUtils.convertToHumanReadableTime(updatedTime, Calendar.getInstance().getTimeInMillis());
			holder.timeField.setText(dateString.trim());
			
			return convertView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ParentViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.expandable_parent_issue_detail_default, null);
				holder = new ParentViewHolder();
				holder.parent = convertView;
				
				convertView.setTag(holder);
			}
			else {
				holder = (ParentViewHolder) convertView.getTag();
			}
			
			// This is a dummy view as only 1 group is gonna be used.
			holder.parent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Empty to avoid expand/collapse
				}
			});
			
			return convertView;
		}
		
		@Override
		public int getChildTypeCount() {
			return 2;
		}
		
		@Override
		public int getChildType(int groupPosition, int childPosition) {
			HSTicketUpdate update = (HSTicketUpdate) getChild(groupPosition, childPosition);
			return update.isStaffUpdate()?0:1;
		}
		
		private class ParentViewHolder {
			View parent;
		}
		
		private class ChildViewHolder {
			public TextView textView1;
			public TextView nameField;
			public TextView timeField;
			public ImageView attachmentButton;
		}
	}
	
	private void resetAttachmentImage() {
		if (selectedAttachment == null) {
			this.mAttachmentButton.setImageResource(R.drawable.ic_action_attachment);
		}
		else {
			
			try {
				Uri uri = Uri.parse(selectedAttachment.getUrl());
				Bitmap selectedBitmap;
				selectedBitmap = NewIssueFragment.downscaleAndReadBitmap(getActivity(), uri);
				this.mAttachmentButton.setImageBitmap(selectedBitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	private void scrollListToBottom() {
		mExpandableListView.setSelectedChild(0, mAdapter.getChildrenCount(0) - 1, true);
	}
	

	/**
	 * @return the ticket
	 */
	public HSTicket getTicket() {
		return ticket;
	}


	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(HSTicket ticket) {
		this.ticket = ticket;
	}
	
	
	
	/// Attachments
	private void openAttachment(HSAttachment attachment) {
		if(knownAttachmentType(attachment)) {
			HSActivityManager.startImageAttachmentDisplayActivity(getHelpStackActivity(), attachment.getUrl(), attachment.getFileName());
		}
		else {
			downloadAttachment(attachment);
		}

	}
	
	private boolean knownAttachmentType(HSAttachment attachment) 
	{
		String mime_type = attachment.getMime_type();
		if (mime_type != null  && mime_type.startsWith("image")) {
			return true;
		}
		String file_name = attachment.getFileName();
		if (file_name != null && isKnowFileNameType(file_name)) {
			return true;
		}
		return false;
	}
	
	private boolean isKnowFileNameType(String file_name) {
		// get the type of file
		StringTokenizer strtok = new StringTokenizer(file_name, ".");

		// getting the last token
		String fileType = null;
		while (strtok.hasMoreTokens()) {
			// parsing to get last token
			fileType = strtok.nextToken();
		}
		String[] knownFileType = {"png", "jpg", "jpeg"};
		if(containString(knownFileType,fileType.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	private boolean containString(String[] array, String data) {
		for (int i = 0; i < array.length; i++) {
			if(array[i].contains(data)) {
				return true;
			}
		}
		return false;
	}

	public void downloadAttachment(HSAttachment attachment) {
		DownloadAttachmentUtility.downloadAttachment(getActivity(), attachment.getUrl(), attachment.getFileName());
	}
	
}
