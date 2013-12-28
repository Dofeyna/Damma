package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class GameActivity extends Activity implements OnClickListener {

	int[][] pieces;
	boolean[][] highlights;
	ImageView[][] views;
	ImageView v;
	boolean clicked = false;
	Button refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamescreen);

		pieces = new int[8][8];
		highlights = new boolean[8][8];
		views = new ImageView[8][8];
		
		refresh = (Button)findViewById(R.id.refresh);
		refresh.setOnClickListener(this);
		
		if( ParseApplication.game.turn == ParseApplication.game.color ){
			refresh.setEnabled(false);
		}
		else{
			refresh.setEnabled(true);
		}

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				views[x][y] = (ImageView) findViewById(getResources()
						.getIdentifier("P" + x + "x" + y, "id",
								getPackageName()));
				views[x][y].setOnClickListener(this);

			}
		}

		updateBoard();

	}

	public void updateBoard() {
		pieces = ParseApplication.game.pieces;

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				v = views[x][y];

				if (pieces[x][y] == 0) {
					v.setVisibility(View.INVISIBLE);
				} else if (pieces[x][y] == 1) {
					v.setImageResource(R.drawable.black2);
					v.setVisibility(View.VISIBLE);
				} else if (pieces[x][y] == -1) {
					v.setImageResource(R.drawable.red2);
					v.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public void updateHighlights() {
		highlights = ParseApplication.game.highlights;

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				v = (ImageView) findViewById(getResources().getIdentifier(
						"P" + x + "x" + y, "id", getPackageName()));

				if (highlights[x][y] == true) {
					if (ParseApplication.game.color == 1) {
						v.setImageResource(R.drawable.opalblack);
					} else {
						v.setImageResource(R.drawable.opalred);
					}

					v.setVisibility(View.VISIBLE);
				}

			}
		}
	}

	public void cleanHighlight() {
		ParseApplication.game.cleanHighlights();
	}

	@Override
	public void onClick(View v) {
		
		if( v.getId() == R.id.refresh ){
			if( ParseApplication.game.updateWhenTurn() == true){
				refresh.setEnabled(false);
				updateBoard();
			}
		}
		else{
			Log.d("turn", "girdi");
			if (ParseApplication.game.turn == ParseApplication.game.color) {
				Log.d("turn2", "girdi2");
	
				String coor = getResources().getResourceName(v.getId());
	
				char xx = coor.charAt(22);
				char yy = coor.charAt(24);
	
				int x = Character.getNumericValue(xx);
				int y = Character.getNumericValue(yy);
	
				if (clicked == false) {
					cleanHighlight();
					if (ParseApplication.game.checkPath(x, y) == true) {
						clicked = true;
					}
				} else {
	
					if (highlights[x][y] == true) {
						Log.d("turn3", "girdi3");
						ParseApplication.game.makeMove(x, y);
						cleanHighlight();
						clicked = false;
						refresh.setEnabled(true);
					} else {
						cleanHighlight();
						if (ParseApplication.game.checkPath(x, y) == true) {
							clicked = true;
						}
					}
				}
	
				updateBoard();
				updateHighlights();
			}
		}
	}

}
