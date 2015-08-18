//  ArticleFragment
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

package com.tenmiles.helpstack.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.model.HSKBItem;

public class ArticleFragment extends HSFragmentParent {
	
	public static final String HTML_WRAPPER_WITH_TITLE = 
			"<!DOCTYPE html><html><head><style>body{font:18.0px helvetica} .heading{}</style>" +
			"</head><body><h3 class='heading'>%s</h3>%s</body></html>";

	public HSKBItem kbItem;
	private WebView webview;
	
	public ArticleFragment() {
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.hs_fragment_article, container, false);
		
		webview = (WebView)rootView.findViewById(R.id.webview);
		
		if (savedInstanceState != null) {
			kbItem = (HSKBItem) savedInstanceState.getSerializable("kbItem");
		}
		
		webview.setWebChromeClient(new WebChromeClient() {
			   public void onProgressChanged(WebView view, int progress) {
			     // Activities and WebViews measure progress with different scales.
			     // The progress meter will automatically disappear when we reach 100%
				   progress = progress*100;
				   getHelpStackActivity().setSupportProgressBarVisibility(progress<98);
				   getHelpStackActivity().setSupportProgress(progress);
			   }
			 });
		
		initializeView();
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("kbItem", kbItem);
	}
	
	public void initializeView() {
		String content = this.kbItem.getBody();
		String contentText = String.format(HTML_WRAPPER_WITH_TITLE, this.kbItem.getSubject(), content);
		webview.loadData(contentText, "text/html", null);
	}

}
