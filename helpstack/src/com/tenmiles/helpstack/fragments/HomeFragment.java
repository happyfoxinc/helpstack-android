package com.tenmiles.helpstack.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.activities.NewIssueActivity;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;

/**
 * Initial Fragment of HelpStack that contains FAQ and Tickets
 * 
 * @author Nalin Chhajer
 *
 */
public class HomeFragment extends HSFragmentParent {

	public static final int REQUEST_CODE_NEW_TICKET = 1003;

	private ExpandableListView mExpandableListView;
	private LocalAdapter mAdapter;

	private SearchFragment mSearchFragment;

	private HSSource gearSource;

	private HSKBItem[] fetchedKbArticles;
	private HSTicket[] fetchedTickets;
	
	// To show loading until both the kb and tickets are not fetched.
	private int numberOfServerCallWaiting = 0;

	public HomeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		// ListView
		mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList); 
		mAdapter = new LocalAdapter(getActivity());

		// report an issue
		View report_an_issue_view = inflater.inflate(R.layout.expandable_footer_report_issue, null);
		report_an_issue_view.findViewById(R.id.button1).setOnClickListener(reportIssueClickListener);
		mExpandableListView.addFooterView(report_an_issue_view);

		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setOnChildClickListener(expandableChildViewClickListener);


		
		// Search fragment
		mSearchFragment = new SearchFragment();
		HSFragmentManager.putFragmentInActivity(getHelpStackActivity(), R.id.search_container, mSearchFragment, "Search");
		// Add search Menu
		setHasOptionsMenu(true);

		// Initialize gear
		gearSource = new HSSource (getActivity());

		// handle orientation
		if (savedInstanceState == null) {
			initializeView();
		}
		else {
			fetchedKbArticles = (HSKBItem[]) savedInstanceState.getSerializable("kbArticles");
			fetchedTickets = (HSTicket[]) savedInstanceState.getSerializable("tickets");
			numberOfServerCallWaiting = savedInstanceState.getInt("numberOfServerCallWaiting");
			mSearchFragment.setKBArticleList(fetchedKbArticles);
			if (numberOfServerCallWaiting > 0) { // To avoid error during orientation
				initializeView(); // refreshing list from server
			}
			else {
				refreshList();
			}
			
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("kbArticles", fetchedKbArticles);
		outState.putSerializable("tickets", fetchedTickets);
		outState.putInt("numberOfServerCallWaiting", numberOfServerCallWaiting);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_NEW_TICKET) {
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.search, menu);

		MenuItem searchItem = menu.findItem(R.id.search);
		mSearchFragment.addSearchViewInMenuItem(getActivity(), searchItem);
	}
	
	private void initializeView() {

		startHomeScreenLoadingDisplay(true);
		
		// Show Loading
		gearSource.requestKBArticle(null, new OnFetchedArraySuccessListener() {



			@Override
			public void onSuccess(Object[] kbArticles) {

				fetchedKbArticles = (HSKBItem[]) kbArticles;
				mSearchFragment.setKBArticleList(fetchedKbArticles);
				refreshList();
				
				// Stop Loading
				startHomeScreenLoadingDisplay(false);
				
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// Stop Loading
				startHomeScreenLoadingDisplay(false);
				if(numberOfServerCallWaiting == 0) {
					HSUtils.showAlertDialog(getActivity(), "Error", "Error in fetching articles and issues");
				}
			}

		});

		gearSource.requestAllTickets(new OnFetchedArraySuccessListener() {

			@Override
			public void onSuccess(Object[] tickets) {
				fetchedTickets = (HSTicket[]) tickets;
				refreshList();
				startHomeScreenLoadingDisplay(false);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Stop Loading
				startHomeScreenLoadingDisplay(false);
				if(numberOfServerCallWaiting == 0) {
					HSUtils.showAlertDialog(getActivity(), "Error", "Error in fetching articles and Issues");
				}

			}
		});

	}
	
	public void startHomeScreenLoadingDisplay(boolean loading) {
		if (loading) {
			numberOfServerCallWaiting = 2;
			getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		}
		else {
			// Stop Loading
			numberOfServerCallWaiting--;
			if (numberOfServerCallWaiting == 0) {
				if (getHelpStackActivity() != null) { // To handle a crash that happens if activity is re-created and we receive network response after that.
					getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				}
				
			}
		}
	}

	protected OnChildClickListener expandableChildViewClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			if (groupPosition == 0) {
				HSKBItem kbItemClicked = (HSKBItem) mAdapter.getChild(groupPosition, childPosition);
				articleClickedOnPosition(kbItemClicked);
				return true;
			}
			if (groupPosition == 1) {
				HSTicket ticket = (HSTicket) mAdapter.getChild(groupPosition, childPosition);
				HSActivityManager.startIssueDetailActivity(getActivity(), ticket);
				return true;

			}
			return false;
		}
	};

	protected OnClickListener reportIssueClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (gearSource.haveImplementedTicketFetching()) {
				if(gearSource.isNewUser()) {
					HSActivityManager.startNewUserActivity(HomeFragment.this, REQUEST_CODE_NEW_TICKET);
				}else {
					HSActivityManager.startNewIssueActivity(HomeFragment.this, gearSource.getUser(), REQUEST_CODE_NEW_TICKET);
				}
			}
			else {
				gearSource.launchEmailAppWithEmailAddress(getActivity());
			}
		}
	};
	
	

	//////////////////////////////////////
	// 		UTILITY FUNCTIONS         ///
	/////////////////////////////////////

	

	private void refreshList() {

		mAdapter.clearAll();

		mAdapter.addParent(0, getString(R.string.articles_title));

		if (fetchedKbArticles != null) {
			for (int i = 0; i < fetchedKbArticles.length ; i++) {

				HSKBItem item = (HSKBItem) fetchedKbArticles[i];
				mAdapter.addChild(0, item);
			}
		}


		if (fetchedTickets != null && fetchedTickets.length > 0) {
			mAdapter.addParent(1, getString(R.string.issues_title));

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

	protected void articleClickedOnPosition(HSKBItem kbItemClicked) {
		if(kbItemClicked.getArticleType() == HSKBItem.TYPE_ARTICLE) {
			HSActivityManager.startArticleActivity(this, kbItemClicked, REQUEST_CODE_NEW_TICKET);

		} else {
			HSActivityManager.startSectionActivity(this, kbItemClicked, REQUEST_CODE_NEW_TICKET);
		}
	}

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

				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);

				convertView.setTag(holder);
			}
			else {
				holder = (ChildViewHolder) convertView.getTag();
			}

			if (groupPosition == 0) {
				HSKBItem item = (HSKBItem) getChild(groupPosition, childPosition);
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
		}
	}
}
