package com.music.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchHistoryDBHelper extends SQLiteOpenHelper{

	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "mp3.db";
	
	public SearchHistoryDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
//		System.out.println("cancel a table---------->");
	}
	
	public SearchHistoryDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
//		System.out.println("table---------->");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("create a new table---------->");
		db.execSQL("CREATE TABLE IF NOT EXISTS search_history_word" +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, key VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
	}	
}
