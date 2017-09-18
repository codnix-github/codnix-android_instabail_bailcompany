package com.bailcompany;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.PackageModel;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class SubscriptionPart3Activity extends CustomActivity implements
		OnCheckedChangeListener {

	Button pay1;
	RadioButton monthly, flat50, flat100;
	TextView monthlyDetail, monthlyPrice;
	TextView flat50Detail, flat50Price;
	TextView flat100Detail, flat100Price;
	public ArrayList<PackageModel> getALLPackages = new ArrayList<PackageModel>();
	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	private static final String TAG = "InstaBailPayment";
	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
	// note that these credentials will differ between live & sandbox
	// environments.
	private static final String CONFIG_CLIENT_ID = "credential from developer.paypal.com";

	private static final int REQUEST_CODE_PAYMENT = 1;
	private static PayPalConfiguration config = new PayPalConfiguration()
			.environment(CONFIG_ENVIRONMENT)
			.clientId(CONFIG_CLIENT_ID)
			// The following are only used in PayPalFuturePaymentActivity.
			.merchantName("Example Merchant")
			.merchantPrivacyPolicyUri(
					Uri.parse("https://www.example.com/privacy"))
			.merchantUserAgreementUri(
					Uri.parse("https://www.example.com/legal"));
	PackageModel model;
	int addOn1 = 0;
	int addOn2 = 0;
	static ProgressDialog pd;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	RelativeLayout parent;
	int packageNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar();
		setContentView(R.layout.activity_subscription_part3);
		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		startService(intent);
		parent = (RelativeLayout) findViewById(R.id.parent);
		pay1 = (Button) findViewById(R.id.subscribe);
		monthly = (RadioButton) findViewById(R.id.radio_btn);
		flat50 = (RadioButton) findViewById(R.id.radio_btn2);
		flat100 = (RadioButton) findViewById(R.id.radio_btn3);
		monthlyDetail = (TextView) findViewById(R.id.textView3);
		monthlyPrice = (TextView) findViewById(R.id.textView2);
		flat50Detail = (TextView) findViewById(R.id.detail);
		flat50Price = (TextView) findViewById(R.id.amnt);
		flat100Detail = (TextView) findViewById(R.id.detail2);
		flat100Price = (TextView) findViewById(R.id.amnt2);
		monthly.setOnCheckedChangeListener(this);
		flat50.setOnCheckedChangeListener(this);
		flat100.setOnCheckedChangeListener(this);
		getALLPackages.clear();
		getAllPackages();
		pay1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				if (monthly.isChecked()) {
					if (getALLPackages.get(0).getPackagePrice()
							.equalsIgnoreCase("0")
							|| getALLPackages.get(0).getPackagePrice()
									.equalsIgnoreCase(""))
						updatePackageMember(null);
					else
						showDialog();
					packageNo = 0;
				} else if (flat100.isChecked()) {
					if (getALLPackages.get(1).getPackagePrice()
							.equalsIgnoreCase("0")
							|| getALLPackages.get(1).getPackagePrice()
									.equalsIgnoreCase(""))
						updatePackageMember(null);
					else
						showDialog();
					packageNo = 1;
				} else if (flat50.isChecked()) {
					if (getALLPackages.get(2).getPackagePrice()
							.equalsIgnoreCase("0")
							|| getALLPackages.get(2).getPackagePrice()
									.equalsIgnoreCase(""))
						updatePackageMember(null);
					else
						showDialog();
					packageNo = 2;
				} else {
					Utils.showDialog(SubscriptionPart3Activity.this,
							"Please checked any Package");
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
	public void onDestroy() {
		// Stop service when done
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
	}

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.package_detail));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	private PayPalOAuthScopes getOauthScopes() {
		/*
		 * create the set of required scopes Note: see
		 * https://developer.paypal.com
		 * /docs/integration/direct/identity/attributes/ for mapping between the
		 * attributes you select for this app in the PayPal developer portal and
		 * the scopes required here.
		 */
		Set<String> scopes = new HashSet<String>(Arrays.asList(
				PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL,
				PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
		return new PayPalOAuthScopes(scopes);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm = data
						.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Log.i(TAG, confirm.toJSONObject().toString(4));
						Log.i(TAG, confirm.getPayment().toJSONObject()
								.toString(4));
						/**
						 * TODO: send 'confirm' (and possibly
						 * confirm.getPayment() to your server for verification
						 * or consent completion. See
						 * https://developer.paypal.com
						 * /webapps/developer/docs/integration
						 * /mobile/verify-mobile-payment/ for more details.
						 * 
						 * For sample mobile backend interactions, see
						 * https://github
						 * .com/paypal/rest-api-sdk-python/tree/master
						 * /samples/mobile_backend
						 */
						JSONObject jsobj = confirm.toJSONObject();
						Toast.makeText(
								getApplicationContext(),
								"PaymentConfirmation info received from PayPal",
								Toast.LENGTH_LONG).show();
						updatePackageMember(jsobj);
					} catch (JSONException e) {
						Log.e(TAG, "an extremely unlikely failure occurred: ",
								e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i(TAG, "The user canceled.");
			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i(TAG,
						"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
			}
		}
	}

	public void onBuyPressed(String amount) {
		/*
		 * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
		 * Change PAYMENT_INTENT_SALE to - PAYMENT_INTENT_AUTHORIZE to only
		 * authorize payment and capture funds later. - PAYMENT_INTENT_ORDER to
		 * create a payment for authorization and capture later via calls from
		 * your server.
		 * 
		 * Also, to include additional payment details and an item list, see
		 * getStuffToBuy() below.
		 */

		PayPalPayment thingToBuy = getThingToBuy(
				PayPalPayment.PAYMENT_INTENT_SALE, amount);
		// PayPalPayment thingToBuy =
		// getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
		/*
		 * See getStuffToBuy(..) for examples of some available payment options.
		 */

		Intent intent = new Intent(SubscriptionPart3Activity.this,
				PaymentActivity.class);

		// send the same configuration for restart resiliency
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

		startActivityForResult(intent, REQUEST_CODE_PAYMENT);

	}

	private PayPalPayment getThingToBuy(String paymentIntent, String payment) {
		return new PayPalPayment(new BigDecimal(payment), "USD",
				"Subscription Fee", paymentIntent);
	}

	public void showDialog() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("Subscription");
		builder1.setMessage("Are you sure to Pay with Paypal");
		builder1.setCancelable(true);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						model = getALLPackages.get(packageNo);
						onBuyPressed(model.getPackagePrice());
					}
				});
		builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	private void updatePackageMember(JSONObject jsonObj) {
		showProgressDialog("Updating Profile.... ");
		RequestParams param = new RequestParams();
		if (jsonObj != null) {
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("PackageId", model.getPackageID());
			param.put("AddonOne", addOn1);
			param.put("AddonTwo", addOn2);
			param.put("TransactionDetails", jsonObj.toString());
			param.put("CompanyAgentType", Subscription2Activity.type + 1);
			param.put("SubscriptionType", Subscription2Activity.type + 1);

		} else {
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("PackageId", "FREE");
			param.put("AddonOne", addOn1);
			param.put("AddonTwo", addOn2);
			param.put("TransactionDetails", "");
			param.put("CompanyAgentType", Subscription2Activity.type + 1);
			param.put("SubscriptionType", Subscription2Activity.type + 1);
		}
		String url = WebAccess.MAIN_URL + WebAccess.GET_UPDATE_MEMBER_PACKAGE;
		client.setTimeout(getCallTimeout);

		client.post(this, url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

				dismissProgressDialog();
				Utils.showDialog(SubscriptionPart3Activity.this,
						R.string.err_unexpect);

			}

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				dismissProgressDialog();
				try {
					String response2;
					response2 = new String(responseBody);
					JSONObject resObj;
					resObj = new JSONObject(response2);
					if (resObj != null) {
						if (resObj.optString("status").equalsIgnoreCase("1")) {
							Toast.makeText(SubscriptionPart3Activity.this,
									resObj.optString("message"),
									Toast.LENGTH_SHORT).show();
							MainActivity.user.setPackageId(model.getPackageID());
							MainActivity.user.setAddonOneEnabled(addOn1 + "");
							MainActivity.user.setAddonTwoEnabled(addOn2 + "");
							MainActivity.user.setPackageExpired("");
							finish();
						} else if (resObj.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(THIS,
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(
									SubscriptionPart3Activity.this, Login.class));
						} else {
							Utils.showDialog(SubscriptionPart3Activity.this,
									resObj.optString("message"));
						}
					}
				} catch (JSONException e) {
					Utils.showDialog(SubscriptionPart3Activity.this,
							R.string.err_unexpect);
					e.printStackTrace();
				}

			}

		});

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getAllPackages() {
		showProgressDialog("");
		RequestParams param = new RequestParams();
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		String url = WebAccess.MAIN_URL + "get-packages";
		client.setTimeout(getCallTimeout);
		client.post(SubscriptionPart3Activity.this, url, param,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
						dismissProgressDialog();
						Utils.showDialog(SubscriptionPart3Activity.this,
								R.string.err_unexpect);
					}

					@Override
					public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

						dismissProgressDialog();
						try {
							String response2;
							response2 = new String(responseBody);
							JSONObject resObj;

							resObj = new JSONObject(response2);

							if (resObj != null) {
								if (resObj.optString("status")
										.equalsIgnoreCase("1")) {
									getALLPackages = WebAccess
											.getALLPackages(response2);
									addViews();
								} else if (resObj.optString("status")
										.equalsIgnoreCase("3")) {
									Toast.makeText(
											THIS,
											"Session was closed please login again",
											Toast.LENGTH_LONG).show();
									MainActivity.sp.edit().putBoolean(
											"isFbLogin", false);
									MainActivity.sp.edit()
											.putString("user", null).commit();
									startActivity(new Intent(
											SubscriptionPart3Activity.this,
											Launcher.class));
								} else {
									Utils.showDialog(
											SubscriptionPart3Activity.this,
											resObj.optString("status"));
								}
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});

	}

	public void addViews() {
		if (getALLPackages.size() > 2) {
			parent.setVisibility(View.VISIBLE);
			PackageModel model = getALLPackages.get(0);
			monthly.setText(model.getPackageName());
			monthlyDetail.setText(model.getPackageDiscription());
			monthlyPrice.setText("$" + model.getPackagePrice());
			PackageModel model2 = getALLPackages.get(1);
			flat50.setText(model2.getPackageName());
			flat50Detail.setText(model2.getPackageDiscription());
			flat50Price.setText("$" + model2.getPackagePrice());
			PackageModel model3 = getALLPackages.get(2);
			flat100.setText(model3.getPackageName());
			flat100Detail.setText(model3.getPackageDiscription());
			flat100Price.setText("$" + model3.getPackagePrice());
		} else {
			Utils.showDialog(SubscriptionPart3Activity.this,
					"Package will not load or No package exist");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (buttonView.isChecked()) {
			if (buttonView.getId() == R.id.radio_btn) {
				monthly.setChecked(true);
				flat50.setChecked(false);
				flat100.setChecked(false);
			} else if (buttonView.getId() == R.id.radio_btn2) {
				monthly.setChecked(false);
				flat50.setChecked(true);
				flat100.setChecked(false);
			} else if (buttonView.getId() == R.id.radio_btn3) {
				monthly.setChecked(false);
				flat50.setChecked(false);
				flat100.setChecked(true);
			}
		}

	}
}
