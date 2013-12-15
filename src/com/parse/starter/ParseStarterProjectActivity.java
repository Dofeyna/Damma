package com.parse.starter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseAnalytics;

public class ParseStarterProjectActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	
	Game game;
	MultiPlayer mp;
	ProgressDialog progress;
	Handler handlerUI;
	Button buttonMP;
	Button buttonSP;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpened(getIntent());
		
		handlerUI = new Handler();
		
		buttonMP = (Button)findViewById(R.id.buttonMP);
		buttonMP.setOnClickListener(this);
		
		buttonSP = (Button)findViewById(R.id.buttonSP);
		buttonSP.setOnClickListener(this);
		
		game = new Game();		
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
				    	game.deleteGame();
				        dialog.dismiss();
				    }
				});
				progress.show();
				game.initialize();
				
				handlerUI.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	int situation = game.checkSituation();
                    	
                    	if(situation == 0){
                    		game.deleteGame();
        					progress.dismiss();
        					Toast.makeText(getApplicationContext(), "Couldn't Find :(((",
        							   Toast.LENGTH_SHORT).show();
        				}
        				else if(situation == 1){
        					
        				}
        				else{
        					//The Game is finished
        				}
                    	
                    	progress.setTitle("Finish");
                    }
                }, 10000);
				
			}
			else{
				game.joinGame(response);
			}
		}
		else if( v.getId() == R.id.buttonSP ){
			Intent myIntent = new Intent(v.getContext(), GameActivity.class);
            startActivityForResult(myIntent, 0);
		}
	}
	

}
