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
		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://example.happyfox.com", 
				"<Your API Key>",
				"<Your Auth Code>", 
				"<Category ID>", "<Priority ID>");
		helpStack.setGear(happyfoxGear);
		
		/* Uncomment the following to use the Email gear with appropriate support email address */
//		HSEmailGear emailGear = new HSEmailGear( "example@happyfox.com",R.xml.articles);
//		helpStack.setGear(emailGear);
	}
}
