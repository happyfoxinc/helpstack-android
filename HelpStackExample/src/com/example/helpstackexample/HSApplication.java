package com.example.helpstackexample;

import android.app.Application;

import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSDeskGear;
import com.tenmiles.helpstack.gears.HSEmailGear;
import com.tenmiles.helpstack.gears.HSZendeskGear;



public class HSApplication extends Application{

	HSHelpStack helpStack;

	@Override
	public void onCreate() {
		super.onCreate();

		helpStack = HSHelpStack.getInstance(this);

//		HSEmailGear emailGear = new HSEmailGear( "example@happyfox.com",R.xml.articles);
//		helpStack.setGear(emailGear);


		/* Uncomment the following to use the Happyfox gear with appropriate support email address */
		//		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://example.happyfox.com", 
		//				"<Your API Key>",
		//				"<Your Auth Code>", 
		//				"<Category ID>", "<Priority ID>");
		//		helpStack.setGear(happyfoxGear);

//		HSZendeskGear zenDeskGear = new HSZendeskGear(
//				"https://2robots1.zendesk.com/",
//				"anirudh24seven@gmail.com",
//				"WIgGdsweOaIlM1Ml50D9g3ejkFIFvBbKtlVF5MCK");
//		helpStack.setGear(zenDeskGear);

		HSDeskGear deskGear = new HSDeskGear(
				"https://2robots2.desk.com/",
				"anirudh.24seven@gmail.com",
				"anirudh.24seven@gmail.com",
				"Temppassword123$");
		helpStack.setGear(deskGear);
	}
}
