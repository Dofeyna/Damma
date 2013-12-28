package com.parse.starter;

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

public class ParseStarterProjectActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	
	MultiPlayer mp;
	ProgressDialog progress;
	Handler handlerUI;
	Button buttonMP,buttonSP,buttonCG;
	TextView checkers;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpened(getIntent());
		
		handlerUI = new Handler();
		
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Android.ttf");
		checkers = (TextView)findViewById(R.id.tvCheckers);
		checkers.setTypeface(tf);
		
		buttonMP = (Button)findViewById(R.id.buttonMP);
		buttonMP.setOnClickListener(this);
		
		buttonSP = (Button)findViewById(R.id.buttonSP);
		buttonSP.setOnClickListener(this);
		
		buttonCG = (Button)findViewById(R.id.buttonCG);
		buttonCG.setOnClickListener(this);
		buttonCG.setClickable(false);
		
		ParseApplication.game = new Game();	
		ParseApplication.game.initialize();
		mp = new MultiPlayer();
		
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.buttonMP ){
			int response = mp.findMate();
			
			if( response == -1 ){
				progress = new ProgressDialog(this);
				progress.setCancelable(true);
				progress.setMessage("Finding an Opponent...");
				progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
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
                    	
                    	if(situation == 0){
                    		ParseApplication.game.deleteGame();
        					progress.dismiss();
        					Toast.makeText(getApplicationContext(), "Couldn't Find :(((",
        							   Toast.LENGTH_SHORT).show();
        				}
        				else if(situation == 1){
        					buttonCG.setClickable(true);
        					Intent myIntent = new Intent( ParseStarterProjectActivity.this, GameActivity.class);
        		            startActivityForResult(myIntent, 0);
        				}
        				else{
        					//The Game is finished
        				}
                    	
                    	progress.setTitle("Finish");
                    }
                }, 10000);
				
			}
			else{
				ParseApplication.game.joinGame(response);
				Intent myIntent = new Intent( ParseStarterProjectActivity.this, GameActivity.class);
	            startActivityForResult(myIntent, 0);
			}
		}
		else if( v.getId() == R.id.buttonSP ){
			ParseApplication.game.joinGame(2);
			Intent myIntent = new Intent(v.getContext(), GameActivity.class);
            startActivityForResult(myIntent, 0);
		}
		else if( v.getId() == R.id.buttonCG ){
			Intent myIntent = new Intent(v.getContext(), GameActivity.class);
            startActivityForResult(myIntent, 0);
		}
	}
	

}
