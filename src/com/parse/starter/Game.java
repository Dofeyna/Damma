package com.parse.starter;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Game {

	/*
	 * for pieces array : 0 = empty -1 = red -2 = red dama 1 = black 2 = black
	 * dama
	 */

	int gameId = -1;
	int[][] pieces;	
	boolean[][] highlights;
	int selectedX, selectedY, targetX, targetY;
	int color;
	int turn;

	public void initialize() {
		// initialize variables
		pieces = new int[8][8];
		highlights = new boolean[8][8];
		turn = 0;
	}

	public void newGame() {
		// Creator is black
		color = 1;
		turn = 1;

		// Receive the gameId from the Parse
		receiveGameId();

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

		// UPDATE PARSE DATABASE
		createBoardInParse();
		
	}

	public boolean checkPath(int x, int y) {
		selectedX = x;
		selectedY = y;

		// Check Path of Men
		if (pieces[x][y] * color == 1) {

			// Check Right
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

			// Check Left
			if (y > 0) {
				if (pieces[x][y - 1] * pieces[x][y] <= 0) {
					if (pieces[x][y - 1] == 0) {
						highlight(x, y - 1);
					} else {
						if (y > 1) {
							if (pieces[x][y - 2] == 0) {
								highlight(x, y - 2);
							}
						}
					}
				}
			}

			// Check Up
			if (x < 7) {
				if (pieces[x + 1][y] * pieces[x][y] <= 0) {
					if (pieces[x + 1][y] == 0) {
						if (color == -1) {
							highlight(x + 1, y);
						}
					} else {
						if (x < 6) {
							if (pieces[x + 2][y] == 0) {
								if (color == -1) {
									highlight(x + 2, y);
								}
							}
						}
					}
				}
			}
			
			// Check Down
			if( x > 0 ){
				if( pieces[x-1][y] * pieces[x][y] <= 0 ){
					if( pieces[x-1][y] == 0 ){
						if( color == 1 ){
							highlight(x-1,y);
						}
					} else{
						if( x > 1 ){
							if( pieces[x-2][y] == 0 ){
								if( color == 1 ){
									highlight(x-2,y);
								}
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

	public boolean makeMove(int targetX, int targetY) {

		// Remove captured pieces
		if (targetY == selectedY) {
			if (targetX > selectedX) {
				for (int i = selectedX + 1; i < targetX; i++) {
					pieces[i][selectedY] = 0;
				}
			} else {
				for (int i = targetX + 1; i < selectedX; i++) {
					pieces[i][selectedY] = 0;
				}
			}
		} else {
			if (targetY > selectedY) {
				for (int j = selectedY + 1; j < targetY; j++) {
					pieces[selectedX][j] = 0;
				}
			} else {
				for (int j = targetY + 1; j < selectedY; j++) {
					pieces[selectedX][j] = 0;
				}
			}
		}

		// Move the piece from current to target
		pieces[targetX][targetY] = pieces[selectedX][selectedY];
		pieces[selectedX][selectedY] = 0;
		
		turn = (-1) * turn;

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

	public void createBoardInParse() {
		
		JSONArray board = new JSONArray();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board.put(pieces[i][j]);
			}
		}

		ParseObject object = new ParseObject("GameBoards");
		object.put("gameId", gameId);
		object.put("board", board);
		object.put("situation", 0);
		object.put("turn", turn);
		ParseACL acl = new ParseACL();
		acl.setPublicReadAccess(true);
		acl.setPublicWriteAccess(true);
		object.setACL(acl);
		try {
			object.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateBoardInParse() {
		
		JSONArray board = new JSONArray();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board.put(pieces[i][j]);

			}
		}

		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		try {
			ParseObject object = query.getFirst();
			object.put("board", board);
			object.put("turn", turn);
			object.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateBoardFromParse() {
		
		JSONArray board = new JSONArray();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);

		try {
			ParseObject object = query.getFirst();
			turn = object.getInt("turn");
			board = object.getJSONArray("board");

			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					try {
						pieces[i][j] = board.getInt(i * 8 + j);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void receiveGameId() {
		try {
			gameId = ParseCloud.callFunction("receiveGameId",
					new HashMap<String, Integer>());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void joinGame(int gameId) {
		this.gameId = gameId;
		color = -1;
		turn = 1;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		try {
			ParseObject object = query.getFirst();
			object.put("situation", 1);
			object.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateBoardFromParse();
	}

	public int checkSituation() {
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

	public void deleteGame() {
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
	
	public boolean updateWhenTurn(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameBoards");
		query.whereEqualTo("gameId", gameId);
		try {
			ParseObject object = query.getFirst();
			if( object.getInt("turn") == color ){
				turn = color;
				updateBoardFromParse();
				return true;
			}
			return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void warnOpponent(){
		HashMap<String, Object> params = new HashMap<String, Object>();
		if( color == -1 ){
			params.put("channel", "game" + gameId + "b");
		}else{
			params.put("channel", "game" + gameId + "r");
		}
		
		try {
			ParseCloud.callFunction("warnOpponent", params);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
