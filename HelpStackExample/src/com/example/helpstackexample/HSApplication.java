package com.example.helpstackexample;

import android.app.Application;

import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;
import com.tenmiles.helpstack.gears.HSHappyfoxGear;

public class HSApplication extends Application {

	HSHelpStack helpStack;

	@Override
	public void onCreate() {
		super.onCreate();

		helpStack = HSHelpStack.getInstance(this);

//		HSEmailGear emailGear = new HSEmailGear("foo@bar.com", R.xml.articles);
//		helpStack.setGear(emailGear);

		/* Uncomment the following to use the HappyFox gear with appropriate support email address */

				HSHappyfoxGear happyfoxGear = new HSHappyfoxGear(
		              "http://acmewidgetsco.happyfox.com",
						"b3416a6ebf4245bf9bbeb55b296ecf92",
						"6b562e17b1ab4c37bc8c955af8f5cc5c",
						"107",
		                "1");
				helpStack.setGear(happyfoxGear);
	}
}
