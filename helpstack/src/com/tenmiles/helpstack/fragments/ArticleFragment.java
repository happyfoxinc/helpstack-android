package com.tenmiles.helpstack.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.model.HSKBItem;

public class ArticleFragment extends HSFragmentParent {
	
	public static final String HTML_WRAPPER_WITH_TITLE = "<!DOCTYPE html><html><head><style>body{font:16.0px helvetica} .heading{}</style></head><body><h3 class='heading'>%s</h3>%s</body></html>";
	public HSKBItem kbItem;
	
	public ArticleFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_article, container, false);
		getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		WebView webview = (WebView)rootView.findViewById(R.id.webview);
		String content = this.kbItem.getBody();
		String contentText = String.format(HTML_WRAPPER_WITH_TITLE, this.kbItem.getSubject(), content);
		webview.loadData(contentText, "text/html", null);
		getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
		return rootView;
	}

}
