package com.example.helpstackexample;

import android.app.Application;

import com.tenmiles.helpstack.HSHappyfoxGear;
import com.tenmiles.helpstack.HSHelpStack;



public class HSApplication extends Application{

	HSHelpStack helpStack;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		helpStack = HSHelpStack.getInstance(this);
		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://acmewidgetsco.happyfox.com", 
				"431293d604214378b33a950fd11c0454",
				"6c8c7c74dfe0491494e26883ee5e8b23", 
				"77", "1");
		
//		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://wonderfulday.happyfox.com", 
//				"b9613e78614d44f591b60ce6b2908109",
//				"de25dd11000847f683a97db8657718dd", 
//				"1", "1");
		helpStack.setGear(happyfoxGear);
		helpStack.ovverideGearArticlesWithLocalArticlePath(R.xml.articles);
		
//		HSTestDataGear testGear = new HSTestDataGear(R.xml.articles);
//		helpStack.setGear(testGear);
		
//		HSEmailGear emailGear = new HSEmailGear( "support@happyfox.com",R.xml.articles);
//		helpStack.setGear(emailGear);
		
//		helpStack.ovverideGearArticlesWithLocalArticlePath(R.xml.articles);
	}
}
