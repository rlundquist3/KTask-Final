/*
 * This class is a custom adapter for navigating through the tasks.
 * 
 * Written by: Keaton Adams and Riley Lundquist
 * Date: November 18, 2013
 * 
 */
package com.example.ktask;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

public class TaskCursorAdapter extends SimpleCursorAdapter {

	OnTouchListener touch;
	
	public TaskCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags, OnTouchListener touch) {
		super(context, layout, cursor, from, to, flags);
		this.touch = touch;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		LinearLayout row = (LinearLayout) view.findViewById(R.id.title_row);
		row.setOnTouchListener(touch);
		
		return view;
	}
}
