package com.bailcompany;

import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.Payment;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PaymentInfo extends CustomActivity {
	EditText nameOnCard, creditCardNo, cardType, cardExpiredDate, cvv,
			nameOnAcc, accType, accNo, routingNo;
	private TextView disclaimer, save;
	private Calendar cal;
	private int day;
	private int month;
	private int year;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	static ProgressDialog pd;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	Payment payment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_info);
		nameOnCard = (EditText) findViewById(R.id.name);
		creditCardNo = (EditText) findViewById(R.id.card_number);
		cardExpiredDate = (EditText) findViewById(R.id.eod);
		cardType = (EditText) findViewById(R.id.type);
		disclaimer = (TextView) findViewById(R.id.disclaimer);
		cvv = (EditText) findViewById(R.id.cvv);
		nameOnAcc = (EditText) findViewById(R.id.name_account);
		accType = (EditText) findViewById(R.id.account_types);
		accNo = (EditText) findViewById(R.id.account_number);
		routingNo = (EditText) findViewById(R.id.routing_number);
		save = (TextView) findViewById(R.id.btn_save);
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		setActionBar();
		disclaimer.setText(Html
				.fromHtml("<b>Disclaimer :</b> <font color=#7a7a7a>"
						+ getString(R.string.disclaimer) + ""));
		getPaymentInfo();
		cardExpiredDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				DateDialog(cardExpiredDate);

			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				if (isValid()) {
					updatePayment();
				}
			}
		});
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

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.payment_info));
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
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					} else {
						// fieldView.setText(year + "-" + monthOfYear + "-"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					}
				} else {
					if (dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3
							|| dayOfMonth == 4 || dayOfMonth == 5
							|| dayOfMonth == 6 || dayOfMonth == 7
							|| dayOfMonth == 8 || dayOfMonth == 9) {
						// fieldView.setText(year + "-0" + monthOfYear + "-0"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					} else {
						// fieldView.setText(year + "-0" + monthOfYear + "-"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					}
				}
			}

		};

		DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year,
				month, day);
		dpDialog.show();

	}

	void getPaymentInfo() {
		if (Utils.isOnline()) {
			showProgressDialog("");
			RequestParams param = new RequestParams();
			param.put("UserName", MainActivity.user.getUsername());
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());

			String url = WebAccess.MAIN_URL + WebAccess.GET_PAYMENT_INFO;
			client.setTimeout(getCallTimeout);
			client.post(this, url, param, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


					dismissProgressDialog();
					Utils.showDialog(PaymentInfo.this, R.string.err_unexpect);
				}

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
					dismissProgressDialog();
					try {
						String response2;
						response2 = new String(responseBody);
						JSONObject resObj = new JSONObject(response2);

						if (resObj != null) {
							message = resObj.optString("message");
							if (resObj.optString("status")
									.equalsIgnoreCase("1")) {
								payment = WebAccess.getPayment(response2);
								if (payment != null) {
									showDetail();
								}
							} else if (resObj.optString("status")
									.equalsIgnoreCase("3")) {
								Toast.makeText(
										THIS,
										"Session was closed please login again",
										Toast.LENGTH_LONG).show();
								MainActivity.sp.edit().putBoolean("isFbLogin",
										false);
								MainActivity.sp.edit().putString("user", null)
										.commit();
								startActivity(new Intent(PaymentInfo.this,
										Login.class));
							} else {
								Utils.showDialog(PaymentInfo.this, message);
							}
						}
					} catch (JSONException e) {
						Utils.showDialog(PaymentInfo.this,
								R.string.err_unexpect);
						e.printStackTrace();
					}
				}

			});
		} else {
			Utils.noInternetDialog(THIS);
		}
	}

	void updatePayment() {
		if (Utils.isOnline()) {
			showProgressDialog("");
			RequestParams param = new RequestParams();
			param.put("UserName", MainActivity.user.getUsername());
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("NameOnCard", nameOnCard.getText().toString());
			param.put("CardType", cardType.getText().toString());
			param.put("CardNumber", creditCardNo.getText().toString());
			param.put("CVV", cvv.getText().toString());
			param.put("CardExpiry", cardExpiredDate.getText().toString());
			param.put("NameOnAccount", nameOnAcc.getText().toString());
			param.put("AccountType", accType.getText().toString());
			param.put("AccountNumber", accNo.getText().toString());
			param.put("RoutingNumber", routingNo.getText().toString());

			String url = WebAccess.MAIN_URL
					+ WebAccess.GET_UPDATE_COMPANY_PAYMENT;
			client.setTimeout(getCallTimeout);
			client.post(this, url, param, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					dismissProgressDialog();
					Utils.showDialog(PaymentInfo.this, R.string.err_unexpect);
				}

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
					dismissProgressDialog();
					try {
						String response2;
						response2 = new String(responseBody);
						JSONObject resObj = new JSONObject(response2);

						if (resObj != null) {
							message = resObj.optString("message");
							if (resObj.optString("status")
									.equalsIgnoreCase("1")) {
								finish();

							} else if (resObj.optString("status")
									.equalsIgnoreCase("3")) {
								Toast.makeText(
										THIS,
										"Session was closed please login again",
										Toast.LENGTH_LONG).show();
								MainActivity.sp.edit().putBoolean("isFbLogin",
										false);
								MainActivity.sp.edit().putString("user", null)
										.commit();
								startActivity(new Intent(PaymentInfo.this,
										Login.class));
							} else {
								Utils.showDialog(PaymentInfo.this, message);
							}
						}
					} catch (JSONException e) {
						Utils.showDialog(PaymentInfo.this,
								R.string.err_unexpect);
						e.printStackTrace();
					}
				}

			});
		} else {
			Utils.noInternetDialog(THIS);
		}
	}

	public void showDetail() {
		nameOnCard.setText(payment.getNameOnCard());
		creditCardNo.setText(payment.getCardNumber());
		cardExpiredDate.setText(payment.getExDate());
		cardType.setText(payment.getCardType());
		cvv.setText(payment.getCvv());
		nameOnAcc.setText(payment.getNameOnAccount());
		accType.setText(payment.getAccType());
		accNo.setText(payment.getAccNumber());
		routingNo.setText(payment.getRoutingNumber());
	}

	private boolean isValid() {

		if (nameOnCard.getText().toString().length() == 0) {
			Utils.showDialog(THIS, "Please enter Name on card !");
			return false;
		} else {
			if (creditCardNo.getText().toString().length() < 16)
				Utils.showDialog(THIS,
						"Please enter 16 digit credit card number");
			if (cardType.getText().length() == 0)
				Utils.showDialog(THIS, "Please enter card type");
			else if (routingNo.getText().toString().length() == 0)
				Utils.showDialog(THIS, "Please enter Rounting Number");
			else if (cardExpiredDate.getText().toString().equalsIgnoreCase(""))
				Utils.showDialog(THIS, "Please Select Expired date of card");
			else if (cvv.getText().toString().length() == 0
					|| cvv.getText().toString().length() < 4)
				Utils.showDialog(THIS, "Please enter ccv of 4 digit");
			else if (nameOnAcc.getText().toString().length() == 0)
				Utils.showDialog(THIS, "Please enter Name on Account");
			else if (accType.getText().toString().length() == 0)
				Utils.showDialog(THIS, "Please enter Acconut Type");
			else if (accNo.getText().toString().length() == 0)
				Utils.showDialog(THIS, "Please enter Account Number");
			else if (routingNo.getText().toString().length() == 0)
				Utils.showDialog(THIS, "Please enter Rounting Number");

			else
				return true;

		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
}
