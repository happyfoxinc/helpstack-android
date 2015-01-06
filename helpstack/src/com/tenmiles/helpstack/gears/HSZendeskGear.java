//  HSZendeskGear
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
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tenmiles.helpstack.logic.HSGear;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.logic.OnFetchedSuccessListener;
import com.tenmiles.helpstack.logic.OnNewTicketFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSAttachment;
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
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class HSZendeskGear extends HSGear {

    private String instanceUrl;
    private String staff_email_address;
    private String api_token;
    private String section_id;

    public HSZendeskGear(String instanceUrl, String staff_email_address, String api_token) {
        if (!instanceUrl.endsWith("/")) {
            instanceUrl = instanceUrl.concat("/");
        }

        this.instanceUrl = instanceUrl;
        this.staff_email_address = staff_email_address;
        this.api_token = api_token;
    }


    @Override
    public void fetchKBArticle(String cancelTag, HSKBItem section, RequestQueue queue,
                               OnFetchedArraySuccessListener successListener, ErrorListener errorListener) {
        if (section == null) {
            // This is first request of sections

            if (this.section_id == null) {
                // Fetch all sections
                String url = getApiUrl().concat("help_center/sections.json");

                ZendeskJsonObjectRequest request = new ZendeskJsonObjectRequest(cancelTag, url,
                        new ZendeskArrayBaseListener<JSONObject>(successListener, errorListener) {

                            @Override
                            public void onResponse(JSONObject sectionsArray) {
                                HSKBItem[] array;
                                try {
                                    array = retrieveSectionsFromData(sectionsArray);
                                    successListener.onSuccess(array);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    errorListener.onErrorResponse(new VolleyError("Parsing failed when getting sections from data"));
                                }
                            }
                        }, errorListener);

                request.setTag(cancelTag);
                request.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                        ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

                queue.add(request);
                queue.start();
            }
            else {
                showArticlesInSection(cancelTag,this.section_id,  queue, successListener, errorListener);
            }
        }
        else {
            showArticlesInSection(cancelTag, section.getId(), queue, successListener, errorListener);
        }
    }

    @Override
    public void createNewTicket(String cancelTag, HSUser user, String message, String body, HSUploadAttachment[] attachments,  RequestQueue queue,
                                OnNewTicketFetchedSuccessListener successListener,
                                ErrorListener errorListener) {

        if (attachments != null && attachments.length > 0) {
        	createNewTicketWithAttachment(cancelTag, user, message, body, attachments, queue, successListener, errorListener);
        }
        else {
            createTicket(cancelTag, user, message, body, null, queue, successListener, errorListener);
        }
    }

    @Override
    public void fetchAllUpdateOnTicket(String cancelTag, final HSTicket ticket, HSUser user, RequestQueue queue,
                                       OnFetchedArraySuccessListener successListener, ErrorListener errorListener) {

        ZendeskJsonObjectRequest fetchRequest = new ZendeskJsonObjectRequest(cancelTag,
                getApiUrl().concat("tickets/").concat(ticket.getTicketId()).concat("/audits.json?include=users"),
                new ZendeskArrayBaseListener<JSONObject>(successListener, errorListener) {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray auditsArray = response.getJSONArray("audits");
                            JSONArray usersArray = response.getJSONArray("users");

                            ArrayList<HSTicketUpdate> ticketUpdates = new ArrayList<HSTicketUpdate>();

                            int auditsArrayLength = auditsArray.length();
                            for (int i = 0; i < auditsArrayLength; i++) {
                                JSONObject updateObject = auditsArray.getJSONObject(i);
                                HSTicketUpdate ticketUpdate = retrieveTicketUpdate(updateObject, usersArray);
                                if (ticketUpdate != null) {
                                    ticketUpdates.add(ticketUpdate);
                                }
                            }

                            HSTicketUpdate[] array = new HSTicketUpdate[0];
                            array = ticketUpdates.toArray(array);
                            successListener.onSuccess(array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorListener.onErrorResponse(new VolleyError("Parsing failed when fetching all update for a ticket"));
                        }
                    }
                }, errorListener);

        fetchRequest.addCredential(staff_email_address, api_token);
        fetchRequest.setTag(cancelTag);
        fetchRequest.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

        queue.add(fetchRequest);
        queue.start();
    }

    @Override
    public void addReplyOnATicket(final String cancelTag, final String message, final HSUploadAttachment[] attachments,  final HSTicket ticket, final HSUser user,
                                  RequestQueue queue, final OnFetchedSuccessListener successListener, ErrorListener errorListener) {

        if (attachments != null && attachments.length > 0) {
        	addReplyToTicketWithAttachment(cancelTag, ticket, user, message, attachments, queue, successListener, errorListener);
        }
        else {
            addReplyToTicket(cancelTag, ticket, user, message, null, null, queue, successListener, errorListener);
        }
    }



    public void setSectionId(String section_id) {
        this.section_id = section_id;
    }

    public String getApiUrl() {
        return this.instanceUrl.concat("api/v2/");
    }

    private void createNewTicketWithAttachment(final String cancelTag, final HSUser user, final String message, final String body, HSUploadAttachment[] attachments,  final RequestQueue queue,
                                               final OnNewTicketFetchedSuccessListener successListener, final ErrorListener errorListener)  {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(getApiUrl());
        builder.appendEncodedPath("uploads.json");

        HSUploadAttachment attachmentObject = attachments[0]; // It is been handled in constructor, so hard coding here
        String attachmentFileName = getAttachmentFileName(attachmentObject);
        builder.appendQueryParameter("filename", attachmentFileName);

        String attachmentUrl = builder.build().toString();

        ZendeskObjectRequest attachmentRequest = new ZendeskObjectRequest(cancelTag, attachmentUrl, attachmentObject, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String attachmentToken = jsonObject.getJSONObject("upload").getString("token");
                    String[] attachmentTokenList = new String[1]; // It is been handled in constructor, so hard coding here
                    attachmentTokenList[0] = attachmentToken;
                    createTicket(cancelTag, user, message, body, attachmentTokenList, queue, successListener, errorListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parsing failed when creating new ticket in Zendesk"));
                }
            }
        }, errorListener);


        attachmentRequest.addCredential(staff_email_address, api_token);
        attachmentRequest.setTag(cancelTag);
        attachmentRequest.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

        queue.add(attachmentRequest);
        queue.start();
    }

    private void createTicket(String cancelTag, final HSUser user, String message, String body, String[] attachmentToken, RequestQueue queue, final OnNewTicketFetchedSuccessListener successListener, final Response.ErrorListener errorListener) {
        JSONObject ticketJson = null;
        try {
            ticketJson = retrieveTicketProperties(user, body, attachmentToken, message);
        } catch (JSONException e) {
            e.printStackTrace();
            errorListener.onErrorResponse(new VolleyError("Error when trying to retrieve ticket properties"));
        }

        ZendeskJsonObjectRequest request = new ZendeskJsonObjectRequest(cancelTag, getApiUrl().concat("tickets.json"), ticketJson, new CreateNewTicketSuccessListener(user, successListener, errorListener) {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject responseTicket = response.getJSONObject("ticket");
                    HSTicket ticket = HSTicket.createATicket(responseTicket.getString("id"), responseTicket.getString("subject"));
                    HSUser user = HSUser.appendCredentialOnUserDetail(this.user, responseTicket.getString("submitter_id"), null);
                    successListener.onSuccess(user, ticket);
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parsing failed when creating a ticket"));
                }
            }
        }, errorListener);

        request.addCredential(staff_email_address, api_token);
        request.setTag(cancelTag);
        request.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

        queue.add(request);
        queue.start();
    }

    private void addReplyToTicketWithAttachment(final String cancelTag, final HSTicket ticket, final HSUser user, final String message, HSUploadAttachment[] attachments,  final RequestQueue queue,
                                                final OnFetchedSuccessListener successListener, final ErrorListener errorListener) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(getApiUrl());
        builder.appendEncodedPath("uploads.json");

        final HSUploadAttachment attachmentObject = attachments[0]; // It is been specified in constructor, so hard-coding the value. Can be changed later
        String attachmentFileName = getAttachmentFileName(attachmentObject);
        builder.appendQueryParameter("filename", attachmentFileName);

        String attachmentUrl = builder.build().toString();

        ZendeskObjectRequest attachmentRequest = new ZendeskObjectRequest(cancelTag, attachmentUrl, attachmentObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String attachmentToken = jsonObject.getJSONObject("upload").getString("token");
                    String[] attachmentTokenList = new String[1]; // It is been specified in constructor, so hard-coding the value. Can be changed later
                    attachmentTokenList[0] = attachmentToken;


                    HSAttachment[] attachmentObjectList = new HSAttachment[1]; // It is been specified in constructor, so hard-coding the value. Can be changed later
                    attachmentObjectList[0] = attachmentObject.getAttachment();

                    addReplyToTicket(cancelTag, ticket, user, message, attachmentTokenList, attachmentObjectList, queue, successListener, errorListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parsing failed when reading attachment token from Zendesk"));
                }
            }
        }, errorListener);

        attachmentRequest.addCredential(staff_email_address, api_token);
        attachmentRequest.setTag(cancelTag);
        attachmentRequest.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

        queue.add(attachmentRequest);
        queue.start();
    }

    private String getAttachmentFileName(HSUploadAttachment attachmentObject) {
        return attachmentObject.getAttachment().getFileName() == null ? "picture":attachmentObject.getAttachment().getFileName();
    }

    private void addReplyToTicket(String cancelTag, HSTicket ticket, HSUser user, final String message, String[] attachmentToken, final HSAttachment[] attachmentObjectList, RequestQueue queue, final OnFetchedSuccessListener successListener, final Response.ErrorListener errorListener) {

        JSONObject ticketJson = null;
        try {
            ticketJson = retrieveRequestProperties(message, attachmentToken);
        } catch (JSONException e) {
            e.printStackTrace();
            errorListener.onErrorResponse(new VolleyError("Error when retrieving request properties"));
        }

        ZendeskJsonObjectRequest request = new ZendeskJsonObjectRequest(cancelTag, user.getEmail(), Request.Method.PUT,
                getApiUrl().concat("requests/").concat(ticket.getTicketId()).concat(".json"),
                ticketJson, new ZendeskBaseListener<JSONObject>(successListener, errorListener) {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    this.errorListener.onErrorResponse(new VolleyError());
                }
                else {
                    HSTicketUpdate update;
                    String updateId = null;
                    String userName = null;
                    Date update_time = null;
                    HSAttachment[] attachmentList = attachmentObjectList;

                    try {
                        JSONObject requestObject= response.getJSONObject("request");

                        if (!requestObject.isNull("requester_id")) {
                            userName = requestObject.getString("requester_id");
                        }

                        if (!requestObject.isNull("updated_at")) {
                            update_time = parseTime(requestObject.getString("updated_at"));
                        }

                        update = HSTicketUpdate.createUpdateByUser(updateId, userName, message, update_time, attachmentList);

                        successListener.onSuccess(update);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError("Parse error with response when adding reply on ticket"));
                    }
                }
            }
        }, errorListener);

        request.addCredential(user.getEmail(), api_token);
        request.setTag(cancelTag);
        request.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

        queue.add(request);
        queue.start();
    }

    private JSONObject retrieveTicketProperties(HSUser user, String body, String[] attachmentToken, String message) throws JSONException {
        JSONObject requester = new JSONObject();
        JSONObject comment = new JSONObject();
        JSONObject ticketProperties = new JSONObject();
        JSONObject ticket = new JSONObject();

        requester.put("name", user.getFullName());
        requester.put("email", user.getEmail());
        requester.put("verified", true);

        comment.put("body", body);
        if (attachmentToken != null) {
            JSONArray attachmentsArray = new JSONArray();
            for (int i = 0; i < attachmentToken.length; i++) {
            	attachmentsArray.put(attachmentToken[i]);
			}
            
            comment.put("uploads", attachmentsArray);
        }

        ticketProperties.put("requester", requester);
        ticketProperties.put("subject", message);
        ticketProperties.put("comment", comment);

        ticket.put("ticket", ticketProperties);

        return ticket;
    }

    private JSONObject retrieveRequestProperties(String body, String[] attachmentToken) throws JSONException {

        JSONObject comment = new JSONObject();
        JSONObject requestProperties = new JSONObject();
        JSONObject requestObject = new JSONObject();

        comment.put("body", body);
        if (attachmentToken != null) {
            JSONArray attachmentsArray = new JSONArray();
            for (int i = 0; i < attachmentToken.length; i++) {
            	attachmentsArray.put(attachmentToken[i]);
			}
            
            comment.put("uploads", attachmentsArray);
        }

        requestProperties.put("comment", comment);

        requestObject.put("request", requestProperties);

        return requestObject;
    }

    /**
     *
     * Note: Returns null if it is not pubic note.
     *
     *
     *
     * @param updateObject
     * @param usersArray
     * @return
     * @throws JSONException
     */
    private HSTicketUpdate retrieveTicketUpdate(JSONObject updateObject, JSONArray usersArray) throws JSONException {
        int authorId = -1;
        String content = null;
        String updateId = null;
        String from = null;
        boolean publicNote = true;
        Date update_time = null;
        boolean isUpdateTypeUserReply = false;
        HSAttachment[] attachments = null;

        JSONArray eventsArray = updateObject.getJSONArray("events");

        int eventsArrayLength = eventsArray.length();
        for (int i = 0; i < eventsArrayLength; i++) { //else Nothing
            JSONObject eventObject = eventsArray.getJSONObject(i);
            if (eventObject.getString("type").equals("Comment")) {

                publicNote = eventObject.getBoolean("public");
                if (!publicNote) {
                    return null;
                }

                content = eventObject.getString("body");

                if (!eventObject.isNull("author_id")) {
                    authorId = eventObject.getInt("author_id");
                }

                // authorId can be null here because of previous if loop. Make sure to handle it properly
                JSONObject author = searchForUser(authorId, usersArray);

                if (author != null) {
                	if (!author.isNull("name")) {
                        from = author.getString("name");
                    }
                	
                	String role = author.getString("role");
                    if (role.equals("end-user")) {
                        isUpdateTypeUserReply = true;
                    }
                }
                
                

                if (!updateObject.isNull("created_at")) {
                    update_time = parseTime(updateObject.getString("created_at"));
                }

                JSONArray attachmentObjects = eventObject.getJSONArray("attachments");
                if(attachmentObjects != null) {
                    int length = attachmentObjects.length();
                    ArrayList<HSAttachment> attachmentArray = new ArrayList<HSAttachment>();
                    for(int j = 0; j < length; j++) {
                        JSONObject attachmentData = attachmentObjects.getJSONObject(j);
                        String attachment_url = attachmentData.getString("mapped_content_url");
                        if (attachment_url.startsWith("/")) {
                            attachment_url = instanceUrl.concat(attachment_url.substring(1));
                        }
                        HSAttachment attachData = HSAttachment.createAttachment(attachment_url, attachmentData.getString("file_name"), attachmentData.getString("content_type"));
                        attachmentArray.add(attachData);
                    }
                    attachments = attachmentArray.toArray(new HSAttachment[length]);
                }
                
                
                break;
            }
        }

        if (isUpdateTypeUserReply) {
            return HSTicketUpdate.createUpdateByUser(updateId, from, content, update_time, attachments);
        }
        else {
            return HSTicketUpdate.createUpdateByStaff(updateId, from, content, update_time, attachments);
        }
    }

    private HSKBItem[] retrieveArticlesFromSectionArray(JSONArray articleArray) throws JSONException {
        ArrayList<HSKBItem> kbArticleArray = new ArrayList<HSKBItem>();

        for (int j = 0; j < articleArray.length(); j++) {
            JSONObject arrayObject = articleArray.getJSONObject(j);
            HSKBItem item = HSKBItem.createForArticle(arrayObject.getString("id"), arrayObject.getString("name").trim(), arrayObject.getString("body"));
            kbArticleArray.add(item);
        }

        HSKBItem[] array = new HSKBItem[0];
        array = kbArticleArray.toArray(array);
        return array;
    }

    private HSKBItem[] retrieveSectionsFromData(JSONObject sectionsArray) throws JSONException {
        JSONArray allSectionsArray = sectionsArray.getJSONArray("sections");
        ArrayList<HSKBItem> kbSectionArray = new ArrayList<HSKBItem>();

        int count  = allSectionsArray.length();
        for (int i = 0; i < count; i++) {
            JSONObject sectionObject = allSectionsArray.getJSONObject(i);
            if (sectionObject != null) {
                HSKBItem item = HSKBItem.createForSection(sectionObject.getString("id"), sectionObject.getString("name"));
                kbSectionArray.add(item);
            }
        }

        HSKBItem[] array = new HSKBItem[0];
        array = kbSectionArray.toArray(array);
        return array;
    }

    private JSONObject searchForUser(int userId, JSONArray usersArray) throws JSONException {
        JSONObject usersObject = null;
        int usersArrayLength = usersArray.length();

        for (int i = 0; i < usersArrayLength; i++) {
            usersObject = usersArray.getJSONObject(i);
            if (usersObject.getInt("id") == userId) {
                return usersObject;
            }
        }

        return null;
    }

    private void showArticlesInSection(String cancelTag, String section_id,  RequestQueue queue, final OnFetchedArraySuccessListener successListener, final ErrorListener errorListener) {
        // Fetch individual section
        String url = getApiUrl().concat("help_center/sections/").concat(section_id).concat("/articles.json");

        ZendeskJsonObjectRequest request = new ZendeskJsonObjectRequest(cancelTag, url, null, new ZendeskArrayBaseListener<JSONObject>(successListener, errorListener) {

            @Override
            public void onResponse(JSONObject sectionsObject) {
                try {
                    HSKBItem[] array = retrieveArticlesFromSectionArray(sectionsObject.getJSONArray("articles"));
                    successListener.onSuccess(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(new VolleyError("Parse error when getting articles in section"));
                }
            }
        }, errorListener);

        request.addCredential(staff_email_address, api_token);
        request.setTag(cancelTag);
        request.setRetryPolicy(new DefaultRetryPolicy(ZendeskJsonObjectRequest.TIMEOUT_MS,
                ZendeskJsonObjectRequest.MAX_RETRIES, ZendeskJsonObjectRequest.BACKOFF_MULT));

        queue.add(request);
        queue.start();
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

    private abstract class ZendeskArrayBaseListener<T> implements Listener<T> {

        protected OnFetchedArraySuccessListener successListener;
        protected ErrorListener errorListener;

        public ZendeskArrayBaseListener(OnFetchedArraySuccessListener successListener,
                                        ErrorListener errorListener) {
            this.successListener = successListener;
            this.errorListener = errorListener;
        }

    }

    private abstract class ZendeskBaseListener<T> implements Listener<T> {

        protected OnFetchedSuccessListener successListener;
        protected Response.ErrorListener errorListener;

        public ZendeskBaseListener(OnFetchedSuccessListener successListener,
                                   ErrorListener errorListener) {
            this.successListener = successListener;
            this.errorListener = errorListener;
        }

    }

    private class ZendeskJsonObjectRequest extends JsonObjectRequest {

        protected static final int TIMEOUT_MS = 0;

        /** Default number of retries for image requests */
        protected static final int MAX_RETRIES = 0;

        /** Default backoff multiplier for image requests */
        protected static final float BACKOFF_MULT = 1f;

        HashMap<String, String> headers = new HashMap<String, String>();

        public ZendeskJsonObjectRequest(String cancelTag, int method, String url, JSONObject jsonRequest, Listener<org.json.JSONObject> listener, ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public ZendeskJsonObjectRequest(String cancelTag, String email_address, int method, String url, JSONObject jsonRequest, Listener<org.json.JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public ZendeskJsonObjectRequest(String cancelTag, String url, JSONObject ticketJson, Listener<JSONObject> listener, ErrorListener errorListener) {
            super(url, ticketJson, listener, errorListener);
        }

        public ZendeskJsonObjectRequest(String cancelTag, String url, Listener<JSONObject> listener, ErrorListener errorListener) {
            super(url, null, listener, errorListener);
        }

        public void addCredential(String name, String api_token) {
            String credentials = name.concat("/token:").concat(api_token);
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            headers.put("Authorization", "Basic ".concat(base64EncodedCredentials));
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers;
        }

    }

    private class ZendeskObjectRequest extends Request<JSONObject> {

        private byte[] content;
        private Listener<JSONObject> mListener;
        HashMap<String, String> headers = new HashMap<String, String>();

        public ZendeskObjectRequest(String cancelTag, String attachmentUrl, HSUploadAttachment attachmentObject, Listener<JSONObject> listener, ErrorListener errorListener) {
            super(Method.POST, attachmentUrl, errorListener);

            mListener = listener;

            InputStream inputStream = null;
            try {
                inputStream = attachmentObject.generateInputStreamToUpload();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // convert input stream to byte array and send that byte array in getBody

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            try {
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.content = output.toByteArray();
        }

        private void addCredential(String name, String api_token) {
            String credentials = name.concat("/token:").concat(api_token);
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            headers.put("Authorization", "Basic ".concat(base64EncodedCredentials));
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers;
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            String jsonString = null;
            try {
                jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

            return Response.success(jsonObject, entry);
        }

        @Override
        protected void deliverResponse(JSONObject response) {
            mListener.onResponse(response);
        }

        @Override
        public String getBodyContentType() {
            return "application/binary";
        }

        @Override
        public byte[] getBody() {
            return this.content;
        }
    }

    private abstract class CreateNewTicketSuccessListener implements Listener<JSONObject>
    {

        protected HSUser user;
        protected OnNewTicketFetchedSuccessListener successListener;
        protected ErrorListener errorListener;

        public CreateNewTicketSuccessListener(HSUser user, OnNewTicketFetchedSuccessListener successListener,
                                              ErrorListener errorListener) {
            this.user = user;
            this.successListener = successListener;
            this.errorListener = errorListener;
        }
    }
}
