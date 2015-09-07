//  HSDeskGear
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

package com.tenmiles.helpstack.gears;


import android.net.Uri;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tenmiles.helpstack.logic.HSGear;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.logic.OnFetchedSuccessListener;
import com.tenmiles.helpstack.logic.OnNewTicketFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;
import com.tenmiles.helpstack.model.HSUploadAttachment;
import com.tenmiles.helpstack.model.HSUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.tenmiles.helpstack.model.HSUser.createNewUserWithDetails;

public class HSDeskGear extends HSGear {

    private String instanceUrl;
    private String to_help_email;
    private String staff_login_email;
    private String staff_login_password;

    public HSDeskGear(String instanceUrl, String to_help_email, String staff_login_email, String staff_login_password) {
        if (!instanceUrl.endsWith("/")) {
            instanceUrl = instanceUrl.concat("/");
        }

        this.instanceUrl = instanceUrl;
        this.to_help_email = to_help_email;
        this.staff_login_email = staff_login_email;
        this.staff_login_password = staff_login_password;
    }

    @Override
    public void fetchKBArticle(String cancelTag, HSKBItem section, RequestQueue queue,
                               OnFetchedArraySuccessListener successListener, Response.ErrorListener errorListener) {

        if (section == null) {
            String url = getApiUrl().concat("topics");

            DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, url, new DeskArrayBaseListener<JSONObject>(successListener, errorListener) {

                @Override
                public void onResponse(JSONObject sectionsArray) {
                    try {
                        JSONArray allSectionsArray = sectionsArray.getJSONObject("_embedded").getJSONArray("entries");
                        HSKBItem[] array = readSectionsFromEntries(allSectionsArray);
                        successListener.onSuccess(array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError("Parsing failed when getting sections from data"));
                    }
                }
            }, errorListener);

            addRequestAndStartQueue(queue, request);

        }
        else {
            // Fetch articles under that section.
            DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, section.getId(), null, new DeskArrayBaseListener<JSONObject>(successListener, errorListener) {

                @Override
                public void onResponse(JSONObject sectionsObject) {
                    HSKBItem[] array;
                    try {
                        array = retrieveArticlesFromSection(sectionsObject);
                        successListener.onSuccess(array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError("Error when fetching articles in section"));
                    }
                }
            }, errorListener);

