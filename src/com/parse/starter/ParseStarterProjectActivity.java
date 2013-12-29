package com.parse.starter;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;

public class ParseStarterProjectActivity extends Activity implements
		OnClickListener {

	/** Called when the activity is first created. */

	MultiPlayer mp;
	ProgressDialog progress;
	Handler handlerUI;
	Button buttonMP, buttonSP, buttonCG;
	TextView checkers;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpened(getIntent());

		handlerUI = new Handler();

		Typeface tf = Typeface
				.createFromAsset(getAssets(), "fonts/Android.ttf");
		checkers = (TextView) findViewById(R.id.tvCheckers);
		checkers.setTypeface(tf);

		buttonMP = (Button) findViewById(R.id.buttonMP);
		buttonMP.setOnClickListener(this);

		buttonSP = (Button) findViewById(R.id.buttonSP);
		buttonSP.setOnClickListener(this);

		buttonCG = (Button) findViewById(R.id.buttonCG);
		buttonCG.setOnClickListener(this);

		ParseApplication.game = new Game();
		ParseApplication.game.initialize();
		mp = new MultiPlayer();

		getCurGameId();
	}
	
	public void getCurGameId(){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userName", ParseUser.getCurrentUser().getUsername());
		HashMap<String, Integer> results = new HashMap<String, Integer>();;		
		int curGameId = -1;
		int color = 0;
		try {
			results = ParseCloud.callFunction("checkCurGame", params);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		curGameId = results.get("curGameId");
		color = results.get("curColor");

		ParseApplication.game.gameId = curGameId;
		ParseApplication.game.color = color;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonMP) {
			int response = mp.findMate();

			if (response == -1) {
				progress = new ProgressDialog(this);
				progress.setCancelable(true);
				progress.setMessage("Finding an Opponent...");
				progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								progress.setMessage("Cancelling...");
								ParseApplication.game.deleteGame();
								dialog.dismiss();
							}
						});
				progress.show();
				ParseApplication.game.newGame();

				handlerUI.postDelayed(new Runnable() {
					@Override
					public void run() {
						int situation = ParseApplication.game.checkSituation();

						if (situation == 0) {
							ParseApplication.game.deleteGame();
							progress.dismiss();
							Toast.makeText(getApplicationContext(),
									"Couldn't Find :(", Toast.LENGTH_SHORT)
									.show();
						} else if (situation == 1) {
							String channel = "game"
									+ ParseApplication.game.gameId + "b";
							PushService.subscribe(getApplicationContext(),
									channel, ParseStarterProjectActivity.class);
							
							// Update User's Current Game Id
							ParseUser user = ParseUser.getCurrentUser();
							user.put("curGameId", ParseApplication.game.gameId);
							user.put("curColor", ParseApplication.game.color);
							try {
								user.save();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							progress.dismiss();

							Intent myIntent = new Intent(
									ParseStarterProjectActivity.this,
									GameActivity.class);
							startActivityForResult(myIntent, 0);
						} else {
							// The Game is finished
						}

						progress.setTitle("Finish");
					}
				}, 10000);

			} else {
				ParseApplication.game.joinGame(response);
				String channel = "game" + ParseApplication.game.gameId + "r";
				PushService.subscribe(getApplicationContext(), channel,
						ParseStarterProjectActivity.class);
				
				// Update User's Current Game Id
				ParseUser user = ParseUser.getCurrentUser();
				user.put("curGameId", ParseApplication.game.gameId);
				user.put("curColor", ParseApplication.game.color);
				try {
					user.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent myIntent = new Intent(ParseStarterProjectActivity.this,
						GameActivity.class);
				startActivityForResult(myIntent, 0);
			}
		} else if (v.getId() == R.id.buttonSP) {

		} else if (v.getId() == R.id.buttonCG) {
			if (ParseApplication.game.gameId != -1) {
				Intent myIntent = new Intent(v.getContext(), GameActivity.class);
				startActivityForResult(myIntent, 0);
			} else {
				Toast.makeText(getApplicationContext(),
						"You don't have a game!", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
