package com.tenmiles.helpstack.application;

import android.app.Application;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.logic.HSHelpStack;
import com.tenmiles.helpstack.logic.HSTestDataGear;

public class HSApplication extends Application{

	HSHelpStack helpStack;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		helpStack = HSHelpStack.getInstance(this);
//		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://acmewidgetsco.happyfox.com", 
//				"431293d604214378b33a950fd11c0454",
//				"6c8c7c74dfe0491494e26883ee5e8b23", 
//				"77", "1");
//		helpStack.setGear(happyfoxGear);
		
		HSTestDataGear testGear = new HSTestDataGear(R.xml.articles);
		helpStack.setGear(testGear);
		
//		HSEmailGear emailGear = new HSEmailGear( "support@happyfox.com",R.xml.articles);
//		helpStack.setGear(emailGear);
		
		helpStack.ovverideGearArticlesWithLocalArticlePath(R.xml.articles);
	}
}
