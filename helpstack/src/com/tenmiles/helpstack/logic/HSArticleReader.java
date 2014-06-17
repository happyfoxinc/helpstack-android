package com.tenmiles.helpstack.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

import com.tenmiles.helpstack.model.HSKBItem;

/**
 * 
 * @author Nalin Chhajer
 *
 */
public class HSArticleReader {

	private int articleResourceId;

	public HSArticleReader(int articlesResourceId) {
		this.articleResourceId = articlesResourceId;
	}
	
	public HSKBItem[] readArticlesFromResource(Context context) throws XmlPullParserException, IOException {
		  ArrayList<HSKBItem> articles = new ArrayList<HSKBItem>();
		  XmlPullParser xpp = context.getResources().getXml(articleResourceId);

		  while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
		    if (xpp.getEventType()==XmlPullParser.START_TAG) {
		      
		    	if (xpp.getName().equals("article")) {
		    	  
		    	  int attributeCount = xpp.getAttributeCount();
		    	  String subject = null;
		    	  String text = null;
		    	  for (int i = 0; i < attributeCount; i++) {
		    		String attrName = xpp.getAttributeName(i);
					if (attrName.equals("subject")) {
						subject = xpp.getAttributeValue(i);
					}
					if (attrName.equals("text")) {
						text = xpp.getAttributeValue(i);
					}
		    	  }
		    	  
		    	  assert subject != null : "Subject was not specified in xml for article @ index "+articles.size()+1;
		    	  assert text != null : "Text was not specified in xml for article @ index "+articles.size()+1;
		    	  articles.add(new HSKBItem(null, subject, text));
		      
		    	}
		    }

		    xpp.next();
		  }
		  
		  HSKBItem[] articleArray = new HSKBItem[0];
		  articleArray = articles.toArray(articleArray);
		  return articleArray;
	}
	
	
}
