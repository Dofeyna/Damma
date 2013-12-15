package com.parse.starter;

import java.util.HashMap;

import com.parse.ParseCloud;
import com.parse.ParseException;

public class MultiPlayer {
	
	public int findMate(){
		int response = -2;
		try {
			response = ParseCloud.callFunction("findMate", new HashMap<String, Integer>());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;		
	}

}
