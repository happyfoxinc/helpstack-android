//  SearchFragment
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

package com.tenmiles.helpstack.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.model.HSKBItem;

/**
 * Search Fragment
 * 
 */
public class SearchFragment extends HSFragmentParent {

	private View rootView;
	private SearchAdapter searchAdapter;
	private HSKBItem[] allKbArticles;
	private ListView listView;
	
	private OnReportAnIssueClickListener articleSelecetedListener;

	public SearchFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		rootView =  inflater.inflate(R.layout.fragment_search, container, false);
		setVisibility(false);
		listView = (ListView)rootView.findViewById(R.id.searchList);
		searchAdapter = new SearchAdapter(this.allKbArticles);
		
		View report_an_issue_view = inflater.inflate(R.layout.expandable_footer_report_issue, null);
        report_an_issue_view.findViewById(R.id.button1).setOnClickListener(reportIssueClickListener);

        listView.addFooterView(report_an_issue_view);
		
		listView.setAdapter(searchAdapter);
		
		listView.setOnItemClickListener(listItemClickListener);
		
		return rootView;
	}

	public void searchStarted() {
		searchAdapter.refreshList(allKbArticles);
		searchAdapter.getFilter().filter("");
		searchAdapter.notifyDataSetChanged();
	}

	public void doSearchForQuery(String q) {
		searchAdapter.getFilter().filter(q);
	}

	public boolean isSearchVisible() {
		if (rootView == null) {
			return false;
		}
		return rootView.getVisibility() == View.VISIBLE;
	}
	
	public void setVisibility(boolean visible) {
		if (visible) {
			rootView.setVisibility(View.VISIBLE);
		}
		else {
			rootView.setVisibility(View.GONE);
		}
	}
	
	public void setKBArticleList(HSKBItem[] fetchedKbArticles) {
		this.allKbArticles = fetchedKbArticles;
		if (isSearchVisible()) {
			searchAdapter.refreshList(allKbArticles);
			searchAdapter.getFilter().filter("");
			searchAdapter.notifyDataSetChanged();
		}
	}
	
	protected OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			HSKBItem kbItemClicked = (HSKBItem) searchAdapter.getItem(position);
			articleClickedOnPosition(kbItemClicked);
		}
	};
	
	protected void articleClickedOnPosition(HSKBItem kbItemClicked) {
		if(kbItemClicked.getArticleType() == HSKBItem.TYPE_ARTICLE) {
			HSActivityManager.startArticleActivity(this, kbItemClicked, HomeFragment.REQUEST_CODE_NEW_TICKET);
			
		} else {
			HSActivityManager.startSectionActivity(this, kbItemClicked, HomeFragment.REQUEST_CODE_NEW_TICKET);
		}
	}
	
	public void addSearchViewInMenuItem(Context context, MenuItem searchItem) {
		MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS|MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		SearchView searchView = new SearchView(context);
		MenuItemCompat.setActionView(searchItem, searchView);
		searchView.setQueryHint(getString(R.string.hs_search_hint));
		searchView.setSubmitButtonEnabled(false);

		searchView.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchStarted();
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String q) {
				
				doSearchForQuery(q);
				
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				doSearchForQuery(newText);
				return true;
			}
		});


		MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				setVisibility(true);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				setVisibility(false);
				return true;
			}
		});
	}
	
	private OnClickListener reportIssueClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if (articleSelecetedListener != null) {
				articleSelecetedListener.startReportAnIssue();
			}
		}
	};
	
	public void setOnReportAnIssueClickListener(OnReportAnIssueClickListener listener) {
		this.articleSelecetedListener = listener;
	}
	
	public interface OnReportAnIssueClickListener {
		public void startReportAnIssue();
	}
	
	private class SearchAdapter extends BaseAdapter implements Filterable{

		private HSKBItem[] allKBItems;
		private HSKBItem[] searchResults;
		private CustomFilter filter;
		
		public SearchAdapter(HSKBItem[] list) {
			this.allKBItems = list;
		}
		
		public void refreshList(HSKBItem[] list) {
			this.allKBItems = list;
		}
		
		@Override
		public int getCount() {
			if(searchResults == null) {
				return 0;
			}
			return searchResults.length;
		}

		@Override
		public Object getItem(int position) {
			return searchResults[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
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

		@Override
		public Filter getFilter() {
			if(filter == null) {
				filter = new CustomFilter();
			}
			return filter;
		}
		
		private class CustomFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if(constraint == null || constraint.length() == 0){
					
					results.values = (HSKBItem[])allKBItems;
					results.count = allKBItems.length;
					
				} else {
					// We perform filtering operation
			        List<HSKBItem> filterList = new ArrayList<HSKBItem>();
			         
			        for (HSKBItem p : allKBItems) {
			            if (p.getSubject().toUpperCase().contains(constraint.toString().toUpperCase())) //.startsWith(constraint.toString().toUpperCase()))
			            	filterList.add(p);
			        }
			        HSKBItem[] values = filterList.toArray(new HSKBItem[filterList.size()]);
			        results.values = values;
			        results.count = filterList.size();
				}
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if(results == null) {
					notifyDataSetInvalidated();
				}else {
					searchResults = (HSKBItem[]) results.values;
					notifyDataSetChanged();
				}
				
			}
			
		}
		
	}
}
