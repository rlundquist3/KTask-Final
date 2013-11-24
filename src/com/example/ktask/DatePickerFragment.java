/*
 * This class is a custom date picker fragment
 * 
 * Written by: Keaton Adams and Riley Lundquist
 * Date: November 18, 2013
 * 
 */

package com.example.ktask;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	Add parent;
	String type;
	
	public DatePickerFragment(Add add, String type) {
		parent = add;
		this.type = type;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get calendar info
		final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		if (type.equals("due"))
			parent.dueDateSet(year, monthOfYear, dayOfMonth);
		else if (type.equals("remind"))
			parent.remindDateSet(year, monthOfYear, dayOfMonth);
	}

}
