package com.tenmiles.helpstack.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.activities.NewIssueActivity;
import com.tenmiles.helpstack.fragments.SearchFragment.OnReportAnIssueClickListener;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.model.HSError;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends TaskFragment implements TaskFragment.TaskCallbacks {

    public static final int REQUEST_CODE_NEW_TICKET = 1003;
    public static final String TAG_TASK_MAIN_FRAGMENT = "task_main_fragment";
    private static final String KEY_TICKETS = "tickets";
    private static final String KEY_KB_ARTICLES = "kbArticles";
    private ExpandableListView mExpandableListView;
    private LocalAdapter mAdapter;
    private SearchFragment mSearchFragment;

    private HSSource gearSource;
    protected OnClickListener reportIssueClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            gearSource.launchCreateNewTicketScreen(HomeFragment.this, REQUEST_CODE_NEW_TICKET);
        }
    };
    private HSKBItem[] fetchedKbArticles;
    private HSTicket[] fetchedTickets;
    private TaskFragment mTaskFragment;
    private ProgressBar mProgressBar;
    private int faq_position = 0;
    protected OnChildClickListener expandableChildViewClickListener = new OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (groupPosition == faq_position) {
                HSKBItem kbItemClicked = (HSKBItem) mAdapter.getChild(groupPosition, childPosition);
                articleClickedOnPosition(kbItemClicked);
                return true;
            }
            if (groupPosition == get_issues_position()) {
                HSTicket ticket = (HSTicket) mAdapter.getChild(groupPosition, childPosition);
                HSActivityManager.startIssueDetailActivity(getHelpStackActivity(), ticket);
                return true;

            }
            return false;
        }
    };
    private OnReportAnIssueClickListener reportAnIssueListener = new OnReportAnIssueClickListener() {

        @Override
        public void startReportAnIssue() {
            mSearchFragment.setVisibility(false);
            gearSource.launchCreateNewTicketScreen(HomeFragment.this, REQUEST_CODE_NEW_TICKET);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_MAIN_FRAGMENT);

        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_MAIN_FRAGMENT).commit();
            mTaskFragment.setTargetFragment(this, 10);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.hs_fragment_home, container, false);

        // ListView
        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList);
        mAdapter = new LocalAdapter(getActivity());

        View progress_bar_view = inflater.inflate(R.layout.hs_expandable_footer_progress_bar, null);
        mProgressBar = (ProgressBar) progress_bar_view.findViewById(R.id.progressBar1);
        mExpandableListView.addFooterView(progress_bar_view);

        // report an issue
        View report_an_issue_view = inflater.inflate(R.layout.hs_expandable_footer_report_issue, null);
        report_an_issue_view.findViewById(R.id.button1).setOnClickListener(reportIssueClickListener);
        mExpandableListView.addFooterView(report_an_issue_view);

        if (HSHelpStack.getInstance(getActivity()).getShowCredits()) {
            View show_credits_view = inflater.inflate(R.layout.hs_expandable_footer_powered_by_helpstack, null);
            mExpandableListView.addFooterView(show_credits_view);
        }

        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setOnChildClickListener(expandableChildViewClickListener);

        // Search fragment
        mSearchFragment = new SearchFragment();
        HSFragmentManager.putFragmentInActivity(getHelpStackActivity(), R.id.search_container, mSearchFragment, "Search");
        mSearchFragment.setOnReportAnIssueClickListener(reportAnIssueListener);
        // Add search Menu
        setHasOptionsMenu(true);

        // Initialize gear
        gearSource = HSSource.getInstance(getActivity());

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // handle orientation
        if (savedInstanceState != null) {
            fetchedKbArticles = HSUtils.getObjectFromJson(savedInstanceState.getString(KEY_KB_ARTICLES), new TypeToken<HSKBItem[]>() {
            }.getType());
            fetchedTickets = HSUtils.getObjectFromJson(savedInstanceState.getString(KEY_TICKETS), new TypeToken<HSTicket[]>() {
            }.getType());
            mSearchFragment.setKBArticleList(fetchedKbArticles);
        } else {
            mTaskFragment.startTask(gearSource, new String[]{TaskFragment.TASK_KB_ARTICLES, TaskFragment.TASK_TICKETS});
        }

        if (mTaskFragment.isRunning()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            refreshList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_KB_ARTICLES, HSUtils.convertObjectToStringJson(fetchedKbArticles, new TypeToken<HSKBItem[]>() {
        }.getType()));
        outState.putString(KEY_TICKETS, HSUtils.convertObjectToStringJson(fetchedTickets, new TypeToken<HSTicket[]>() {
        }.getType()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NEW_TICKET) {
            if (resultCode == HSActivityManager.resultCode_sucess) {
                gearSource.refreshUser();
                ArrayList<HSTicket> temp = new ArrayList<HSTicket>();
                temp.add((HSTicket) data.getSerializableExtra(NewIssueActivity.RESULT_TICKET));
                temp.addAll(Arrays.asList(fetchedTickets));
                HSTicket[] array = new HSTicket[0];
                array = temp.toArray(array);
                fetchedTickets = array;
                refreshList();
                mExpandableListView.setSelectedGroup(1);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.hs_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchFragment.addSearchViewInMenuItem(getActivity(), searchItem);
    }

    @Override
    public void onDetach() {
        gearSource.cancelOperation("FAQ");
        super.onDetach();
    }

    private void refreshList() {
        mAdapter.clearAll();

        if (fetchedTickets != null && fetchedTickets.length > 0) {
            faq_position = 1;
            mAdapter.addParent(0, getString(R.string.hs_issues_title));

            for (int i = 0; i < fetchedTickets.length; i++) {
                HSTicket item = fetchedTickets[i];
                mAdapter.addChild(0, item);
            }
        }

        mAdapter.addParent(faq_position, getString(R.string.hs_articles_title));

        if (fetchedKbArticles != null) {
            for (int i = 0; i < fetchedKbArticles.length; i++) {
                HSKBItem item = fetchedKbArticles[i];
                mAdapter.addChild(faq_position, item);
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
        if (kbItemClicked.getArticleType() == HSKBItem.TYPE_ARTICLE) {
            HSActivityManager.startArticleActivity(this, kbItemClicked, REQUEST_CODE_NEW_TICKET);
        } else {
            HSActivityManager.startSectionActivity(this, kbItemClicked, REQUEST_CODE_NEW_TICKET);
        }
    }

    private int get_issues_position() {
        return 1 - faq_position;
    }

    @Override
    public void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPostExecute(final Object object) {
        if (object != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    if (object instanceof TaskFragment.TaskResponse) {
                        TaskFragment.TaskResponse response = (TaskFragment.TaskResponse) object;
                        fetchedKbArticles = response.kbArticles;
                        fetchedTickets = response.tickets;
                        mSearchFragment.setKBArticleList(fetchedKbArticles);
                        refreshList();
                    } else if (object instanceof HSError) {
                        HSUtils.showAlertDialog(getActivity(), getResources().getString(R.string.hs_error), ((HSError) object).message);
                    }
                }
            });
        }
    }

    private class LocalAdapter extends HSBaseExpandableListAdapter {

        public LocalAdapter(Context context) {
            super(context);
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.hs_expandable_child_home_default, null);
                holder = new ChildViewHolder();
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            if (groupPosition == faq_position) {
                HSKBItem item = (HSKBItem) getChild(groupPosition, childPosition);
                holder.textView1.setText(item.getSubject());
            } else if (groupPosition == get_issues_position()) {
                HSTicket item = (HSTicket) getChild(groupPosition, childPosition);
                holder.textView1.setText(item.getSubject());
            }

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ParentViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.hs_expandable_parent_home_default, null);
                holder = new ParentViewHolder();
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(holder);
            } else {
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
