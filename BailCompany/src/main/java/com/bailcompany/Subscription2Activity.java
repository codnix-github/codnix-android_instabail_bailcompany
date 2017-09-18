package com.bailcompany;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.PackageModel;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class Subscription2Activity extends CustomActivity implements
		OnCheckedChangeListener {
	Button continue2;
	RadioButton complementory, subscription, flatFee;
	public static int type = -1;
	String key;
	RadioGroup parentGroup;
	static int getCallTimeout = 50000;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	boolean radioChecked;
	public static ArrayList<PackageModel> getALLPackages = new ArrayList<PackageModel>();
	public static ArrayList<String> getAllTypes = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscription2);
		setActionBar();
		parentGroup = (RadioGroup) findViewById(R.id.radio_group);

		// subscription.setOnCheckedChangeListener(this);
		// flatFee.setOnCheckedChangeListener(this);
		// complementory.setOnCheckedChangeListener(this);
		continue2 = (Button) findViewById(R.id.continue2);
		setPackages();
		addViews();
		// getAllPackages();

		continue2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				if (radioChecked) {
					startActivity(new Intent(Subscription2Activity.this,
							SubscriptionPart3Activity.class));
				} else {
					Utils.showDialog(Subscription2Activity.this,
							"Please select any option");
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

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.package2));
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		radioChecked = true;
		if (buttonView.isChecked()) {
			// buttonView.setBackgroundColor(getResources()
			// .getColor(R.color.green));
			type = buttonView.getId();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		hideKeyboard();
		if (!MainActivity.user.getPackageId().equalsIgnoreCase("0")) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(THIS);
		sp.edit().putBoolean("isFbLogin", false);
		sp.edit().putString("user", null).commit();
		Intent intent = new Intent(THIS, Login.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
	}

	public void setPackages() {
		getAllTypes.clear();
		getAllTypes.add("We will dispactch our own agents");
		getAllTypes.add("We will dispatch Insta-Bail agents");
		getAllTypes.add("We will use both");
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getAllPackages() {
		showProgressDialog("");
		RequestParams param = new RequestParams();
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		String url = WebAccess.MAIN_URL + "get-packages";
		client.setTimeout(getCallTimeout);
		client.post(Subscription2Activity.this, url, param,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
						dismissProgressDialog();
						Utils.showDialog(Subscription2Activity.this,
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
									setPackages();
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
											Subscription2Activity.this,
											Launcher.class));
								} else {
									Utils.showDialog(
											Subscription2Activity.this,
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
		for (int i = 0; i < getAllTypes.size(); i++) {
			String model = getAllTypes.get(i);
			View convertView = Subscription2Activity.this.getLayoutInflater()
					.inflate(R.layout.radio_item, null);
			RadioButton rb = (RadioButton) convertView
					.findViewById(R.id.radio_btn);

			rb.setId(i);
			rb.setText(model);
			rb.setPadding(10, 10, 10, 10);
			rb.setOnCheckedChangeListener(Subscription2Activity.this);
			parentGroup.addView(rb);
		}

	}
}
