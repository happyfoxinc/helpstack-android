//  HSHappyfoxGear
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


import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class HSHappyfoxGear extends HSGear {
	private static final String TAG = HSHappyfoxGear.class.getSimpleName();

	private String instanceUrl;
	private String api_key;
	private String auth_code;
	private String priority_id;
	private String category_id;
	private String section_id;

    // This are cached here so server call can be minimized and improve the speed of UI
    JSONArray allSectionsArray;

	public HSHappyfoxGear(String instanceUrl, String api_key, String auth_code, String category_id, String priority_id) {

		assert instanceUrl != null : "Instance Url cannot be null";
		assert api_key != null : "Api key cannot be null";
		assert auth_code != null : "Authcode cannot be null";
		assert category_id != null : "Category id cannot be null";
		assert priority_id != null : "Priority id cannot be null";

		if (!instanceUrl.endsWith("/")) 
			instanceUrl = instanceUrl.concat("/"); 

		this.instanceUrl = instanceUrl;
		this.api_key = api_key;
		this.auth_code = auth_code;
		this.priority_id = priority_id;
		this.category_id = category_id;
	}

	// If user taps on a section, then section is send as a paremeter to the function
	@Override
	public void fetchKBArticle(String cancelTag, HSKBItem section, RequestQueue queue, OnFetchedArraySuccessListener success, ErrorListener errorListener) {

		if (section == null) {
			// This is first request of sections 

			if (this.section_id == null) {
				// Fetch all sections
				String url = getApiUrl().concat("kb/sections/");

				JsonArrayRequest request = new JsonArrayRequest(url, new HappyfoxArrayBaseListener<JSONArray>(success, errorListener) {

					@Override
					public void onResponse(JSONArray sectionsArray) {
						HSKBItem[] array = getSectionsFromData(sectionsArray);
						successCallback.onSuccess(array);
					}
				}, errorListener);

				// to avoid server overload call
				request.setRetryPolicy(new DefaultRetryPolicy(TicketFormRequest.TIMEOUT_MS, TicketFormRequest.MAX_RETRIES, TicketFormRequest.BACKOFF_MULT));
                request.setTag(cancelTag);

				queue.add(request);
			}
			else {
				// Fetch individual section
				String url = getApiUrl().concat("kb/section/").concat(this.section_id).concat("/");

				JsonObjectRequest request = new JsonObjectRequest(url, null, new HappyfoxArrayBaseListener<JSONObject>(success, errorListener) {

					@Override
					public void onResponse(JSONObject sectionsArray) {
						try {
							HSKBItem[] array = getArticlesFromSection(sectionsArray);
							successCallback.onSuccess(array);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, errorListener);

				// to avoid server overload call
				request.setRetryPolicy(new DefaultRetryPolicy(TicketFormRequest.TIMEOUT_MS, TicketFormRequest.MAX_RETRIES, TicketFormRequest.BACKOFF_MULT));
                request.setTag(cancelTag);

				queue.add(request);
			}

		}
		else {
			// Actually it should go to server to get list of all sections, but we are saving it here to avoid server call.
			assert allSectionsArray!=null: "This gear was re-created and articles for section is lost.";

			int count  = allSectionsArray.length();
			for (int i = 0; i < count; i++) {
				try {
					JSONObject sectionObject = allSectionsArray.getJSONObject(i);
					String section_id = sectionObject.getString("id");

					if (section.getId().equals(section_id)) {
						HSKBItem[] array = getArticlesFromSection(sectionObject);
						success.onSuccess(array); // Work accomplished
						break;
					}

				} catch (JSONException e) {
					e.printStackTrace();
					errorListener.onErrorResponse(new VolleyError("parsing failed"));
				}
			}
		}

	}

	@Override
	public void registerNewUser(String cancelTag, String firstName, String lastname, String emailAddress, RequestQueue queue,
            OnFetchedSuccessListener success, ErrorListener error) {

		HSUser user = HSUser.createNewUserWithDetails(firstName, lastname, emailAddress);
		success.onSuccess(user);
	}

	@Override
	public void createNewTicket(String cancelTag, HSUser user, String message, String body, HSUploadAttachment[] attachments,  RequestQueue queue,
			OnNewTicketFetchedSuccessListener successListener, ErrorListener errorListener) {

		Properties prop = new Properties();
		prop.put("name", user.getFullName());
		prop.put("email", user.getEmail());
		prop.put("category", category_id);
		prop.put("priority", priority_id);
		prop.put("subject", message);
		prop.put("text", body);

		TicketFormRequest request = new TicketFormRequest(getApiUrl()+"new_ticket/", prop, attachments,  new CreateNewTicketSuccessListener(user, successListener, errorListener) {

			@Override
			public void onResponse(JSONObject response) {

				try {
					HSTicket ticket = HSTicket.createATicket(response.getString("id"), response.getString("subject"));
					HSUser user = HSUser.appendCredentialOnUserDetail(this.user,response.getJSONObject("user").getString("id"), null);
					this.successListener.onSuccess(user, ticket);
				} catch (JSONException e) {
					e.printStackTrace();
					this.errorListener.onErrorResponse(new VolleyError("Parsing failed when creating a ticket"));
				}
			}
		}, errorListener);

		request.addCredential(api_key, auth_code);
		request.setTag(cancelTag);

		queue.add(request);
		queue.start();
	}

	@Override
	public void fetchAllUpdateOnTicket(String cancelTag, HSTicket ticket, HSUser user, RequestQueue queue,
			OnFetchedArraySuccessListener success, ErrorListener errorListener) {

		TicketFormRequest request = new TicketFormRequest(getApiUrl() + "ticket/" + ticket.getTicketId(), new HappyfoxArrayBaseListener<JSONObject>(success, errorListener) {

			@Override
			public void onResponse(JSONObject response) {

				try {
					JSONArray updateArray = response.getJSONArray("updates");

					ArrayList<HSTicketUpdate> ticketUpdates = new ArrayList<HSTicketUpdate>();

					int updateLen = updateArray.length();
					for (int i = 0; i < updateLen; i++) {
						JSONObject updateObject = updateArray.getJSONObject(i);

						if (!updateObject.isNull("message")) {
							HSTicketUpdate update = parseTicketUpdateFromJson(updateObject);
							if (update != null) {
								ticketUpdates.add(update);
							}
						}
					}

					HSTicketUpdate[] array = new HSTicketUpdate[0];
					array = ticketUpdates.toArray(array);
					this.successCallback.onSuccess(array);
				} catch (JSONException e) {
					e.printStackTrace();
					this.errorListener.onErrorResponse(new VolleyError("Parsing failed when fetching all update for a ticket"));
				}

			}
		}, errorListener);

		request.addCredential(api_key, auth_code);
		request.setTag(cancelTag);

		queue.add(request);
		queue.start();
	}

	@Override
	public void addReplyOnATicket(String cancelTag, String message, HSUploadAttachment[] attachments,  HSTicket ticket, HSUser user, RequestQueue queue,
            OnFetchedSuccessListener success, ErrorListener errorListener) {

		Properties prop = new Properties();
		prop.put("user", user.getUserId());
		prop.put("text", message);

		TicketFormRequest request = new TicketFormRequest(getApiUrl()+"ticket/" + ticket.getTicketId() + "/user_reply/", prop, attachments,
				new HappyfoxBaseListner<JSONObject>(success, errorListener) {

					@Override
					public void onResponse(JSONObject response) {

						try {
							HSTicketUpdate update = null;
							// fetch last message from user in update array.
							JSONArray updateArray = response.getJSONArray("updates");

							int updateLen = updateArray.length();
							assert updateLen>0 : "No updates were returned by server";
							for (int i = updateLen - 1; i >= 0 ; i--) {
								JSONObject updateObject = updateArray.getJSONObject(i);
								JSONObject byObject = updateObject.getJSONObject("by");

								if (!byObject.getString("type").equals("user") && updateObject.isNull("message")) {
									continue;
								}

								update = parseTicketUpdateFromJson(updateObject);

								if (update == null) {
									continue;
								}
								
								break;
							}

							if (update == null) {
								this.errorListener.onErrorResponse(new VolleyError("Could not find user message in update"));
							}
							else {
								this.successCallback.onSuccess(update);
							}

						} catch (JSONException e) {
							e.printStackTrace();
							this.errorListener.onErrorResponse(new VolleyError("Parsing failed when adding reply to a ticket"));
						}

					}
				}, errorListener);

		request.addCredential(api_key, auth_code);
		request.setTag(cancelTag);

		queue.add(request);
		queue.start();
	}

    public void setSectionId(String sectionId) {
        this.section_id = sectionId;
    }

    private HSKBItem[] getArticlesFromSection(JSONObject sectionObject) throws JSONException {
        JSONArray articleArray = sectionObject.getJSONArray("articles");
        ArrayList<HSKBItem> kbArticleArray = new ArrayList<HSKBItem>();

        for (int j = 0; j < articleArray.length(); j++) {
            JSONObject arrayObject = articleArray.getJSONObject(j);
            HSKBItem item = HSKBItem.createForArticle(arrayObject.getString("id"), arrayObject.getString("title").trim(), arrayObject.getString("contents"));
            kbArticleArray.add(item);
        }

        HSKBItem[] array = new HSKBItem[0];
        array = kbArticleArray.toArray(array);
        return array;
    }

    private HSKBItem[] getSectionsFromData(JSONArray sectionsArray) {
        allSectionsArray = sectionsArray;
        ArrayList<HSKBItem> kbSectionArray = new ArrayList<HSKBItem>();

        int count  = sectionsArray.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject sectionObject = sectionsArray.getJSONObject(i);
                if (sectionObject.getJSONArray("articles").length() > 0) {
                    HSKBItem item = HSKBItem.createForSection(sectionObject.getString("id"), sectionObject.getString("name"));
                    kbSectionArray.add(item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                new VolleyError("parsing failed");
            }
        }

        HSKBItem[] array = new HSKBItem[0];
        array = kbSectionArray.toArray(array);
        return array;
    }

    
    /**
     * 
     * @param updateObject, can be null if it is private note
     * @return
     * @throws JSONException
     */
	private HSTicketUpdate parseTicketUpdateFromJson(JSONObject updateObject) throws JSONException {
		String updateId = null;
		String userName = null;

		if (updateObject.isNull("message")) {
			return null;
		}
		
		JSONObject messageObject = updateObject.getJSONObject("message");
		
		// private note
		if (!messageObject.isNull("message_type") && messageObject.getString("message_type").equals("p")) {
			return null;
		}
		
		JSONObject byObject = updateObject.getJSONObject("by");
		if (!byObject.isNull("name")) {
			userName = updateObject.getJSONObject("by").getString("name");
		}
		String message = updateObject.getJSONObject("message").getString("text");

		Date update_time = null;
		if (!updateObject.isNull("timestamp")) {
			update_time = parseTime(updateObject.getString("timestamp"));
		}

		JSONArray attachmentObjects = updateObject.getJSONObject("message").getJSONArray("attachments");
		HSAttachment[] attachments = null;
		if(attachmentObjects != null) {
			int length = attachmentObjects.length();
			ArrayList<HSAttachment> attachmentArray = new ArrayList<HSAttachment>();
			for(int i = 0; i < length; i++) {
				JSONObject attachmentData = attachmentObjects.getJSONObject(i);
				String attachment_url = attachmentData.getString("url");
				if (attachment_url.startsWith("/")) {
					attachment_url = instanceUrl.concat(attachment_url.substring(1));
				}
				HSAttachment attachData = HSAttachment.createAttachment(attachment_url, attachmentData.getString("filename"), null);
				attachmentArray.add(attachData);
			}
			attachments = attachmentArray.toArray(new HSAttachment[length]);
		}

		if (byObject.getString("type").equals("user")) {
			return HSTicketUpdate.createUpdateByUser(updateId, userName, message, update_time, attachments);
		}
		else {
			return HSTicketUpdate.createUpdateByStaff(updateId, userName, message, update_time, attachments);
		}
	}

	public String getApiUrl() {
		return this.instanceUrl+"api/1.1/json/";
	}

	protected static Date parseTime(String dateString) {
		Date givenTimeDate = null;
		try {
			givenTimeDate = parseUTCString(dateString,"yyyy-MM-dd HH:mm:ss");
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

	private abstract class HappyfoxArrayBaseListener<T> implements Listener<T> {

		protected OnFetchedArraySuccessListener successCallback;
		protected ErrorListener errorListener;

		public HappyfoxArrayBaseListener(OnFetchedArraySuccessListener success, ErrorListener errorListener) {
			this.successCallback = success;
			this.errorListener = errorListener;
		}
	}

	private abstract class HappyfoxBaseListner<T> implements Listener<T> {

		protected OnFetchedSuccessListener successCallback;
		protected ErrorListener errorListener;

		public HappyfoxBaseListner(OnFetchedSuccessListener success, ErrorListener errorListener) {
			this.successCallback = success;
			this.errorListener = errorListener;
		}
	}

	private abstract class CreateNewTicketSuccessListener implements Listener<JSONObject> {

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

	private class TicketFormRequest extends Request<JSONObject> {

		/** Socket timeout in milliseconds for image requests */
		protected static final int TIMEOUT_MS = 10000;

		/** Default number of retries for image requests */
		protected static final int MAX_RETRIES = 2;

		/** Default backoff multiplier for image requests */
		protected static final float BACKOFF_MULT = 1f;

		private Listener<JSONObject> mListener;

		private MultipartEntity entity;

		HashMap<String, String> headers = new HashMap<String, String>();

		public TicketFormRequest(String url, Properties requestProperties, HSUploadAttachment[] attachments_to_upload,
                                 Listener<JSONObject> listener, ErrorListener errorListener) {
			super(Method.POST, url, errorListener);
			mListener = listener;

			setRetryPolicy(
					new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));

			entity = new MultipartEntity();

			// iter properties
			Enumeration<Object> enumKey = requestProperties.keys();
			while(enumKey.hasMoreElements()) {
				String key = (String) enumKey.nextElement();
				String val = requestProperties.getProperty(key);
				try {
					entity.addPart(key, new StringBody(val));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			// Adding attachments if any
			if (attachments_to_upload != null) {
				for (int i = 0; i < attachments_to_upload.length; i++) {
					try {
						entity.addPart("attachments", attachments_to_upload[i].generateStreamToUpload());
					} catch (FileNotFoundException e) {
						Log.e(TAG, "Attachment upload failed");
						e.printStackTrace();
					}
				}
			}
		}

		public TicketFormRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
			super(Method.GET, url, errorListener);
			mListener = listener;
			setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
		}

		@Override
		public String getBodyContentType()
		{
			if (entity == null) {
				return super.getBodyContentType();
			}
			return entity.getContentType().getValue();
		}

		@Override
		public byte[] getBody() throws AuthFailureError
		{
			if (entity == null) {
				return super.getBody();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				entity.writeTo(bos);
			} catch (IOException e) {
				VolleyLog.e("IOException writing to ByteArrayOutputStream");
			}
			return bos.toByteArray();
		}

		@Override
		protected void deliverResponse(JSONObject response) {
			mListener.onResponse(response);
		}

		@Override
		protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
			try {
				String jsonString =
						new String(response.data, HttpHeaderParser.parseCharset(response.headers));
				return Response.success(new JSONObject(jsonString),
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (UnsupportedEncodingException e) {
				return Response.error(new ParseError(e));
			} catch (JSONException je) {
				return Response.error(new ParseError(je));
			}
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			return headers;
		}

		public void addCredential(String name, String password) {
			String credentials = name + ":" + password;
			String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
			headers.put("Authorization", "Basic "+base64EncodedCredentials);
		}
	}

}
