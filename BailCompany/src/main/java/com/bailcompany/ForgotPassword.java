package com.bailcompany;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ForgotPassword extends CustomActivity {

	private TextView btnSubmit;
	private EditText email;

	static int getCallTimeout = 50000;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_pass);
		btnSubmit = (TextView) findViewById(R.id.btn_submit);
		email = (EditText) findViewById(R.id.email_forgot);
		setTouchNClick(R.id.btn_submit);
		setActionBar();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == btnSubmit) {
			hideKeyboard();
			if (Utils.isValidEmail(email.getText().toString())) {
				if (Utils.isOnline(THIS))
					doForgotPassword(email.getText().toString());
				else
					Utils.noInternetDialog(THIS);
			} else
				Utils.showDialog(THIS, "Please enter valid email !");
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideKeyboard();
	}

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.forgot_title));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	// private void doForgotPassword(final String email) {
	// showProgressDialog("");
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// response = WebAccess.forgotPassword(email);
	// THIS.runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// dismissProgressDialog();
	// if (!Commons.isEmpty(response)
	// && response.equalsIgnoreCase("done")) {
	// Utils.showDialog(THIS, R.string.pass_sent,
	// new OnClickListener() {
	//
	// @Override
	// public void onClick(
	// DialogInterface dialog,
	// int which) {
	// finish();
	//
	// }
	// });
	//
	// } else if (!Commons.isEmpty(response)
	// && response.equalsIgnoreCase("invalid"))
	// Utils.showDialog(THIS, R.string.pass_invalid);
	// else
	// Utils.showDialog(THIS, R.string.err_unexpect);
	//
	// }
	// });
	//
	// }
	// }).start();
	// }

	private void doForgotPassword(final String email) {
		showProgressDialog("");
		RequestParams param = new RequestParams();

		param.put("UserEmail", email);

		String url = WebAccess.MAIN_URL + WebAccess.FORGOT_PASSWORD;
		client.setTimeout(getCallTimeout);

		client.post(this, url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

				dismissProgressDialog();

			}

			@SuppressWarnings("unused")
			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				try {
					dismissProgressDialog();
					String response2;
					response2 = new String(responseBody);
					if (response2 != null) {
						JSONObject json;

						json = new JSONObject(response2);
						message = json.optString("message");

						Utils.showDialog(ForgotPassword.this, message);

					} else
						Utils.showDialog(ForgotPassword.this,
								R.string.err_unexpect);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

}
