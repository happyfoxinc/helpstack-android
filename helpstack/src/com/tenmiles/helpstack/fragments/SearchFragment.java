package com.tenmiles.helpstack.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.model.HSKBItem;

/**
 * Search Fragment
 * 
 */
public class SearchFragment extends HSFragmentParent {

	private View rootView;
	private SearchAdapter searchAdapter;

	public SearchFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		rootView =  inflater.inflate(R.layout.fragment_search, container, false);
		setVisibility(false);
		searchAdapter = new SearchAdapter(null);
		return rootView;
	}

	public void searchStarted() {
		// TODO Auto-generated method stub
		Log.v("search","started");
	}

	public void doSearchForQuery(String q) {
		// TODO Auto-generated method stub
		Log.v("search","do querry");
	}

	public void setVisibility(boolean visible) {
		if (visible) {
			rootView.setVisibility(View.VISIBLE);
		}
		else {
			rootView.setVisibility(View.GONE);
		}
	}
	
	private class SearchAdapter extends BaseAdapter {

		private HSKBItem[] searchResults;
		
		public SearchAdapter(HSKBItem[] list) {
			this.searchResults = list;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchResults.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return searchResults[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.sectionlist_article, null);
				holder = new ViewHolder();
				holder.textview = (TextView)convertView.findViewById(R.id.sectionlisttextview);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.textview.setText(((HSKBItem)this.searchResults[position]).getSubject());
			return convertView;
		}
		
		private class ViewHolder {
			private TextView textview;
		}
		
	}

}
