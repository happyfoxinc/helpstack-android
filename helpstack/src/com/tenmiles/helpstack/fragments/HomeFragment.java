package com.tenmiles.helpstack.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;

/**
 * Initial Fragment that contains FAQ and Tickets
 * 
 * @author Nalin Chhajer
 *
 */
public class HomeFragment extends HSFragmentParent {

	private ExpandableListView mExpandableListView;
	private LocalAdapter mAdapter;
	
	public HomeFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		 
         View rootView = inflater.inflate(R.layout.fragment_home, container, false);
         
         mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList); 
         mAdapter = new LocalAdapter(getActivity());
         
         mExpandableListView.setAdapter(mAdapter);
         
         initializeView();
         
         return rootView;
    }
	 
	private void initializeView() {
		
		mAdapter.clearAll();
		
		mAdapter.addParent(0, "FAQ");
		
		mAdapter.addParent(1, "ISSUES");
		
		mAdapter.notifyDataSetChanged();
		
		expandAll();
	}
	
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
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			ChildViewHolder holder;
			
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.expandable_child_home_default, null);
				holder = new ChildViewHolder();
				
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				
				
				convertView.setTag(holder);
			}
			else {
				holder = (ChildViewHolder) convertView.getTag();
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
		}
	 }
}
