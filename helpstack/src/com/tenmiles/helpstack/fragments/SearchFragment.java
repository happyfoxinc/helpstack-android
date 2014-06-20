package com.tenmiles.helpstack.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import com.tenmiles.helpstack.activities.ArticleActivity;
import com.tenmiles.helpstack.activities.SectionActivity;
import com.tenmiles.helpstack.model.HSKBItem;
import com.android.internal.util.*;

/**
 * Search Fragment
 * 
 */
public class SearchFragment extends HSFragmentParent {

	private View rootView;
	private SearchAdapter searchAdapter;
	private HSKBItem[] allKbArticles;
	private ListView listView;
	
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
		listView.setAdapter(searchAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HSKBItem kbItemClicked = (HSKBItem) searchAdapter.getItem(position);
				if(kbItemClicked.getArticleType() == HSKBItem.TYPE_ARTICLE) {
					//Type article
					Intent intent = new Intent(getActivity(), ArticleActivity.class);
					intent.putExtra("item", kbItemClicked);
					startActivity(intent);
					
				} else {
					//Type section
					Intent intent = new Intent(getActivity(), SectionActivity.class);
					intent.putExtra("section_item", kbItemClicked);
					startActivity(intent);
				}
				
			}
		});
		
		return rootView;
	}

	public void searchStarted() {
		searchAdapter.refreshList(allKbArticles);
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
	
	public void addSearchViewInMenuItem(Context context, MenuItem searchItem) {
		MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS|MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		SearchView searchView = new SearchView(context);
		MenuItemCompat.setActionView(searchItem, searchView);
		searchView.setQueryHint("Search");
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
				}else {
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
