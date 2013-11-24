/*
 * This class is the screen for showing the user the completed tasks.
 * 
 * Written by: Keaton Adams and Riley Lundquist
 * Date: November 18, 2013
 * 
 */
package com.example.ktask;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.ktask.FeedReaderHelper.FeedEntry;

public class Completed extends ListActivity implements OnItemClickListener, OnTouchListener {
	// Initialize variables
	private FeedReaderDatabaseHelper dbHelper = new FeedReaderDatabaseHelper(this);
	SimpleCursorAdapter adapter, adapter2;
	static final String[] PROJECTION = {
		FeedEntry._ID,
		FeedEntry.COLUMN_NAME_TITLE,
		FeedEntry.COLUMN_NAME_COMPLETE,
		FeedEntry.COLUMN_NAME_DUE,
		FeedEntry.COLUMN_NAME_REMIND,
		FeedEntry.COLUMN_NAME_DESCRIPTION
	};
	float lastPos;
	HashMap<String, Long> ids = new HashMap<String, Long>();
	ArrayList<String> completed = new ArrayList<String>();
	public static final String EXTRA_ID = "com.example.ktask.ID";
	ListView list, completeList;
	String[] fromColumns = {
			FeedEntry.COLUMN_NAME_TITLE
	};
	int[] toViews = {R.id.completed_title};
	// Called when the Activity starts
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set up database
		SQLiteDatabase db = dbHelper.getReadableDatabase();               
		Cursor cursor = db.query(FeedEntry.TABLE_NAME, PROJECTION, FeedEntry.COLUMN_NAME_COMPLETE + "=?", new String[] {"true"}, null, null, FeedEntry.COLUMN_NAME_DUE + " DESC");
		adapter = new TaskCursorAdapter(this, R.layout.completed_row, cursor, fromColumns, toViews, 0, this); 
		// Set ListView
		list = getListView();
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(adapter);       
		// Query the database for all completed tasks
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			long id = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
			String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
			ids.put(title, id);        	
			boolean complete = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_COMPLETE)));
			if (complete)
				completed.add(title);        	
			cursor.moveToNext();
		}
		// Close the database        
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.completed, menu);
		return true;
	}    
	// Set navigation bar for the user
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			onClickAdd(this);
			return true;
			//		case R.id.delete_all:
			//			deleteAll(this);
			//			return true;
		case R.id.action_help:
			onClickHelp(this);
			return true;
		case R.id.action_home:
			onClickHome(this);
			return true;
		default:
			return false;
		}
	}	
	// Take user to help screen
	private void onClickHelp(Activity activity)
	{
		Intent help_intent = new Intent(this, Help.class);
		startActivity(help_intent);
	}
	// Delete all tasks
	//    private void deleteAll(Completed completedActivity) {
	//    	SQLiteDatabase db = dbHelper.getWritableDatabase();
	//		db.delete(FeedEntry.TABLE_NAME, null, null);
	//		onCreate(null);
	//	}
	// Take user to add screen
	public void onClickAdd(Activity activity) {
		Intent addIntent = new Intent (this, Add.class);
		startActivity(addIntent);
	}
	// Take user to home screen
	private void onClickHome(Activity activity)
	{
		Intent home_intent = new Intent(this, Home.class);
		startActivity(home_intent);
	}
	// Implement onChecked
	public void onChecked(View view) {
		//Intentionally left blank
	}
	// Override onItemClick method
	@Override
	public void onItemClick(AdapterView<?> adapt, View view, int position, long id) {
		// Set listener
		view.setOnTouchListener(this);
		CheckedTextView task = (CheckedTextView) view;
		// Query to database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		Long dbId = ids.get(task.getText().toString());
		values.put(FeedEntry.COLUMN_NAME_COMPLETE, completed.contains(task.getText().toString()));
		int affected = db.update(FeedEntry.TABLE_NAME, values, FeedEntry._ID + "=" + dbId, null);
	}
	// Override onTouch; called when an item is clicked or swiped by the user
	@SuppressLint("ResourceAsColor")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// Initialize database and layout elements
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		LinearLayout task = (LinearLayout) view;
		CheckBox check = (CheckBox) task.getChildAt(0);
		TextView text = (TextView) task.getChildAt(1);
		float currentPos = event.getX();
		String textStr = (String) text.getText();
		Long id = ids.get(textStr);
		int action = MotionEventCompat.getActionMasked(event);
		// Implement actions
		switch(action) {
		case (MotionEvent.ACTION_DOWN) :
		task.setBackgroundColor(android.graphics.Color.argb(100, 60, 102, 49));
		lastPos = currentPos;
		return true;
		case (MotionEvent.ACTION_MOVE) :
		return true;
		case (MotionEvent.ACTION_UP) :
		task.setBackgroundColor(android.R.color.background_light);
		// Allow user to edit when a task is swiped
		if (currentPos > lastPos + view.getWidth()/10) {
			Intent editIntent = new Intent (this, Add.class);
			editIntent.putExtra(EXTRA_ID, id.toString());
			startActivity(editIntent);
		}
		// Item is clicked by the user
		else {
			check.setChecked(!check.isChecked());
			ContentValues values = new ContentValues();
			values.put(FeedEntry.COLUMN_NAME_COMPLETE, Boolean.toString(check.isChecked()));
			int affected = db.update(FeedEntry.TABLE_NAME, values, FeedEntry._ID + "=" + id, null);
			// Refresh screen since an item was edited
			finish();
			startActivity(getIntent());
		}
		// Implement more actions
		return true;
		case (MotionEvent.ACTION_CANCEL) :
		task.setBackgroundColor(android.R.color.background_light);
		return true;
		case (MotionEvent.ACTION_OUTSIDE) :
		return true;      
		default : 
			// Close database and set onTouchEvent
			db.close();
			return super.onTouchEvent(event);
		}
	}
}