            addRequestAndStartQueue(queue, request);
        }
    }

    @Override
    public void registerNewUser(final String cancelTag, final String firstName, final String lastname, final String emailAddress, final RequestQueue queue,
                                OnFetchedSuccessListener successListener, Response.ErrorListener errorListener) {

        HSUser user = createNewUserWithDetails(firstName, lastname, emailAddress);

        DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, getApiUrl().concat("customers/search?email=").concat(user.getEmail()),
                new CreateNewUserSuccessListener(user, successListener, errorListener) {

            @Override
            public void onResponse(JSONObject responseObject) {

                int totalEntries = 0;
                try {
                    totalEntries = responseObject.getInt("total_entries");
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parsing error when getting Total Entries"));
                }

                if (totalEntries >= 1) {
                    try {
                        JSONObject validUserDetail = null;

                        JSONArray couldBeValidUsersArray = responseObject.getJSONObject("_embedded").getJSONArray("entries");
                        int couldBeValidUsersArrayLength = couldBeValidUsersArray.length();

                        for (int i = 0; i < couldBeValidUsersArrayLength; i++) {
                            JSONObject couldBeValidUserDetail = couldBeValidUsersArray.getJSONObject(i);
                            JSONArray emailsArray = couldBeValidUserDetail.getJSONArray("emails");

                            int emailsArrayLength = emailsArray.length();
                            for (int j = 0; j < emailsArrayLength; j++) {
                                JSONObject emailDetails = couldBeValidUsersArray.getJSONObject(j);

                                if (emailDetails.getString("value").equals(user.getEmail())) {
                                    validUserDetail = couldBeValidUserDetail;
                                    break;
                                }
                            }

                            if (validUserDetail != null) {
                                break;
                            }
                        }

                        if (validUserDetail != null) {
                            String firstName = validUserDetail.getString("first_name");
                            String lastName = validUserDetail.getString("last_name");
                            String email = validUserDetail.getJSONArray("emails").getJSONObject(0).getString("value");
                            String userLink = validUserDetail.getJSONObject("_links").getJSONObject("self").getString("href");

                            HSUser validUser = createNewUserWithDetails(firstName, lastName, email, userLink);
                            successListener.onSuccess(validUser);
                        }
                        else {
                            errorListener.onErrorResponse(new VolleyError("Error when getting details of users"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError("Error when getting single user from multiple users"));
                    }
                }
                else {
                    //not found in search.
                    //create a new user and send it as valid user.

                    JSONObject emailContents = new JSONObject();
                    JSONArray emails = new JSONArray();
                    final JSONObject params = new JSONObject();

                    try {
                        emailContents.put("type", "home");
                        emailContents.put("value", user.getEmail());
                        emails.put(emailContents);

                        params.put("first_name", user.getFirstName());
                        params.put("last_name", user.getLastName());
                        params.put("emails", emails);

                        DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, getApiUrl().concat("customers"), params, new CreateNewUserSuccessListener(user, successListener, errorListener) {

                            @Override
                            public void onResponse(JSONObject responseObject) {
                                String firstName;
                                String lastName;
                                String apiHref;
                                HSUser validatedUser = null;

                                JSONArray emailsArray;

                                try {
                                    firstName = responseObject.getString("first_name");
                                    lastName = responseObject.getString("last_name");
                                    apiHref = responseObject.getJSONObject("_links").getJSONObject("self").getString("href");
                                    emailsArray = responseObject.getJSONArray("emails");

                                    int eventsArrayLength = emailsArray.length();
                                    for (int i = 0; i < eventsArrayLength; i++) {
                                        JSONObject emailObject = emailsArray.getJSONObject(i);
                                        if (emailObject.getString("type").equals("home")) {
                                            validatedUser = createNewUserWithDetails(firstName, lastName, emailObject.getString("value"), apiHref);
                                        }
                                    }

                                    successListener.onSuccess(validatedUser);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    errorListener.onErrorResponse(new VolleyError("Error when creating new user with details"));
                                }
                            }
                        }, errorListener);

                        addRequestAndStartQueue(queue, request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError("Error when setting customer details as parameters"));
                    }
                }
            }
        }, errorListener);

        addRequestAndStartQueue(queue, request);
    }

    @Override
    public void createNewTicket(final String cancelTag, HSUser user, String message, String body, final HSUploadAttachment[] attachments, final RequestQueue queue,
                                OnNewTicketFetchedSuccessListener successListener, Response.ErrorListener errorListener) {

        JSONObject ticketJson = null;
        try {
            ticketJson = retrieveTicketProperties(user, body, message);
        } catch (JSONException e) {
            e.printStackTrace();
            errorListener.onErrorResponse(new VolleyError("Error when getting ticket properties"));
            return;
        }

        DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, getApiUrl().concat("cases"), ticketJson,
                new CreateNewTicketSuccessListener(user, successListener, errorListener) {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    String subject = response.getString("subject");
                    String caseIdHref = response.getJSONObject("_links").getJSONObject("self").getString("href");
                    String caseId = retrieveSectionId(caseIdHref);

                    final HSTicket ticket = HSTicket.createATicket(caseId, subject, caseIdHref);
                    // If there are attachment to be uploaded, upload attachment

                    if (attachments != null && attachments.length > 0) {
                        HSUploadAttachment attachmentObject = attachments[0]; // We are handling the number of attachments in constructor

                        uploadAttachmentToServer(cancelTag, caseIdHref, attachmentObject, queue, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                successListener.onSuccess(user, ticket);
                            }
                        }, errorListener);

                    }
                    else {
                        successListener.onSuccess(this.user, ticket);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parsing failed when creating a ticket"));
                }
            }
        }, errorListener);

        addRequestAndStartQueue(queue, request); // For ticket creation
    }

    @Override
    public void fetchAllUpdateOnTicket(final String cancelTag, final HSTicket ticket, final HSUser user, final RequestQueue queue,
                                       final OnFetchedArraySuccessListener successListener, Response.ErrorListener errorListener) {

        final String apiHref = getApiHref(ticket);

        String url = instanceUrl.concat(apiHref).concat("/message");
        String userId = user.getUserId();
        DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, url, new DeskArrayBaseListener<JSONObject>(successListener, errorListener) {

            @Override
            public void onResponse(JSONObject responseObject) {
                try {

                    String content = responseObject.getString("body");
                    String from = responseObject.getString("from");
                    final Date update_time = parseTime(responseObject.getString("updated_at"));

                    HSTicketUpdate originalMessage = HSTicketUpdate.createUpdateByUser(ticket.getTicketId(), from, content, update_time, null);

                    final ArrayList<HSTicketUpdate> ticketUpdates = new ArrayList<HSTicketUpdate>();
                    ticketUpdates.add(originalMessage);

                    String repliesUrl = instanceUrl.concat(apiHref).concat("/replies");

                    DeskJsonObjectRequest repliesRequest = new DeskJsonObjectRequest(cancelTag, repliesUrl, null, new DeskArrayBaseListener<JSONObject>(successListener, errorListener) {

                        @Override
                        public void onResponse(JSONObject repliesObject) {
                            String from = null;
                            String content = null;
                            boolean isUpdateTypeUserReply;
                            Date updated_time;

                            try {
                            	JSONArray entriesArray = repliesObject.getJSONObject("_embedded").getJSONArray("entries");

                                int eventsArrayLength = entriesArray.length();
                                for (int i = 0; i < eventsArrayLength; i++) {
                                    JSONObject replyObject = entriesArray.getJSONObject(i);

                                    if (!replyObject.isNull("from")) {
                                        from = replyObject.getString("from");
                                    }

                                    content = replyObject.getString("body");

                                    if (replyObject.getString("direction").equals("out")) {
                                        isUpdateTypeUserReply = false;
                                    } else {
                                        isUpdateTypeUserReply = true;
                                    }

                                    updated_time = parseTime(replyObject.getString("updated_at"));

                                    HSTicketUpdate replyForTicket;
                                    if (isUpdateTypeUserReply) {
                                        replyForTicket = HSTicketUpdate.createUpdateByUser(ticket.getTicketId(), from, content, updated_time, null);
                                    } else {
                                        replyForTicket = HSTicketUpdate.createUpdateByStaff(ticket.getTicketId(), from, content, updated_time, null);
                                    }

                                    ticketUpdates.add(replyForTicket);
                                }

                                HSTicketUpdate[] array = new HSTicketUpdate[0];
                                array = ticketUpdates.toArray(array);
                                successListener.onSuccess(array);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                errorListener.onErrorResponse(new VolleyError("Parsing failed when fetching all replies for a ticket"));
                            }

                        }
                    }, errorListener);

                    addRequestAndStartQueue(queue, repliesRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parsing failed when fetching all updates for a ticket"));
                }
            }
        }, errorListener);

        addRequestAndStartQueue(queue, request);
    }

    @Override
    public void addReplyOnATicket(final String cancelTag, final String message, final HSUploadAttachment[] attachments,  final HSTicket ticket, final HSUser user, final RequestQueue queue,
                                  final OnFetchedSuccessListener successListener, Response.ErrorListener errorListener) {

        JSONObject replyJson = null;
        try {
            replyJson = createUserReply(message, to_help_email);
        } catch (JSONException e) {
            e.printStackTrace();
            errorListener.onErrorResponse(new VolleyError("Error when creating user reply"));
        }

        DeskJsonObjectRequest request = new DeskJsonObjectRequest(cancelTag, instanceUrl.concat(getApiHref(ticket)).concat("/replies"), replyJson,
                new DeskBaseListener<JSONObject>(successListener, errorListener) {

                    @Override
                    public void onResponse(JSONObject responseObject) {
                        String content = null;
                        String userName = null;
                        Date update_time = null;

                        try {
                            if (!responseObject.isNull("from")) {
                                userName = responseObject.getString("from");
                            }

                            if (!responseObject.isNull("body")) {
                                content = responseObject.getString("body");
                            }

                            if (!responseObject.isNull("updated_at")) {
                                update_time = parseTime(responseObject.getString("updated_at"));
                            }

                            final HSTicketUpdate userReply = HSTicketUpdate.createUpdateByUser(ticket.getTicketId(), userName, content, update_time, null);
                            
                            if (attachments != null && attachments.length > 0) {
                                HSUploadAttachment attachmentObject = attachments[0]; // We are handling the number of attachments in constructor

                                uploadAttachmentToServer(cancelTag, ticket.getApiHref(), attachmentObject, queue, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                    	successListener.onSuccess(userReply);
                                    }
                                }, errorListener);

                            }
                            else {
                            	successListener.onSuccess(userReply);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorListener.onErrorResponse(new VolleyError("Error when parsing replies"));
                        }
                    }
                }, errorListener);

        addRequestAndStartQueue(queue, request);
    }

    private String getApiUrl() {
        return this.instanceUrl.concat("api/v2/");
    }

    private String getApiHref(HSTicket ticket) {
        String apiHref = ticket.getApiHref();
        if (apiHref.startsWith("/")) {
            apiHref = apiHref.substring(1);
        }
        return apiHref;
    }

    private void addRequestAndStartQueue(RequestQueue queue, DeskJsonObjectRequest request) {
        queue.add(request);
        queue.start();
    }
    
    private void uploadAttachmentToServer(String cancelTag,  String caseId, HSUploadAttachment attachmentObject, RequestQueue queue,
                                          Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) throws JSONException {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(instanceUrl);
        builder.appendEncodedPath(caseId.substring(1));
        builder.appendEncodedPath("attachments");

        String attachmentUrl = builder.build().toString();
        String attachmentFileName = attachmentObject.getAttachment().getFileName() == null ? "picture":attachmentObject.getAttachment().getFileName();
        String attachmentMimeType = attachmentObject.getAttachment().getMimeType();

        try {
            JSONObject attachmentPostObject = new JSONObject();

            InputStream input = attachmentObject.generateInputStreamToUpload();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            try {
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            attachmentPostObject.put("content", Base64.encodeToString(output.toByteArray(), Base64.DEFAULT));

            attachmentPostObject.put("content_type", attachmentMimeType);
            attachmentPostObject.put("file_name", attachmentFileName);

            DeskJsonObjectRequest attachmentRequest = new DeskJsonObjectRequest(cancelTag, attachmentUrl, attachmentPostObject,
                    successListener, errorListener);

            addRequestAndStartQueue(queue, attachmentRequest); // For Attachments
        } catch (FileNotFoundException e) {
            errorListener.onErrorResponse(new VolleyError("File not found"));
            e.printStackTrace();
        }
    }

    private String retrieveSectionId(String href) throws JSONException {
        // href will be of the form: /api/v2/topic/<section id>
        // <section id> will therefore be the text after the last /
        return href.substring(href.lastIndexOf("/")+1);
    }

    private HSKBItem[] retrieveArticlesFromSection(JSONObject sectionObject) throws JSONException {
        ArrayList<HSKBItem> kbArticleArray = new ArrayList<HSKBItem>();

        JSONArray articleArray = sectionObject.getJSONObject("_embedded").getJSONArray("entries");

        for (int j = 0; j < articleArray.length(); j++) {
            JSONObject arrayObject = articleArray.getJSONObject(j);
            if (arrayObject.getBoolean("in_support_center")) {
                HSKBItem item = HSKBItem.createForArticle(null, arrayObject.getString("subject").trim(), arrayObject.getString("body"));
                kbArticleArray.add(item);
            }
        }

        HSKBItem[] array = new HSKBItem[0];
        array = kbArticleArray.toArray(array);
        return array;
    }

    private HSKBItem[] readSectionsFromEntries(JSONArray sectionsArray) throws JSONException {
        ArrayList<HSKBItem> kbSectionArray = new ArrayList<HSKBItem>();

        int count  = sectionsArray.length();
        for (int i = 0; i < count; i++) {
            JSONObject sectionObject = sectionsArray.getJSONObject(i);
            if (sectionObject != null && sectionObject.getBoolean("in_support_center")) {
                String href = sectionObject.getJSONObject("_links").getJSONObject("articles").getString("href");
                href = instanceUrl.concat(href.substring(1));
                HSKBItem item = HSKBItem.createForSection(href, sectionObject.getString("name"));
                kbSectionArray.add(item);
            }
        }

        HSKBItem[] array = new HSKBItem[0];
        array = kbSectionArray.toArray(array);
        return array;
    }

    private JSONObject retrieveTicketProperties(HSUser user, String body, String message) throws JSONException {
        JSONObject messageFields = new JSONObject();
        JSONObject customerLinks = new JSONObject();
        JSONObject customerParameter = new JSONObject();
        JSONObject params = new JSONObject();

        messageFields.put("direction", "in");
        messageFields.put("from", user.getEmail());
        messageFields.put("to", to_help_email);
        messageFields.put("subject", message);
        messageFields.put("body", body);

        customerLinks.put("href", user.getApiHref());
        customerLinks.put("class", "customer");

        customerParameter.put("customer", customerLinks);

        params.put("type", "email");
        params.put("subject", message);
        params.put("_links", customerParameter);
        params.put("message", messageFields);

        return params;
    }

    private JSONObject createUserReply(String message, String email) throws JSONException {
        JSONObject userReply = new JSONObject();
        userReply.put("direction", "in");
        userReply.put("body", message);
        userReply.put("to", email);
        return  userReply;
    }

    protected static Date parseTime(String dateString) {
        Date givenTimeDate = null;
        try {
            givenTimeDate = parseUTCString(dateString,"yyyy-MM-dd'T'HH:mm:ss'Z'");
        } catch (ParseException e) {
            try {
                givenTimeDate = parseUTCString(dateString,"yyyy-MM-dd");
            } catch (ParseException e1) {
                try {
                    givenTimeDate = parseUTCString(dateString,"yyyy-MM-dd HH:mm:ss.SSSSZ");
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return givenTimeDate;
    }

    private static Date parseUTCString(String timeStr, String pattern) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(timeStr);
    }

    private abstract class DeskArrayBaseListener<T> implements Response.Listener<T> {
        protected OnFetchedArraySuccessListener successListener;
        protected Response.ErrorListener errorListener;

        public DeskArrayBaseListener(OnFetchedArraySuccessListener successListener, Response.ErrorListener errorListener) {
            this.successListener = successListener;
            this.errorListener = errorListener;
        }
    }

    private abstract class DeskBaseListener<T> implements Response.Listener<T> {
        protected OnFetchedSuccessListener successListener;
        protected Response.ErrorListener errorListener;

        public DeskBaseListener(OnFetchedSuccessListener successListener, Response.ErrorListener errorListener) {
            this.successListener = successListener;
            this.errorListener = errorListener;
        }
    }

    private class DeskJsonObjectRequest extends JsonObjectRequest {

        protected static final int TIMEOUT_MS = 10000;

        /** Default number of retries for image requests */
        protected static final int MAX_RETRIES = 2;

        /** Default backoff multiplier for image requests */
        protected static final float BACKOFF_MULT = 0f;

        HashMap<String, String> headers = new HashMap<String, String>();

        // Post
        public DeskJsonObjectRequest(String cancelTag, String url, JSONObject ticketJson, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, ticketJson, listener, errorListener);
            addRequestParameters(cancelTag);
        }

        // Get
        public DeskJsonObjectRequest(String cancelTag, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, null, listener, errorListener);
            addRequestParameters(cancelTag);
        }

        private void addRequestParameters(String cancelTag) {
            this.setTag(cancelTag);
            this.addCredential(staff_login_email, staff_login_password);
            this.setRetryPolicy(new DefaultRetryPolicy(DeskJsonObjectRequest.TIMEOUT_MS,
                    DeskJsonObjectRequest.MAX_RETRIES, DeskJsonObjectRequest.BACKOFF_MULT));
        }

        public void addCredential(String name, String password) {
            String credentials = name.concat(":").concat(password);
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            headers.put("Authorization", "Basic ".concat(base64EncodedCredentials));
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers;
        }
    }

    private abstract class CreateNewTicketSuccessListener implements Response.Listener<JSONObject> {
        protected HSUser user;
        protected OnNewTicketFetchedSuccessListener successListener;
        protected Response.ErrorListener errorListener;

        public CreateNewTicketSuccessListener(HSUser user, OnNewTicketFetchedSuccessListener successListener, Response.ErrorListener errorListener) {
            this.user = user;
            this.successListener = successListener;
            this.errorListener = errorListener;
        }
    }

    private abstract class CreateNewUserSuccessListener implements Response.Listener<JSONObject> {
        protected HSUser user;
        protected OnFetchedSuccessListener successListener;
        protected Response.ErrorListener errorListener;

        public CreateNewUserSuccessListener(HSUser user, OnFetchedSuccessListener successListener, Response.ErrorListener errorListener) {
            this.user = user;
            this.successListener = successListener;
            this.errorListener = errorListener;
        }
    }
}
