package com.example.helpstackexample;

import android.app.Application;

import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;

public class HSApplication extends Application {

	HSHelpStack helpStack;

	@Override
	public void onCreate() {
		super.onCreate();

		helpStack = HSHelpStack.getInstance(this);

		HSEmailGear emailGear = new HSEmailGear("foo@bar.com", R.xml.articles);
		helpStack.setGear(emailGear);

		/* Uncomment the following to use the HappyFox gear with appropriate support email address */
		//		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear(
		//              "https://example.happyfox.com",
		//				"<API Key>",
		//				"<Auth Code>",
		//				"<Category ID>",
		//              "<Priority ID>");
		//		helpStack.setGear(happyfoxGear);
	}
}
