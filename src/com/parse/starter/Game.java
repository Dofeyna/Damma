package com.parse.starter;

import java.util.HashMap;
import org.json.JSONArray;
import android.util.Log;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class Game {

	/*
	 * for pieces array : 0 = empty
	 * 					  -1 = red 
	 * 					  -2 = red dama 
	 * 					  1 = black 
	 * 					  2 = black dama
	 */

	int gameId = -1;
	int[][] pieces;
	JSONArray board;
	boolean[][] highlights;
	int selectedX, selectedY, targetX, targetY;
	int color;

	public boolean initialize() {
		color = 1;
		// Receive the gameId from the Parse
		receiveGameId();
		
		// initialize variables
		board = new JSONArray();
		pieces = new int[8][8];
		highlights = new boolean[8][8];
		
		// initialize highlighted arrays
		cleanHighlights();
		cleanBoard();

		// initialize white pieces
		for (int x = 1; x < 3; x++) {
			for (int y = 0; y < 8; y++) {
				pieces[x][y] = -1;
			}
		}

		// initialize black pieces
		for (int x = 5; x < 7; x++) {
			for (int y = 0; y < 8; y++) {
				pieces[x][y] = 1;
			}
		}
		
		//UPDATE PARSE DATABASE
		createBoardInParse();

		return true;
	}

	public boolean checkPath(int x, int y) {
		selectedX = x;
		selectedY = y;

		// Check Path of Men
		if (pieces[x][y] * color == 1) {

			// Check Up
			if (y < 7) {
				if (pieces[x][y + 1] * pieces[x][y] <= 0) {
					if (pieces[x][y + 1] == 0) {
						highlight(x, y + 1);
					} else {
						if (y < 6) {
							if (pieces[x][y + 2] == 0) {
								highlight(x, y + 2);
							}
						}
					}
				}
			}

			// Check Down
			if (y > 1) {
				if (pieces[x][y - 1] * pieces[x][y] < 0) {
					if (pieces[x][y - 2] == 0) {
						highlight(x, y - 2);
					}
				}
			}

			// Check Right
			if (x < 7) {
				if (pieces[x + 1][y] * pieces[x][y] <= 0) {
					if (pieces[x + 1][y] == 0) {
						highlight(x, y + 1);
					} else {
						if (y < 6) {
							if (pieces[x + 2][y] == 0) {
								highlight(x, y + 2);
							}
						}
					}
				}
			}

			// Check Right
			if (x > 0) {
				if (pieces[x - 1][y] * pieces[x][y] <= 0) {
					if (pieces[x - 1][y] == 0) {
						highlight(x, y - 1);
					} else {
						if (y > 1) {
							if (pieces[x - 2][y] == 0) {
								highlight(x, y - 2);
							}
						}
					}
				}
			}

			return true;
		}
		// Check Path of King
		else if (pieces[x][y] * color == 2) {

		}

		return false;
	}

	public boolean makeMove(int curX, int curY, int targetX, int targetY) {

		// Remove captured pieces
		if (targetY == curY) {
			if (targetX > curX) {
				for (int i = curX + 1; i < targetX; i++) {
					pieces[i][curY] = 0;
				}
			} else {
				for (int i = targetX + 1; i < curX; i++) {
					pieces[i][curY] = 0;
				}
			}
		} else {
			if (targetY > curY) {
				for (int i = curY + 1; i < targetY; i++) {
					pieces[i][curX] = 0;
				}
			} else {
				for (int i = targetY + 1; i < curY; i++) {
					pieces[i][curX] = 0;
				}
			}
		}

		// Move the piece from current to target
		pieces[targetX][targetY] = pieces[curX][curY];
		
		// Update the pieces array 		
		updateBoardInParse();

		return true;
	}

	public boolean highlight(int x, int y) {
		highlights[x][y] = true;

		return false;
	}

	public boolean cleanHighlights() {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				highlights[x][y] = false;
			}
		}

		return true;
	}

	public boolean cleanBoard() {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				pieces[x][y] = 0;
			}
		}

		return true;
	}
	
	public void createBoardInParse(){
		for( int i = 0; i < 8; i++ ){
			for( int j = 0; j < 8; j++){
				board.put(pieces[i][j]);
			}
		}
		
		ParseObject object = new ParseObject("GameBoards");
		object.put("gameId", gameId);
		object.put("board", board);	
		object.put("situation", 0);
		object.saveInBackground();
	}
	
	public void updateBoardInParse(){
		
		for( int i = 0; i < 8; i++ ){
			for( int j = 0; j < 8; j++){
				board.put(pieces[i][j]);

			}
		}
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		query.getFirstInBackground(new GetCallback<ParseObject>(){

			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				object.put("board", board);
				object.saveInBackground();
			}
		});
	}
	
	public void receiveGameId(){		
		try {
			gameId = ParseCloud.callFunction("receiveGameId", new HashMap<String, Integer>());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Log.d("GAMEID: ", "" + gameId);
	}
	
	public void joinGame( int gameId){
		this.gameId = gameId;
		color = -1;
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		try {
			ParseObject object = query.getFirst();
			object.put("situation",1);
			object.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int checkSituation(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		try {
			ParseObject object = query.getFirst();
			return object.getInt("situation");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 3;
	}
	
	public void deleteGame(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		try {
			ParseObject object = query.getFirst();
			object.delete();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
