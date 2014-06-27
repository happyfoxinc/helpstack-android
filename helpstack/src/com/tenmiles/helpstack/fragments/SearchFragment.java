package com.tenmiles.helpstack.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import com.tenmiles.helpstack.logic.HSSource;
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
	private HSSource gearSource;
	
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
		
		gearSource = new HSSource (getActivity());
		
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
		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setSubmitButtonEnabled(true);

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
			// TODO Auto-generated method stub
			return searchResults[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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
			            if (p.getSubject().toUpperCase().startsWith(constraint.toString().toUpperCase()))
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
