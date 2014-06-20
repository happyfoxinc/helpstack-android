package com.tenmiles.helpstack.logic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;


public class HSHappyfoxGear extends HSGear {

	private String instanceUrl;
	private String api_key;
	private String auth_code;
	private String priority_id;
	private String category_id;

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
	
	
	// This are cached here so server call can be minimized and improve the speed of UI
	JSONArray allSectionsArray;
	
	@Override
	public void fetchKBArticle(HSKBItem section, RequestQueue queue,
			OnFetchedArraySuccessListener success, ErrorListener errorListener) {
		
		
		if (section == null) {
			JsonArrayRequest request = new JsonArrayRequest(getApiUrl()+"kb/sections/", new HappyfoxBaseListner<JSONArray>(success) {

				@Override
				public void onResponse(JSONArray sectionsArray) {
					
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
					successCallback.onSuccess(array);
					
				}
			}, errorListener);
			
			queue.add(request);
			queue.start();
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
						JSONArray articleArray = sectionObject.getJSONArray("articles");
						ArrayList<HSKBItem> kbArticleArray = new ArrayList<HSKBItem>();
						
						for (int j = 0; j < articleArray.length(); j++) {
							JSONObject arrayObject = articleArray.getJSONObject(j);
							HSKBItem item = HSKBItem.createForArticle(arrayObject.getString("id"), arrayObject.getString("title"), arrayObject.getString("contents"));
							kbArticleArray.add(item);
						}
						
						HSKBItem[] array = new HSKBItem[0];
						array = kbArticleArray.toArray(array);
						success.onSuccess(array); // Work accomplished
						
						break;
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					errorListener.onErrorResponse(new VolleyError("parsing failed"));
				}
			}
		}
	}
		
	@Override
	public void registerNewUser(String firstName, String lastname,
			String emailAddress, RequestQueue queue,OnFetchedSuccessListener success,
			ErrorListener error) {
		
		HSUser user = HSUser.createNewUserWithDetails(firstName, lastname, emailAddress);
		success.onSuccess(user);
		
	}
	
	@Override
	public void createNewTicket(HSUser user, String message, String body,RequestQueue queue,
			OnNewTicketFetchedSuccessListener successListener,
			ErrorListener errorListener) {
		
		JSONObject postParams = new JSONObject();
		try {
			postParams.put("name", user.getFullName());
			postParams.put("email", user.getEmail());
			postParams.put("category", category_id);
			postParams.put("priority", priority_id);
			postParams.put("subject", message);
			postParams.put("text", body);
			
		} catch (JSONException e) {
			// Not gonna come here
			errorListener.onErrorResponse(new VolleyError("Invalid data specified when creating ticket"));
			e.printStackTrace();
			return;
		}
		
		
		TicketJSONRequest request = new TicketJSONRequest(getApiUrl()+"new_ticket/", postParams, new CreateNewTicketSuccessListener(user, successListener, errorListener) {

			@Override
			public void onResponse(JSONObject response) {
				
				try {
					HSTicket ticket = HSTicket.createATicket(response.getString("id"), response.getString("subject"));
					HSUser user = HSUser.appendCredentialOnUserDetail(this.user,response.getJSONObject("user").getString("id"), null);
					this.successListener.onSuccess(user, ticket);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.errorListener.onErrorResponse(new VolleyError("Parsing failed when creating a ticket"));
				}
				
			}
		}, errorListener);
		
		request.addCredential(api_key, auth_code);
		
		queue.add(request);
		queue.start();
	}
	
	
	
	private abstract class HappyfoxBaseListner<T> implements Listener<T> {

		public OnFetchedArraySuccessListener successCallback;

		public HappyfoxBaseListner(OnFetchedArraySuccessListener success) {
			this.successCallback = success;
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

	public String getApiUrl() {
		return this.instanceUrl+"api/1.1/json/";
	}
	
	
	private class TicketJSONRequest extends JsonObjectRequest {

		
		HashMap<String, String> headers = new HashMap<String, String>();
		
		public TicketJSONRequest(int method, String url,
				JSONObject jsonRequest, Listener<JSONObject> listener,
				ErrorListener errorListener) {
			super(method, url, jsonRequest, listener, errorListener);
			// TODO Auto-generated constructor stub
		}

		public TicketJSONRequest(String url, JSONObject jsonRequest,
				Listener<JSONObject> listener, ErrorListener errorListener) {
			super(url, jsonRequest, listener, errorListener);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			// TODO Auto-generated method stub
			return headers;
		}
		
		public void addCredential(String name, String password) {
			String credentials = name + ":" + password;
	    	String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
	    	headers.put("Authorization", "Basic "+base64EncodedCredentials);
		}
		
	}
	
	
}
