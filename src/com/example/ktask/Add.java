/*
 * This class is the screen for adding a new task.
 * 
 * Written by: Keaton Adams and Riley Lundquist
 * Date: November 18, 2013
 * 
 */

package com.example.ktask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ktask.FeedReaderHelper.FeedEntry;

public class Add extends FragmentActivity {
	// Initialize variables
	private FeedReaderDatabaseHelper dbHelper = new FeedReaderDatabaseHelper(this);
	String dueMonth = "", dueDay = "", dueYear = "";
	String remMonth = "", remDay = "", remYear = "", remHour = "", remMin = "", remAmPm = "";
	boolean dueSet = false, remindSet = false;
	long id;
	// Called when Activity begins
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);		
		// Get the intent and store the id
		Intent intent = getIntent();		
		try {
			id = Long.parseLong(intent.getStringExtra(Home.EXTRA_ID));
			populate(id);
		} catch (NumberFormatException e) {
			id = -1;
		}
		
	}
	
	// Take user to help screen
	private void onClickHelp(Activity activity)
	{
		Intent help_intent = new Intent(this, Help.class);
		startActivity(help_intent);
	}
	
	// Take  user to screen to show completed tasks
	private void onShowComplete(Add add) {
		Intent completeIntent = new Intent (this, Completed.class);
    	startActivity(completeIntent);
	}
	// Get info from the database if the user is editing an existing task
	private void populate(long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = {
				FeedEntry._ID,
				FeedEntry.COLUMN_NAME_TITLE,
				FeedEntry.COLUMN_NAME_DUE,
				FeedEntry.COLUMN_NAME_REMIND,
				FeedEntry.COLUMN_NAME_DESCRIPTION
		};
		
		EditText title = (EditText) findViewById(R.id.title_text);
		//TextView due = (TextView) findViewById(R.id.due_display);
		TextView remind = (TextView) findViewById(R.id.remind_display);
		EditText description = (EditText) findViewById(R.id.description_text);
		
		Cursor cursor = db.query(FeedEntry.TABLE_NAME, projection, FeedEntry._ID + "=" + id, null, null, null, FeedEntry.COLUMN_NAME_DUE + " DESC");
		
		cursor.moveToFirst();
		title.setText(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)));
//		String dueString = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DUE));
//		if (dueString != null)
//				due.setText(dueString);
		String remindString = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_REMIND));
		if (remindString != null)
			remind.setText(remindString);
		description.setText(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DESCRIPTION)));
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }  
	// Set navigation bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.show_complete:
			onShowComplete(this);
			return true;
		case R.id.action_help:
			onClickHelp(this);
			return true;
		case R.id.action_home:
			onClickGoHome(this);
			return true;
		default:
			return false;
		}
	}
	// Take user to add screen
	public void onClickGoHome(Activity activity) {
		Intent homeIntent = new Intent (this, Add.class);
		startActivity(homeIntent);
	}
	// Save the task to the database when the user clicks the Save button
	public void onClickHome(View view) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		EditText title = (EditText) findViewById(R.id.title_text);
		EditText description = (EditText) findViewById(R.id.description_text);
		
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_TITLE, title.getText().toString());
		values.put(FeedEntry.COLUMN_NAME_COMPLETE, "false");
		if (dueSet)
			values.put(FeedEntry.COLUMN_NAME_DUE, "Due: " + dueMonth + " " + dueDay);
		values.put(FeedEntry.COLUMN_NAME_DESCRIPTION, description.getText().toString());
		if (remindSet)
			values.put(FeedEntry.COLUMN_NAME_REMIND, remMonth + " " + remDay + ", " + remHour + ":" + remMin + " " + remAmPm);
		
		long newRowId;
		int affected;
		
		if (id == -1)
			newRowId = db.insert(FeedEntry.TABLE_NAME, FeedEntry.COlUMN_NAME_NULLABLE, values);
		else
			affected = db.update(FeedEntry.TABLE_NAME, values, FeedEntry._ID + "=" + id, null);
		
		Intent home_intent = new Intent(this, Home.class);
		startActivity(home_intent);
		
		db.close();
	}
	// Allow user to select a due time
	public void showTimePickerDialog(View view) {
		DialogFragment timeFragment = new TimePickerFragment(this);
		timeFragment.show(getSupportFragmentManager(), "timePicker");
	}
	// Allow user to select a due date
	public void showDatePickerDialog(View view, String type) {
		DialogFragment dateFragment = new DatePickerFragment(this, type);
		dateFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
//	public void dueDialog(View view) {
//		showDatePickerDialog(view, "due");
//	}
	// Print output to user
	public void reminderDialog(View view) {
		showTimePickerDialog(view);
		showDatePickerDialog(view, "remind");
	}
	// Due date formatting
	public void dueDateSet(int year, int monthOfYear, int dayOfMonth) {
		switch (monthOfYear) {
		case 0:
			dueMonth = "Jan.";
			break;
		case 1:
			dueMonth = "Feb.";
			break;
		case 2:
			dueMonth = "March";
			break;
		case 3:
			dueMonth = "April";
			break;
		case 4:
			dueMonth = "May";
			break;
		case 5:
			dueMonth = "June";
			break;
		case 6:
			dueMonth = "July";
			break;
		case 7:
			dueMonth = "Aug.";
			break;
		case 8:
			dueMonth = "Sept.";
			break;
		case 9:
			dueMonth = "Oct.";
			break;
		case 10:
			dueMonth = "Nov.";
			break;
		case 11:
			dueMonth = "Dec.";
			break;
		default:
			break;
		}
		dueYear = Integer.toString(year);
		dueDay = Integer.toString(dayOfMonth);
		dueSet = true;
		
		//TextView dueDisplay = (TextView) findViewById(R.id.due_display);
		//dueDisplay.setText("Due: " + dueMonth + " " + dueDay);
	}
	// Due time formatting
	public void remindTimeSet(int hourOfDay, int minute) {
		if (hourOfDay > 12)
		{
			remAmPm = "PM";
			hourOfDay -= 12;
		}
		else
			remAmPm = "AM";
		remHour = Integer.toString(hourOfDay);
		remMin = Integer.toString(minute);
		if (minute < 10)
			remMin = "0" + remMin;
		remindSet = true;
		
		TextView remDisplay = (TextView) findViewById(R.id.remind_display);
		remDisplay.setText(remMonth + " " + remDay + ", " + remHour + ":" + remMin + " " + remAmPm);
		
		
	}
	// More due date formatting
	public void remindDateSet(int year, int monthOfYear, int dayOfMonth) {
		switch (monthOfYear) {
		case 0:
			remMonth = "Jan.";
			break;
		case 1:
			remMonth = "Feb.";
			break;
		case 2:
			remMonth = "March";
			break;
		case 3:
			remMonth = "April";
			break;
		case 4:
			remMonth = "May";
			break;
		case 5:
			remMonth = "June";
			break;
		case 6:
			remMonth = "July";
			break;
		case 7:
			remMonth = "Aug.";
			break;
		case 8:
			remMonth = "Sept.";
			break;
		case 9:
			remMonth = "Oct.";
			break;
		case 10:
			remMonth = "Nov.";
			break;
		case 11:
			remMonth = "Dec.";
			break;
		default:
			break;
		}
		remYear = Integer.toString(year);
		remDay = Integer.toString(dayOfMonth);
	}
}
