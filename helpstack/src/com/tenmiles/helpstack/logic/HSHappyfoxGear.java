package com.tenmiles.helpstack.logic;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSUser;


public class HSHappyfoxGear extends HSGear {

	private String instanceUrl;
	private String api_key;
	private String auth_code;
	private String priority_id;
	private String category_id;

	public HSHappyfoxGear(String instanceUrl, String api_key, String auth_code, String priority_id, String category_id) {
		
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
			JsonArrayRequest request = new JsonArrayRequest(this.instanceUrl+"api/1.1/json/kb/sections/", new HappyfoxBaseListner<JSONArray>(success) {

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
			String emailAddress, OnFetchedSuccessListener success,
			ErrorListener error) {
		
		HSUser user = HSUser.createNewUserWithDetails(firstName, lastname, emailAddress);
		success.onSuccess(user);
		
	}
	
	private abstract class HappyfoxBaseListner<T> implements Listener<T> {

		public OnFetchedArraySuccessListener successCallback;

		public HappyfoxBaseListner(OnFetchedArraySuccessListener success) {
			this.successCallback = success;
		}
		
	}

}
