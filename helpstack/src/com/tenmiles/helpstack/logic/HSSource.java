//  HSSource
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

package com.tenmiles.helpstack.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.fragments.HSFragmentParent;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSCachedTicket;
import com.tenmiles.helpstack.model.HSCachedUser;
import com.tenmiles.helpstack.model.HSDraft;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;
import com.tenmiles.helpstack.model.HSUploadAttachment;
import com.tenmiles.helpstack.model.HSUser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class HSSource {
    private static final String TAG = HSSource.class.getSimpleName();

    private static final String HELPSTACK_DIRECTORY = "helpstack";
    private static final String HELPSTACK_TICKETS_FILE_NAME = "tickets";
    private static final String HELPSTACK_TICKETS_USER_DATA = "user_credential";
    private static final String HELPSTACK_DRAFT = "draft";

    private static HSSource singletonInstance = null;
    private HSGear gear;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private HSCachedTicket cachedTicket;
    private HSCachedUser cachedUser;
    private HSDraft draftObject;
    private HSSource(Context context) {
        this.mContext = context;
        setGear(HSHelpStack.getInstance(context).getGear());
        mRequestQueue = HSHelpStack.getInstance(context).getRequestQueue();
        refreshFieldsFromCache();
    }

    /**
     * @param context
     * @return singleton instance of this class.
     */
    public static HSSource getInstance(Context context) {
        if (singletonInstance == null) {
            synchronized (HSSource.class) { // 1
                if (singletonInstance == null) // 2
                {
                    Log.d(TAG, "New Instance");
                    singletonInstance = new HSSource(
                            context.getApplicationContext()); // 3
                }
            }
        }
        // As this singleton can be called even before gear is set, refreshing it
        singletonInstance.setGear(HSHelpStack.getInstance(context).getGear());
        return singletonInstance;
    }

    private static String getDeviceInformation(Context activity) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n\n");
        builder.append("========");
        builder.append("\nDevice brand: ");
        builder.append(Build.MODEL);
        builder.append("\nAndroid version: ");
        builder.append(Build.VERSION.SDK_INT);
        builder.append("\nApp package: ");
        try {
            builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).packageName);
        } catch (NameNotFoundException e) {
            builder.append("NA");
        }
        builder.append("\nApp version: ");
        try {
            builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode);
        } catch (NameNotFoundException e) {
            builder.append("NA");
        }

        return builder.toString();
    }

    public static void throwError(ErrorListener errorListener, String error) {
        VolleyError volleyError = new VolleyError(error);
        printErrorDescription(null, volleyError);
        errorListener.onErrorResponse(volleyError);
    }

    private static void printErrorDescription(String methodName, VolleyError error) {
        if (methodName == null) {
            Log.e(HSHelpStack.LOG_TAG, "Error occurred in HelpStack");
        } else {
            Log.e(HSHelpStack.LOG_TAG, "Error occurred when executing " + methodName);
        }

        Log.e(HSHelpStack.LOG_TAG, error.toString());
        if (error.getMessage() != null) {
            Log.e(HSHelpStack.LOG_TAG, error.getMessage());
        }

        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                Log.e(HSHelpStack.LOG_TAG, new String(error.networkResponse.data, "utf-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        error.printStackTrace();
    }

    public void requestKBArticle(String cancelTag, HSKBItem section, OnFetchedArraySuccessListener success, ErrorListener errorListener) {

        if (gear.haveImplementedKBFetching()) {
            gear.fetchKBArticle(cancelTag, section, mRequestQueue, new SuccessWrapper(success) {

                @Override
                public void onSuccess(Object[] successObject) {
                    assert successObject != null : "It seems requestKBArticle was not implemented in gear";

                    // Do your work here, may be caching, data validation etc.
                    super.onSuccess(successObject);
                }
            }, new ErrorWrapper("Fetching KB articles", errorListener));
        } else {
            try {
                HSArticleReader reader = new HSArticleReader(gear.getLocalArticleResourceId());
                success.onSuccess(reader.readArticlesFromResource(mContext));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                throwError(errorListener, "Unable to parse local article XML");
            } catch (IOException e) {
                e.printStackTrace();
                throwError(errorListener, "Unable to read local article XML");
            }
        }
    }

    public void requestAllTickets(OnFetchedArraySuccessListener success, ErrorListener error) {
        if (cachedTicket == null) {
            success.onSuccess(new HSTicket[0]);
        } else {
            success.onSuccess(cachedTicket.getTickets());
        }
    }

    public void checkForUserDetailsValidity(String cancelTag, String firstName, String lastName, String email, OnFetchedSuccessListener success, ErrorListener errorListener) {
        gear.registerNewUser(cancelTag, firstName, lastName, email, mRequestQueue, success, new ErrorWrapper("Registering New User", errorListener));
    }

    public void createNewTicket(String cancelTag, HSUser user, String subject, String message, HSAttachment[] attachment, OnNewTicketFetchedSuccessListener successListener, ErrorListener errorListener) {

        HSUploadAttachment[] upload_attachments = convertAttachmentArrayToUploadAttachment(attachment);
        message = message + getDeviceInformation(mContext);

        if (gear.canUploadMessageAsHtmlString()) {
            message = Html.toHtml(new SpannableString(message));
        }

        gear.createNewTicket(cancelTag, user, subject, message, upload_attachments, mRequestQueue, new NewTicketSuccessWrapper(successListener) {

            @Override
            public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {

                // Save ticket and user details in cache
                // Save properties also later.
                doSaveNewTicketPropertiesForGearInCache(ticket);
                doSaveNewUserPropertiesForGearInCache(udpatedUserDetail);
                super.onSuccess(udpatedUserDetail, ticket);
            }
        }, new ErrorWrapper("Creating New Ticket", errorListener));

    }

    public void requestAllUpdatesOnTicket(String cancelTag, HSTicket ticket, OnFetchedArraySuccessListener success, ErrorListener errorListener) {
        gear.fetchAllUpdateOnTicket(cancelTag, ticket, cachedUser.getUser(), mRequestQueue, success, new ErrorWrapper("Fetching updates on Ticket", errorListener));
    }

    public void addReplyOnATicket(String cancelTag, String message, HSAttachment[] attachments, HSTicket ticket, OnFetchedSuccessListener success, ErrorListener errorListener) {

        if (gear.canUploadMessageAsHtmlString()) {
            message = Html.toHtml(new SpannableString(message));
        }

        gear.addReplyOnATicket(cancelTag, message, convertAttachmentArrayToUploadAttachment(attachments), ticket, getUser(), mRequestQueue, new OnFetchedSuccessListenerWrapper(success, message, attachments) {

            @Override
            public void onSuccess(Object successObject) {

                if (gear.canIgnoreTicketUpdateInformationAfterAddingReply()) {
                    HSTicketUpdate update = HSTicketUpdate.createUpdateByUser(null, null, this.message, Calendar.getInstance().getTime(), this.attachments);
                    super.onSuccess(update);
                } else {
                    super.onSuccess(successObject);
                }
            }
        }, new ErrorWrapper("Adding reply to a ticket", errorListener));
    }

    public HSGear getGear() {
        return gear;
    }

    private void setGear(HSGear gear) {
        this.gear = gear;
    }

    public boolean isNewUser() {
        return cachedUser.getUser() == null;
    }

    public void refreshUser() {
        doReadUserFromCache();
    }

    public HSUser getUser() {
        return cachedUser.getUser();
    }

    public String getDraftSubject() {
        if (draftObject != null) {
            return draftObject.getSubject();
        }
        return null;
    }

    public String getDraftMessage() {
        if (draftObject != null) {
            return draftObject.getMessage();
        }
        return null;
    }

    public HSUser getDraftUser() {
        if (draftObject != null) {
            return draftObject.getDraftUser();
        }
        return null;
    }

    public HSAttachment[] getDraftAttachments() {
        if (draftObject != null) {
            return draftObject.getAttachments();
        }
        return null;
    }

    public String getDraftReplyMessage() {
        if (draftObject != null) {
            return draftObject.getDraftReplyMessage();
        }
        return null;
    }

    public HSAttachment[] getDraftReplyAttachments() {
        if (draftObject != null) {
            return draftObject.getDraftReplyAttachments();
        }
        return null;
    }

    public void saveTicketDetailsInDraft(String subject, String message, HSAttachment[] attachmentsArray) {
        doSaveTicketDraftForGearInCache(subject, message, attachmentsArray);
    }

    public void saveUserDetailsInDraft(HSUser user) {
        doSaveUserDraftForGearInCache(user);
    }

    public void saveReplyDetailsInDraft(String message, HSAttachment[] attachmentsArray) {
        doSaveReplyDraftForGearInCache(message, attachmentsArray);
    }

    public boolean haveImplementedTicketFetching() {
        return gear.haveImplementedTicketFetching();
    }

    public String getSupportEmailAddress() {
        return gear.getCompanySupportEmailAddress();
    }

    /***
     * Depending on the setting set on gear, it launches new ticket activity.
     * <p>
     * if email : launches email [Done]
     * else:
     * if user logged in : launches user details [Done]
     * else: launches new ticket [Done]
     *
     * @param fragment
     * @param requestCode
     */
    public void launchCreateNewTicketScreen(HSFragmentParent fragment, int requestCode) {

        if (haveImplementedTicketFetching()) {
            if (isNewUser()) {
                HSActivityManager.startNewIssueActivity(fragment, null, requestCode);
            } else {
                HSActivityManager.startNewIssueActivity(fragment, getUser(), requestCode);
            }
        } else {
            launchEmailAppWithEmailAddress(fragment.getActivity());
        }
    }

    /////////////////////////////////////////////////
    ////////     Utility Functions  /////////////////
    /////////////////////////////////////////////////

    public void launchEmailAppWithEmailAddress(Activity activity) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getSupportEmailAddress()});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getDeviceInformation(activity));

        activity.startActivity(Intent.createChooser(emailIntent, "Email"));
    }

    public void cancelOperation(String cancelTag) {
        mRequestQueue.cancelAll(cancelTag);
    }

    public void refreshFieldsFromCache() {
        // read the ticket data from cache and maintain here
        doReadTicketsFromCache();
        doReadUserFromCache();
        doReadDraftFromCache();
    }

    /**
     * Opens a file and read its content. Return null if any error occured or file not found
     *
     * @param file
     * @return
     */
    private String readJsonFromFile(File file) {

        if (!file.exists()) {
            return null;
        }

        String json = null;
        FileInputStream inputStream;

        try {
            StringBuilder datax = new StringBuilder();
            inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(isr);

            String readString = bufferReader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = bufferReader.readLine();
            }

            isr.close();

            json = datax.toString();
            return json;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeJsonIntoFile(File file, String json) {
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doSaveNewTicketPropertiesForGearInCache(HSTicket ticket) {
        cachedTicket.addTicketAtStart(ticket);

        Gson gson = new Gson();
        String ticketsgson = gson.toJson(cachedTicket);
        File ticketFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_FILE_NAME);

        writeJsonIntoFile(ticketFile, ticketsgson);
    }

    protected void doSaveNewUserPropertiesForGearInCache(HSUser user) {
        cachedUser.setUser(user);

        Gson gson = new Gson();
        String userjson = gson.toJson(cachedUser);
        File userFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_USER_DATA);

        writeJsonIntoFile(userFile, userjson);
    }

    protected void doReadTicketsFromCache() {
        File ticketFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_FILE_NAME);
        String json = readJsonFromFile(ticketFile);

        if (json == null) {
            cachedTicket = new HSCachedTicket();
        } else {
            Gson gson = new Gson();
            cachedTicket = gson.fromJson(json, HSCachedTicket.class);
        }
    }

    protected void doReadUserFromCache() {
        File userFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_USER_DATA);
        String json = readJsonFromFile(userFile);

        if (json == null) {
            cachedUser = new HSCachedUser();
        } else {
            Gson gson = new Gson();
            cachedUser = gson.fromJson(json, HSCachedUser.class);
        }
    }

    protected void doReadDraftFromCache() {
        File draftFile = new File(getProjectDirectory(), HELPSTACK_DRAFT);
        String json = readJsonFromFile(draftFile);

        if (json == null) {
            draftObject = new HSDraft();
        } else {
            Gson gson = new Gson();
            draftObject = gson.fromJson(json, HSDraft.class);
        }
    }

    protected void doSaveTicketDraftForGearInCache(String subject, String message, HSAttachment[] attachmentsArray) {
        draftObject.setDraftSubject(subject);
        draftObject.setDraftMessage(message);
        draftObject.setDraftAttachments(attachmentsArray);

        writeDraftIntoFile();
    }

    protected void doSaveUserDraftForGearInCache(HSUser user) {
        draftObject.setDraftUser(user);
        writeDraftIntoFile();
    }

    protected void doSaveReplyDraftForGearInCache(String message, HSAttachment[] attachmentsArray) {
        draftObject.setDraftReplyMessage(message);
        draftObject.setDraftReplyAttachments(attachmentsArray);

        writeDraftIntoFile();
    }

    private void writeDraftIntoFile() {
        Gson gson = new Gson();
        String draftJson = gson.toJson(draftObject);
        File draftFile = new File(getProjectDirectory(), HELPSTACK_DRAFT);

        writeJsonIntoFile(draftFile, draftJson);
    }

    protected File getProjectDirectory() {
        File projDir = new File(mContext.getFilesDir(), HELPSTACK_DIRECTORY);
        if (!projDir.exists())
            projDir.mkdirs();

        return projDir;
    }

    public void clearTicketDraft() {
        saveTicketDetailsInDraft("", "", null);
    }

    public void clearReplyDraft() {
        saveReplyDetailsInDraft("", null);
    }

    protected HSUploadAttachment[] convertAttachmentArrayToUploadAttachment(HSAttachment[] attachment) {

        HSUploadAttachment[] upload_attachments = new HSUploadAttachment[0];

        if (attachment != null && attachment.length > 0) {
            int attachmentCount = gear.getNumberOfAttachmentGearCanHandle();
            assert attachmentCount >= attachment.length : "Gear cannot handle more than " + attachmentCount + " attachments";
            upload_attachments = new HSUploadAttachment[attachment.length];
            for (int i = 0; i < upload_attachments.length; i++) {
                upload_attachments[i] = new HSUploadAttachment(mContext, attachment[i]);
            }
        }

        return upload_attachments;
    }

    public void deleteAllFiles() {
        try {
            File dir = getProjectDirectory();
            deleteRecursive(dir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory != null) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        }
    }

    private class NewTicketSuccessWrapper implements OnNewTicketFetchedSuccessListener {

        private OnNewTicketFetchedSuccessListener lastListener;

        public NewTicketSuccessWrapper(OnNewTicketFetchedSuccessListener lastListener) {
            this.lastListener = lastListener;
        }

        @Override
        public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {
            if (lastListener != null)
                lastListener.onSuccess(udpatedUserDetail, ticket);
        }
    }

    private class SuccessWrapper implements OnFetchedArraySuccessListener {

        private OnFetchedArraySuccessListener lastListener;

        public SuccessWrapper(OnFetchedArraySuccessListener lastListener) {
            this.lastListener = lastListener;
        }

        @Override
        public void onSuccess(Object[] successObject) {
            if (lastListener != null)
                lastListener.onSuccess(successObject);
        }
    }

    private class OnFetchedSuccessListenerWrapper implements OnFetchedSuccessListener {

        protected String message;
        protected HSAttachment[] attachments;
        private OnFetchedSuccessListener listener;

        private OnFetchedSuccessListenerWrapper(OnFetchedSuccessListener listener, String message, HSAttachment[] attachments) {
            this.listener = listener;
            this.message = message;
            this.attachments = attachments;
        }

        @Override
        public void onSuccess(Object successObject) {
            if (this.listener != null) {
                this.listener.onSuccess(successObject);
            }
        }
    }

    private class ErrorWrapper implements ErrorListener {

        private ErrorListener errorListener;
        private String methodName;

        public ErrorWrapper(String methodName, ErrorListener errorListener) {
            this.errorListener = errorListener;
            this.methodName = methodName;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            printErrorDescription(methodName, error);
            this.errorListener.onErrorResponse(error);
        }
    }
}
