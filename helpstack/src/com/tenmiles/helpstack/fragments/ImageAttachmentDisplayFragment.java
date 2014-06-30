package com.tenmiles.helpstack.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.polites.android.GestureImageView;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.service.DownloadAttachmentUtility;

public class ImageAttachmentDisplayFragment extends HSFragmentParent {
	
	private static final String TAG = ImageAttachmentDisplayFragment.class.getSimpleName();

	public String image_url;
	
	GestureImageView imageView;
	LocalAsync localAsync;

	private View progressView;
	
	boolean isAttachmentDownloaded = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		View rootView = inflater.inflate(
				R.layout.fragment_image_attachment_display, container,
				false);
		
		progressView = rootView.findViewById(R.id.progressHolder);
		
		imageView = (GestureImageView) rootView.findViewById(R.id.image);
		
		setHasOptionsMenu(true);
		
		if (savedInstanceState == null) {
			showLoading(true);
			loadImage();
		}
		else {
			isAttachmentDownloaded = savedInstanceState.getBoolean("isAttachmentDownloaded");
			if (isAttachmentDownloaded) {
				showLoading(false);
				imageView.setImageBitmap((Bitmap)savedInstanceState.getParcelable("bitmap"));
			}
			else {
				showLoading(true);
				loadImage();
			}
			
			
		}
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isAttachmentDownloaded", isAttachmentDownloaded);
		outState.putParcelable("bitmap", ((BitmapDrawable)imageView.getDrawable()).getBitmap());
	}
	
	@Override
	public void onDestroy() {
		closeAsync();
		super.onDestroy();
	}
	
	public void showLoading(boolean visible) {
		progressView.setVisibility(visible?View.VISIBLE:View.GONE);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.image_attachment_display, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_download) {
			DownloadAttachmentUtility.downloadAttachment(getActivity(), image_url, getHelpStackActivity().getTitle().toString());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void loadImage() {
		closeAsync();
		localAsync = new LocalAsync();
		localAsync.execute(image_url);
	}
	
	public void closeAsync() {
		if(localAsync!=null&& localAsync.getStatus()!=AsyncTask.Status.FINISHED) {
			localAsync.cancel(true);
			localAsync = null;
		}
	}
	
	private class LocalAsync extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoading(true);
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			
			try {
				return downloadBitmap(params[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			showLoading(false);
			
			if(result!=null) {
				isAttachmentDownloaded = true;
				imageView.setImageDrawable(new BitmapDrawable(getResources(), result));
			}
			else {
				HSUtils.showAlertDialog(getActivity(), "Error", "Error in fetching Attachment");
			}
		}
	}

	private static Bitmap downloadBitmap(String url) throws IOException {
		Bitmap bitmap = null;
	    InputStream in = null;
	    try {
	    	BitmapFactory.Options options = new BitmapFactory.Options();
	    	bitmap = BitmapFactory.decodeStream(new URL(url).openStream(),null,options);
	        
	    } catch (IOException e) {
	        Log.e(TAG, "Could not load Bitmap from: " + url);
	        throw e;
	    } finally {
	    	if (in != null) {
                in.close();  
            }
	    }

	    return bitmap;
	}

}
