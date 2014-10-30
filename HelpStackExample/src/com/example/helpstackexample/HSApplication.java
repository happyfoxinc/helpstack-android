package com.example.helpstackexample;

import android.app.Application;

import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;
import com.tenmiles.helpstack.gears.HSZendeskGear;



public class HSApplication extends Application{

	HSHelpStack helpStack;

	@Override
	public void onCreate() {
		super.onCreate();

		helpStack = HSHelpStack.getInstance(this);

		HSEmailGear emailGear = new HSEmailGear( "example@happyfox.com",R.xml.articles);
		helpStack.setGear(emailGear);


		/* Uncomment the following to use the Happyfox gear with appropriate support email address */
		//		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://example.happyfox.com", 
		//				"<Your API Key>",
		//				"<Your Auth Code>", 
		//				"<Category ID>", "<Priority ID>");
		//		helpStack.setGear(happyfoxGear);

	}
}
