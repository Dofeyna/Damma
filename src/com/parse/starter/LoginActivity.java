package com.parse.starter;

import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener{

	EditText username, password;
	Button login;
	TextView warning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);
		
		username = (EditText)findViewById(R.id.username2);
		password = (EditText)findViewById(R.id.password2);
		login = (Button)findViewById(R.id.loginButton2);
		warning = (TextView)findViewById(R.id.tvWarning2);
		
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String uName = username.getText().toString();
		String pass = password.getText().toString();
		
		if( uName != null && pass != null ){
		
			try {
				ParseUser.logIn(uName, pass);
				Intent myIntent = new Intent( LoginActivity.this, ParseStarterProjectActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivityForResult(myIntent, 0);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				warning.setVisibility(TextView.VISIBLE);
			}
		}
	}

}
