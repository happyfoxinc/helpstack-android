package com.tenmiles.helpstack.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.ArticleActivity;
import com.tenmiles.helpstack.activities.SectionActivity;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSKBItem;

public class SectionFragment extends HSFragmentParent {

	public HSKBItem kbItem;
	
	private ListView mListView;
	private HSSource gearSource;
	private HSKBItem[] fetchedKbItems;
	
	private SearchFragment mSearchFragment;
	
	public SectionFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_section, container, false);
		
		mListView = (ListView)rootView.findViewById(R.id.sectionlistview);
		gearSource = new HSSource (getActivity());
		
		mSearchFragment = new SearchFragment();
        HSFragmentManager.putFragmentInActivity(getHelpStackActivity(), R.id.search_container, mSearchFragment, "Search");
        
        setHasOptionsMenu(true);
        
		initializeView();
		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.search, menu);
        
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchFragment.addSearchViewInMenuItem(getActivity(), searchItem);
	}
	
	private void initializeView() {
		
		getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		
		gearSource.requestKBArticle(this.kbItem, new OnFetchedArraySuccessListener() {
			
			@Override
			public void onSuccess(Object[] successObject) {
				fetchedKbItems = (HSKBItem[])successObject;
				mSearchFragment.setKBArticleList(fetchedKbItems);
				refreshList();
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void refreshList() {
		SectionAdapter sectionAdapter = new SectionAdapter(this.fetchedKbItems);
		mListView.setAdapter(sectionAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HSKBItem kbItemClicked = (HSKBItem) fetchedKbItems[position];
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
	}
	
	private class SectionAdapter extends BaseAdapter {

		HSKBItem[] kbItems;
		
		public SectionAdapter (HSKBItem[] kbItems) {
			this.kbItems = kbItems;
		}
		
		@Override
		public int getCount() {
			return this.kbItems.length;
		}

		@Override
		public Object getItem(int position) {
			return this.kbItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			if(convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.sectionlist_article, null);
				holder.title = (TextView)convertView.findViewById(R.id.sectionlisttextview);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.title.setText(((HSKBItem)getItem(position)).getSubject());
			
			return convertView;
		}
		
		private class ViewHolder {
			TextView title;
		}
		
	}
}
