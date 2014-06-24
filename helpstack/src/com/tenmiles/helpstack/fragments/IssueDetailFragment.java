package com.tenmiles.helpstack.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.AttachmentActivity;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter.OnChildItemClickListener;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.logic.OnFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;

public class IssueDetailFragment extends HSFragmentParent 
{

	private static final int SELECT_PICTURE = 1;
	
	public IssueDetailFragment() {
		// TODO Auto-generated constructor stub
	}
	
	private HSTicket ticket;
	private ExpandableListView mExpandableListView;
	private ImageView mAttachmentButton;
	private LocalAdapter mAdapter;
	private HSSource gearSource;
	
	private HSTicketUpdate[] fetchedUpdates;
	
	private EditText replyEditTextView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_issue_detail, null);
		
		
		replyEditTextView = (EditText) rootView.findViewById(R.id.replyEditText);
		rootView.findViewById(R.id.button1).setOnClickListener(sendReplyListener);
		
		mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList); 
		mAttachmentButton = (ImageView) rootView.findViewById(R.id.attachmentbutton);
		
		mAttachmentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showGallery();
			}
		});
		
        mAdapter = new LocalAdapter(getActivity());
        
        mExpandableListView.setAdapter(mAdapter);
        
        gearSource = new HSSource(getActivity());
		
        mAdapter.setOnChildItemClickListener(new OnChildItemClickListener() {
			
			@Override
			public boolean onChildListItemLongClick(int groupPosition,
					int childPosition, String type, Object map) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				
			}
		});
        
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
		}
		
		refreshList();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("updates", fetchedUpdates);
	}
	
	private void showAttachments(final HSAttachment[] attachmentsArray) {
		ArrayList<String> attachments = new ArrayList<String>();
		for(HSAttachment attachment : attachmentsArray) {
			attachments.add(attachment.getFileName());
		}
		String[] attachmentNames = attachments.toArray(new String[attachments.size()]);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.attachment_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("List");
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,attachmentNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HSAttachment attachmentToShow = attachmentsArray[position];
				Intent intent = new Intent(getActivity(), AttachmentActivity.class);
				intent.putExtra("attachment", attachmentToShow);
				intent.putExtra("isLocalAttachment", false);
				startActivity(intent);
			}
		});
        
        alertDialog.show();
	}
	
	private void showGallery() {
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			
		}
	};

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
	
	private void refreshUpdateFromServer() {
		
		getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		
		gearSource.requestAllUpdatesOnTicket(ticket, new OnFetchedArraySuccessListener() {
			
			@Override
			public void onSuccess(Object[] successObject) {
				
				fetchedUpdates = (HSTicketUpdate[]) successObject;
				
				refreshList();
				
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
		});
	}
	
	private OnClickListener sendReplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			String message = replyEditTextView.getText().toString();
			
			getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
			
			gearSource.addReplyOnATicket(message, ticket, new OnFetchedSuccessListener() {
				
				@Override
				public void onSuccess(Object successObject) {
					
					HSTicketUpdate update = (HSTicketUpdate) successObject;
					
					ArrayList<HSTicketUpdate> updateList = new ArrayList<HSTicketUpdate>();
					updateList.addAll(Arrays.asList(fetchedUpdates));
					updateList.add(update);
					
					HSTicketUpdate[] updateArray = new HSTicketUpdate[0];
					fetchedUpdates = updateList.toArray(updateArray);
					
					refreshList();
					
					getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					
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
			
			holder.textView1.setText(update.getText());
			if(update.isUserUpdate()) {
				holder.nameField.setText("Me");
			}else {
				holder.nameField.setText("Staff");
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
			
			holder.timeField.setText(dateString);
			
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
	
}
