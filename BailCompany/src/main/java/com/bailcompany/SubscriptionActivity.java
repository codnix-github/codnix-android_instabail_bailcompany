package com.bailcompany;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.utils.Utils;

public class SubscriptionActivity extends CustomActivity implements
		OnCheckedChangeListener {
	Button continuePayment;
	RadioButton subcribe, dispatch;
	public static boolean dispatchType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscription);
		setActionBar();
		subcribe = (RadioButton) findViewById(R.id.subscribe);
		dispatch = (RadioButton) findViewById(R.id.dispatch);
		dispatch.setOnCheckedChangeListener(this);
		subcribe.setOnCheckedChangeListener(this);
		continuePayment = (Button) findViewById(R.id.continue_pay);
		continuePayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (subcribe.isChecked() || dispatch.isChecked()) {
					startActivity(new Intent(SubscriptionActivity.this,
							Subscription2Activity.class));
				} else {
					Utils.showDialog(SubscriptionActivity.this,
							"Please select any option");
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideKeyboard();
		if (!MainActivity.user.getPackageId().equalsIgnoreCase("0")) {
			finish();
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

	@Override
	public void onBackPressed() {

	}

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.subscribe));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}

}
