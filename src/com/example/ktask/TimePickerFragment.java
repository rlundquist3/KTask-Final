/*
 * This class is a custom time picker fragment
 * 
 * Written by: Keaton Adams and Riley Lundquist
 * Date: November 18, 2013
 * 
 */

package com.example.ktask;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;

@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	Add parent;
	
	public TimePickerFragment(Add add) {
		parent = add;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get calendar info
		final Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		int len = String.valueOf(minute).length();
		Log.w("LENGTH", String.valueOf(len));
		if (len == 1)
		{
			String temp = "0" + String.valueOf(minute);
			Log.w("TEMP", temp);
			minute = Integer.parseInt(temp);
			Log.w("MINUTE LOOP:", String.valueOf(minute));
		}
		
		parent.remindTimeSet(hourOfDay, minute);
		Log.w("MINUTE:", String.valueOf(minute));

	}
	
}
