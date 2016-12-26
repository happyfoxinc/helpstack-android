package com.example.helpstackexample;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;

import io.fabric.sdk.android.Fabric;

public class HSApplication extends Application {

    public static HSHelpStack helpStack;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        helpStack = HSHelpStack.getInstance(this);

        HSEmailGear emailGear = new HSEmailGear("foo@bar.com", R.xml.articles);
        helpStack.setGear(emailGear);

		/* Uncomment the following to use the HappyFox gear with appropriate support email address */

//        HSHappyfoxGear happyfoxGear = new HSHappyfoxGear(
//              "http://example.happyfox.com",
//                "<API Key>",
//                "<Auth code>",
//                "<Category ID>",
//                "<Priority ID>");
//
//        helpStack.setGear(happyfoxGear);

    }

}
