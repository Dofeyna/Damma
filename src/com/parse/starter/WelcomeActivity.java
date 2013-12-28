package com.parse.starter;

import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity implements OnClickListener {

	Button signUp, login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openscreen);
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if( currentUser != null ){
			Intent myIntent = new Intent( this, ParseStarterProjectActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
		}
		
		login = (Button)findViewById(R.id.loginButton3);
		signUp = (Button)findViewById(R.id.signUpButton3);
		
		login.setOnClickListener(this);
		signUp.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.loginButton3 ){
			Intent myIntent = new Intent( WelcomeActivity.this, LoginActivity.class);
            startActivityForResult(myIntent, 0);
		}
		else{
			Intent myIntent = new Intent( WelcomeActivity.this, SignUpActivity.class);
            startActivityForResult(myIntent, 0);
		}
		
	}
	
	

}
