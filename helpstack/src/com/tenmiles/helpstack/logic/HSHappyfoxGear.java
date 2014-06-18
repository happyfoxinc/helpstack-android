package com.tenmiles.helpstack.logic;


import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tenmiles.helpstack.model.HSKBItem;


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
	
	
	
	JSONArray allSectionsArray;
	
	@Override
	public void fetchKBArticle(HSKBItem section, RequestQueue queue,
			OnFetchedArraySuccessListener success, ErrorListener errorListener) {
		
		
		if (section == null) {
			JsonArrayRequest request = new JsonArrayRequest(this.instanceUrl+"api/1.1/json/kb/sections/", new HappyfoxBaseListner<JSONArray>(success) {

				@Override
				public void onResponse(JSONArray sectionsArray) {
					
					allSectionsArray = sectionsArray;
					ArrayList<HSKBItem> kbItemArray = new ArrayList<HSKBItem>();
					
					int count  = sectionsArray.length();
					for (int i = 0; i < count; i++) {
						try {
							JSONObject sectionObject = sectionsArray.getJSONObject(i);
							if (sectionObject.getJSONArray("articles").length() > 0) {
								HSKBItem item = HSKBItem.createForSection(sectionObject.getString("id"), sectionObject.getString("name"));
								kbItemArray.add(item);
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
							new VolleyError("parsing failed");
						}
					}
					
					HSKBItem[] array = new HSKBItem[0];
					array = kbItemArray.toArray(array);
					successCallback.onSuccess(array);
					
				}
			}, errorListener);
			
			queue.add(request);
			queue.start();
		}
		else {
			
		}
		
		
	}
	
	
	private abstract class HappyfoxBaseListner<T> implements Listener<T> {

		public OnFetchedArraySuccessListener successCallback;

		public HappyfoxBaseListner(OnFetchedArraySuccessListener success) {
			this.successCallback = success;
		}
		
	}

}
