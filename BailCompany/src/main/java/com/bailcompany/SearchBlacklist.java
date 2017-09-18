package com.bailcompany;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.ui.BlackListMembers;
import com.bailcompany.utils.Utils;

public class SearchBlacklist extends CustomActivity {
	EditText name, dob, city;
	TextView search;
	private Calendar cal;
	private int day;
	private int month;
	private int year;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_blacklist);
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		name = (EditText) findViewById(R.id.name);
		dob = (EditText) findViewById(R.id.dob);
		city = (EditText) findViewById(R.id.city);
		search = (TextView) findViewById(R.id.btn_search);
		setActionBar();
		dob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				DateDialog(dob);

			}
		});
		setTouchNClick(R.id.btn_search);
	}

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.search_black));
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		if (v.getId() == R.id.btn_search) {
			hideKeyboard();
			if (!name.getText().toString().equalsIgnoreCase("")
					|| !city.getText().toString().equalsIgnoreCase("")
					|| !dob.getText().toString().equalsIgnoreCase("")) {
				BlackListMembers.name2 = name.getText().toString();
				BlackListMembers.city2 = city.getText().toString();
				BlackListMembers.dob2 = dob.getText().toString();
				setResult(RESULT_OK);
				finish();
			} else {
				Utils.showDialog(this, "Please fill atleast any field");
			}
		}

	}

	void hideKeyboard() {
		View view = THIS.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) THIS
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void DateDialog(final EditText fieldView) {

		OnDateSetListener listener = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				monthOfYear = monthOfYear + 1;
				if (monthOfYear == 11 || monthOfYear == 12 || monthOfYear == 10) {

					if (dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3
							|| dayOfMonth == 4 || dayOfMonth == 5
							|| dayOfMonth == 6 || dayOfMonth == 7
							|| dayOfMonth == 8 || dayOfMonth == 9) {
						// fieldView.setText(year + "-" + monthOfYear + "-0"
						// + dayOfMonth);
						fieldView.setText(year + "-" + monthOfYear + "-"
								+ dayOfMonth);
					} else {
						// fieldView.setText(year + "-" + monthOfYear + "-"
						// + dayOfMonth);
						fieldView.setText(year + "-" + monthOfYear + "-"
								+ dayOfMonth);
					}
				} else {
					if (dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3
							|| dayOfMonth == 4 || dayOfMonth == 5
							|| dayOfMonth == 6 || dayOfMonth == 7
							|| dayOfMonth == 8 || dayOfMonth == 9) {
						// fieldView.setText(year + "-0" + monthOfYear + "-0"
						// + dayOfMonth);
						fieldView.setText(year + "-" + monthOfYear + "-"
								+ dayOfMonth);
					} else {
						// fieldView.setText(year + "-0" + monthOfYear + "-"
						// + dayOfMonth);
						fieldView.setText(year + "-" + monthOfYear + "-"
								+ dayOfMonth);
					}
				}
			}

		};

		DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year,
				month, day);
		dpDialog.show();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

}
