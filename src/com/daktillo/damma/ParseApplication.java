package com.daktillo.damma;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "8mwgMTwEb5XKuNWRrPUJJzhYPnDHaEAnxttv2Joh", "iw7tq46bcZA3A0Fdo6o7TSaJvEPEyV245sEukcVz"); 


		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
				
		ParseACL.setDefaultACL(defaultACL, true);
	}

}
