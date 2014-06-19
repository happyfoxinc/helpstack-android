package com.tenmiles.helpstack.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.ArticleActivity;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.activities.NewIssueActivity;
import com.tenmiles.helpstack.activities.SectionActivity;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;

/**
 * Initial Fragment that contains FAQ and Tickets
 * 
 * @author Nalin Chhajer
 *
 */
public class HomeFragment extends HSFragmentParent {

	private ExpandableListView mExpandableListView;
	private LocalAdapter mAdapter;
	
	private HSSource gearSource;
	
	private HSKBItem[] fetchedKbArticles;
	private HSTicket[] fetchedTickets;

	
	public HomeFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		 
         View rootView = inflater.inflate(R.layout.fragment_home, container, false);
         
         mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList); 
         mAdapter = new LocalAdapter(getActivity());
         
         View report_an_issue_view = inflater.inflate(R.layout.expandable_footer_report_issue, null);

         report_an_issue_view.findViewById(R.id.button1).setOnClickListener(reportIssueClickListener);

         mExpandableListView.addFooterView(report_an_issue_view);
         
         mExpandableListView.setAdapter(mAdapter);
         
         gearSource = new HSSource (getActivity());
         
         if (savedInstanceState == null) {
        	 initializeView();
         }
         else {
        	 fetchedKbArticles = (HSKBItem[]) savedInstanceState.getSerializable("kbArticles");
        	 fetchedTickets = (HSTicket[]) savedInstanceState.getSerializable("tickets");
        	 refreshList();
         }
         
         initializeView();

         mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Log.v("HelpStack", "Item clicked");
				
				if (groupPosition == 0) {
					HSKBItem kbItemClicked = (HSKBItem) mAdapter.getChild(groupPosition, childPosition);
					if(kbItemClicked.getArticleType() == HSKBItem.TYPE_ARTICLE) {
						//Type article
						Intent intent = new Intent(getActivity(), ArticleActivity.class);
						intent.putExtra("item", kbItemClicked);
						startActivity(intent);
						return true;
						
					} else {
						//Type section
						Intent intent = new Intent(getActivity(), SectionActivity.class);
						intent.putExtra("section_item", kbItemClicked);
						startActivity(intent);
						return true;
					}
				}
				if (groupPosition == 1) {
					HSTicket ticket = (HSTicket) mAdapter.getChild(groupPosition, childPosition);
					HSActivityManager.startIssueDetailActivity(getActivity(), ticket);
					return true;
					
				}
				return false;
			}
		});
         
         return rootView;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("kbArticles", fetchedKbArticles);
		outState.putSerializable("tickets", fetchedTickets);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1003) {
			if (resultCode == HSActivityManager.resultCode_sucess) {
				ArrayList<HSTicket> temp = new ArrayList<HSTicket>();
				temp.add((HSTicket)data.getSerializableExtra(NewIssueActivity.RESULT_TICKET));
				temp.addAll(Arrays.asList(fetchedTickets));
				HSTicket[] array = new HSTicket[0];
				array = temp.toArray(array);
				fetchedTickets = array;
				refreshList();
			}
		}
	}
	 
	private void initializeView() {
		
		getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		
		
		
		// Show Loading
		gearSource.requestKBArticle(null, new OnFetchedArraySuccessListener() {
			
			

			@Override
			public void onSuccess(Object[] kbArticles) {
				
				fetchedKbArticles = (HSKBItem[]) kbArticles;
				refreshList();
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				// Stop Loading
			}
			
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// Stop Loading
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
			
		});
		
		gearSource.requestAllTickets(new OnFetchedArraySuccessListener() {
			
			@Override
			public void onSuccess(Object[] tickets) {
				fetchedTickets = (HSTicket[]) tickets;
				refreshList();
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Stop Loading
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				
			}
		});
		
		refreshList();
	}
	
	private void refreshList() {
		
		mAdapter.clearAll();
		
		mAdapter.addParent(0, "FAQ");
		
		if (fetchedKbArticles != null) {
			for (int i = 0; i < fetchedKbArticles.length ; i++) {
				
				HSKBItem item = (HSKBItem) fetchedKbArticles[i];
				mAdapter.addChild(0, item);
			}
		}
		
		
		if (fetchedTickets != null && fetchedTickets.length > 0) {
			mAdapter.addParent(1, "ISSUES");
			
			for (int i = 0; i < fetchedTickets.length ; i++) {
				
				HSTicket item = (HSTicket) fetchedTickets[i];
				mAdapter.addChild(1, item);
			}
		}
		
		
		mAdapter.notifyDataSetChanged();
		
		expandAll();
	}
	
	private void expandAll() {
		int count = mAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			mExpandableListView.expandGroup(i);
		}
	}
	
	private OnClickListener reportIssueClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if (gearSource.haveImplementedTicketFetching()) {
				if(gearSource.isNewUser()) {
					HSActivityManager.startNewUserActivity(HomeFragment.this, 1003);
				}else {
					HSActivityManager.startNewIssueActivity(HomeFragment.this, gearSource.getUser(), 1003);
				}
			}
			else {
				gearSource.launchEmailAppWithEmailAddress(getActivity());
			}
		}
	};

	private class LocalAdapter extends HSBaseExpandableListAdapter
	 {

		public LocalAdapter(Context context) {
			super(context);
		}

		@Override
		public View getChildView(final int groupPosition,final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			ChildViewHolder holder;
			
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.expandable_child_home_default, null);
				holder = new ChildViewHolder();
				
				holder.parent = convertView;
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			
			if (groupPosition == 0) {
				final HSKBItem item = (HSKBItem) getChild(groupPosition, childPosition);
				holder.textView1.setText(item.getSubject());
				
				
			}
			else if (groupPosition == 1){
				HSTicket item = (HSTicket) getChild(groupPosition, childPosition);
				holder.textView1.setText(item.getSubject());
			}
			
			return convertView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ParentViewHolder holder;
			
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.expandable_parent_home_default, null);
				holder = new ParentViewHolder();
				
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ParentViewHolder) convertView.getTag();
			}
			
			String text = (String) getGroup(groupPosition);
			
			holder.textView1.setText(text);
			
			return convertView;
		}
		
		private class ParentViewHolder {
			TextView textView1;
		}
		
		private class ChildViewHolder {
			TextView textView1;
			View parent;
		}
	 }
}
