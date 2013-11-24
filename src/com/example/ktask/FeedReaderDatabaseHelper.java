package com.example.ktask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ktask.FeedReaderHelper.FeedEntry;

public class FeedReaderDatabaseHelper extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
					FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_COMPLETE + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_DUE + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_REMIND + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + ")";
	
	private static final String SQL_DELETE_ENTRIES = 
			"DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
	
	//If you change your schema you must increment the DB version
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "my_db.db";
	
	public FeedReaderDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
	
	public void onDownGrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
