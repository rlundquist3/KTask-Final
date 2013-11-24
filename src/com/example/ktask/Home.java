/*
 * This class serves as the center for the application. It contains all the active tasks as well as several navigation options for the user.
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

public class Home extends ListActivity implements OnItemClickListener, OnTouchListener {

	// Initialize several variables
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
			FeedEntry.COLUMN_NAME_TITLE,
			FeedEntry.COLUMN_NAME_REMIND
	};
	int[] toViews = {R.id.row_title, R.id.row_due};

	// Called when this Activity is started
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set up database
		SQLiteDatabase db = dbHelper.getReadableDatabase();        
		Cursor cursor = db.query(FeedEntry.TABLE_NAME, PROJECTION, FeedEntry.COLUMN_NAME_COMPLETE + "=?", new String[] {"false"}, null, null, FeedEntry.COLUMN_NAME_REMIND + " DESC");
		// Use a custom adapter
		adapter = new TaskCursorAdapter(this, R.layout.title_row, cursor, fromColumns, toViews, 0, this);    
		// Set ListView
		list = getListView();
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(adapter);
		// Set cursor to load all non-completed objects in the database
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
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}    
	// Set navigation/menu bar at the top of the screen
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			onClickAdd(this);
			return true;
		case R.id.show_complete:
			onShowComplete(this);
			return true;
			//		case R.id.delete_all:
			//			deleteAll(this);
			//			return true;
		case R.id.action_help:
			onClickHelp(this);
			return true;
		default:
			return false;
		}
	}

	// Show the completed tasks to the user (change activities)
	private void onShowComplete(Home home) {
		Intent completeIntent = new Intent (this, Completed.class);
		startActivity(completeIntent);
	}
	// Take the user to the help screen
	private void onClickHelp(Activity activity)
	{
		Intent help_intent = new Intent(this, Help.class);
		startActivity(help_intent);
	}

	//	// Delete all tasks from the database
	//    private void deleteAll(Home home) {
	//    	SQLiteDatabase db = dbHelper.getWritableDatabase();
	//		db.delete(FeedEntry.TABLE_NAME, null, null);
	//		onCreate(null);
	//	}

	// Take the user to the add screen
	public void onClickAdd(Activity activity) {
		Intent addIntent = new Intent (this, Add.class);
		startActivity(addIntent);
	}
	
	//Method for checkbox completion
	public void onChecked(View view) {
		//Intentionally left empty
	}
	
	// Override onItemClick method for each task
	@Override
	public void onItemClick(AdapterView<?> adapt, View view, int position, long id) {
		view.setOnTouchListener(this);
		CheckedTextView task = (CheckedTextView) view;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		Long dbId = ids.get(task.getText().toString());
		// Item was completed
		values.put(FeedEntry.COLUMN_NAME_COMPLETE, completed.contains(task.getText().toString()));
		// Query to database
		int affected = db.update(FeedEntry.TABLE_NAME, values, FeedEntry._ID + "=" + dbId, null);
	}
	// Override onTouch method for each task
	@SuppressLint("ResourceAsColor")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		// Get layout elements for the ListView
		LinearLayout task = (LinearLayout) view;
		CheckBox check = (CheckBox) task.getChildAt(0);
		TextView text = (TextView) task.getChildAt(1);
		float currentPos = event.getX();
		String textStr = (String) text.getText();
		Long id = ids.get(textStr);
		int action = MotionEventCompat.getActionMasked(event);
		switch(action) {
		case (MotionEvent.ACTION_DOWN) :
			System.out.println("Action was DOWN");
		task.setBackgroundColor(android.graphics.Color.argb(100, 60, 102, 49));
		lastPos = currentPos;
		// Implement different possible actions
		return true;
		case (MotionEvent.ACTION_MOVE) :
		return true;
		case (MotionEvent.ACTION_UP) :
		task.setBackgroundColor(android.R.color.background_light);
		// Swipe action. Set so the user has to swipe the task 1/10 of the size of the screen
		if (currentPos > lastPos + view.getWidth()/10) {
			Intent editIntent = new Intent (this, Add.class);
			editIntent.putExtra(EXTRA_ID, id.toString());
			startActivity(editIntent);
		}
		else {
			// Item was checked
			check.setChecked(!check.isChecked());
			ContentValues values = new ContentValues();
			values.put(FeedEntry.COLUMN_NAME_COMPLETE, Boolean.toString(check.isChecked()));
			int affected = db.update(FeedEntry.TABLE_NAME, values, FeedEntry._ID + "=" + id, null);
			// Refresh the screen since one of the tasks was updated
			finish();
			startActivity(getIntent());
		}
		// Implement more possible actions
		return true;
		case (MotionEvent.ACTION_CANCEL) :
		task.setBackgroundColor(android.R.color.background_light);
		return true;
		case (MotionEvent.ACTION_OUTSIDE) :
		return true;      
		default : 
			// Close the database and set onTouchEvent
			db.close();
			return super.onTouchEvent(event);
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
		startActivity(getIntent());
	}
}

