package com.tenmiles.helpstack.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityParent;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSError;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sureshkumar on 24/12/16.
 */


/**
 * TaskFragment manages a single background task and retains itself across
 * configuration changes.
 * <p>
 * ref: http://www.vogella.com/tutorials/AndroidFragments/article.html#headless-fragments
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TaskFragment extends HSFragmentParent {

    protected static final String TASK_TICKETS = "task_tickets";
    protected static final String TASK_KB_ARTICLES = "task_kb_articles";

    private Activity mActivity;
    private List<String> mTasks;
    private Task mTask;
    private TaskResponse mResponse;
    private TaskCallbacks mCallbacks;
    private boolean mRunning;
    private boolean isFailed;
    private HSSource mGearSource;

    /**
     * Hold a reference to the parent Activity so we can report the task's current
     * progress and results. The Android framework will pass us a reference to the
     * newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HSActivityParent) context;
    }

    /**
     * This method is called once when the Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            mCallbacks = (TaskCallbacks) getTargetFragment();
    }

    /**
     * Note that this method is <em>not</em> called when the Fragment is being
     * retained across Activity instances. It will, however, be called when its
     * parent Activity is being destroyed for good (such as when the user clicks
     * the back button, etc.).
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    /**
     * Start the background task.
     */
    public void startTask(HSSource gearSource, String[] tasks) {
        if (!mRunning) {
            mGearSource = gearSource;
            mTasks = new ArrayList<>(Arrays.asList(tasks));
            mTask = new Task();
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tasks);
            mResponse = new TaskResponse();
            mRunning = true;
        }
    }

    /**
     * Cancel the background task.
     */
    public void cancelTask() {
        if (mRunning) {
            mGearSource = null;
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    /**
     * Returns the current state of the background task.
     */
    public boolean isRunning() {
        return mRunning;
    }

    private void fetchKbArticles(final Task task) {
        mGearSource.requestKBArticle("FAQ", null, new OnFetchedArraySuccessListener() {
            @Override
            public void onSuccess(Object[] kbArticles) {
                mResponse.kbArticles = (HSKBItem[]) kbArticles;
                mTasks.remove(TASK_KB_ARTICLES);
                task.onPostExecute(mResponse);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                task.onPostExecute(handleError(mActivity, error));
            }
        });
    }

    private void fetchTickets(final Task task) {
        mGearSource.requestAllTickets(new OnFetchedArraySuccessListener() {
            @Override
            public void onSuccess(Object[] tickets) {
                mResponse.tickets = (HSTicket[]) tickets;
                mTasks.remove(TASK_TICKETS);
                task.onPostExecute(mResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                task.onPostExecute(handleError(mActivity, error));
            }
        });
    }

    private HSError handleError(Context context, VolleyError error) {
        HSError hsError = new HSError();
        String message = "";
        if ((error instanceof NoConnectionError) || (error instanceof TimeoutError)) {
            hsError.initWithNetworkError(context.getResources().getString(R.string.hs_error_check_network_connection));
            message = context.getResources().getString(R.string.hs_error_check_network_connection);
        } else {
            hsError.initWithAppError(context.getResources().getString(R.string.hs_error_fetching_articles_issues));
            message = context.getResources().getString(R.string.hs_error_fetching_articles_issues);
        }
        hsError.message = message;
        if(error.networkResponse != null)
            hsError.httpResponseCode = error.networkResponse.statusCode;
        return hsError;
    }

    /**
     * Callback interface through which the fragment can report the task's
     * progress and results back to the Activity.
     */
    static interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute(Object object);
    }

    private class Task extends AsyncTask<String, Integer, Object> {
        @Override
        protected void onPreExecute() {
            // Proxy the call to the Activity.
            if (mCallbacks != null)
                mCallbacks.onPreExecute();
            mRunning = true;
        }

        @Override
        protected Object doInBackground(String... params) {
            for (int i = 0; i < params.length; i++) {
                Log.d("Loading task", params[i]);
                if (params[i].equals(TASK_KB_ARTICLES)) {
                    fetchKbArticles(this);
                } else if (params[i].equals(TASK_TICKETS)) {
                    fetchTickets(this);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // Proxy the call to the Activity.
            if (mCallbacks != null)
                mCallbacks.onProgressUpdate(percent[0]);
        }

        @Override
        protected void onCancelled() {
            // Proxy the call to the Activity.
            if (mCallbacks != null)
                mCallbacks.onCancelled();
            mRunning = false;
        }

        @Override
        protected void onPostExecute(Object object) {
            // Proxy the call to the Activity.
            if(object != null) {
                if (object instanceof HSError && !isFailed) {
                    mRunning = false;
                    isFailed = true;
                    if (mCallbacks != null)
                        mCallbacks.onPostExecute(object);
                    cancelTask();
                }

                if (mTasks.size() == 0 && !isFailed) {
                    mRunning = false;
                    if (mCallbacks != null)
                        mCallbacks.onPostExecute(object);
                    cancelTask();
                }
            }
        }
    }

    class TaskResponse {
        HSKBItem[] kbArticles;
        HSTicket[] tickets;
    }

}
