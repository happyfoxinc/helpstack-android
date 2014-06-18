package com.tenmiles.helpstack.application;

import android.app.Application;

import com.tenmiles.helpstack.logic.HSHappyfoxGear;
import com.tenmiles.helpstack.logic.HSHelpStack;

public class HSApplication extends Application{

	HSHelpStack helpStack;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		helpStack = HSHelpStack.getInstance(this);
		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://support.happyfox.com", null, null, null, null);
		helpStack.setGear(happyfoxGear);
		
//		HSEmailGear emailGear = new HSEmailGear( "support@happyfox.com",R.xml.articles);
//		helpStack.setGear(emailGear);
		
//		helpStack.ovverideGearArticlesWithLocalArticlePath(R.xml.articles);
	}
}
