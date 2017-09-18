package com.bailcompany;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BadDebtMember;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.User;
import com.bailcompany.ui.MainFragment;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BadDetail extends CustomActivity {
	private BadDebtMember bm;
	String comp;
	String reqId;
	TextView submit;
	boolean fromNotification = false;
	MainFragment m = new MainFragment();
	String currentDateTimeString, reqDateTimeString;
	static ProgressDialog pd;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	int crnthour, reqthour;
	int crntminute, reqtminute;
	BailRequestModel mod = new BailRequestModel();
	int j = 0;
	ImageView imgCompany;
	ImageView imgdef;
	static Bitmap bmCompany, bmDef;
	static boolean isCompany;
	LinearLayout layoutCompany;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bad_detail);
		setActionBar();
		layoutCompany = (LinearLayout) findViewById(R.id.linear_1);
		if (getIntent().hasExtra("badDebt")) {
			bm = (BadDebtMember) getIntent().getSerializableExtra("badDebt");
			showDetail();
		} else {
			Utils.showDialog(BadDetail.this, "No detail found").show();
		}
		imgCompany.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				isCompany = true;
				F1.newInstance().show(getFragmentManager(), null);

			}
		});
		imgdef.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				isCompany = false;
				F1.newInstance().show(getFragmentManager(), null);

			}
		});
		layoutCompany.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				getCompanyDetail();
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
		actionBar.setTitle(getString(R.string.bad_deb_detail));
	}

	@SuppressLint("SimpleDateFormat")
	private void showDetail() {

		((TextView) findViewById(R.id.defAddr)).setText(bm.getAddress());
		((TextView) findViewById(R.id.def_cotxt)).setText(bm
				.getDefendentOrCosigner());
		((TextView) findViewById(R.id.amount_ownedtxt)).setText("$"
				+ bm.getAmountOwed());


//		try {
//			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(bm.getDOB());
//
//			Date d3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(bm
//					.getCreatedDateTime());
//			String date = new SimpleDateFormat("MM/dd/yyyy").format(d);
//			String date3 = new SimpleDateFormat("MM/dd/yyyy hh:mm").format(d3);
//			((TextView) findViewById(R.id.dobtxt)).setText(date);
//			((TextView) findViewById(R.id.date_timetxt)).setText(date3);
//					
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		((TextView) findViewById(R.id.dobtxt)).setText(Utils.getRequiredDateFormatGMT("yyyy-MM-dd","MM/dd/yyyy", bm.getDOB()));
		((TextView) findViewById(R.id.date_timetxt)).setText(Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss","MM/dd/yyyy hh:mm", bm
				.getCreatedDateTime()));
		((TextView) findViewById(R.id.teltxt)).setText(bm.getTelephone());
		((TextView) findViewById(R.id.temp)).setText(bm.getCompanyName());
		((TextView) findViewById(R.id.temp2)).setText(bm.getName());
		imgCompany = (ImageView) findViewById(R.id.img_company1);
		imgdef = (ImageView) findViewById(R.id.img_company2);
		// loadImage(imgCompany, bm.getCompanyPicture());
		Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
				StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
				bm.getCompanyPicture(), new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap bm) {
						Log.d("Bitmap", bm == null ? "Null Bitmap"
								: "Valid Bitmap");
						if (bm != null) {
							bmCompany = ImageUtils.getCircularBitmap(bm);
							imgCompany.setImageBitmap(ImageUtils
									.getCircularBitmap(bm));

						}

					}
				});
		if (bmp != null)
			imgCompany.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
		else
			imgCompany.setImageBitmap(null);
		Bitmap bmp2 = new ImageLoader(StaticData.getDIP(100),
				StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
				bm.getPicture(), new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap bm) {
						Log.d("Bitmap", bm == null ? "Null Bitmap"
								: "Valid Bitmap");
						if (bm != null) {
							bmDef = ImageUtils.getCircularBitmap(bm);
							imgdef.setImageBitmap(ImageUtils
									.getCircularBitmap(bm));

						}

					}
				});
		if (bmp2 != null)
			imgdef.setImageBitmap(ImageUtils.getCircularBitmap(bmp2));
		else
			imgdef.setImageBitmap(null);

	}

	public static class F1 extends DialogFragment {

		public static F1 newInstance() {
			F1 f1 = new F1();
			f1.setStyle(DialogFragment.STYLE_NO_FRAME,
					android.R.style.Theme_DeviceDefault_Dialog);
			return f1;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// Remove the default background
			getDialog().getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));

			// Inflate the new view with margins and background
			View v = inflater.inflate(R.layout.popup_layout, container, false);
			ImageView bm = (ImageView) v.findViewById(R.id.bm_company);
			if (isCompany)
				bm.setImageBitmap(bmCompany);
			else
				bm.setImageBitmap(bmDef);
			// Set up a click listener to dismiss the popup if they click
			// outside
			// of the background view
			v.findViewById(R.id.popup_root).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dismiss();
						}
					});

			return v;
		}
	}

	private void getCompanyDetail() {
		showProgressDialog("");
		RequestParams param = new RequestParams();

		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		param.put("companyid", bm.getCompanyId());
		String url = WebAccess.MAIN_URL + WebAccess.GET_COMPANY_DETAIL;
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
				Utils.showDialog(BadDetail.this, R.string.err_unexpect);
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
							user = WebAccess.getCompanyResponse(response2);
							startActivity(new Intent(THIS, CompanyProfile.class)
									.putExtra("user", user));
						} else if (resObj.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(THIS,
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(BadDetail.this,
									Login.class));
						} else {
							Utils.showDialog(BadDetail.this,
									resObj.optString("message"));
						}
					}
				} catch (JSONException e) {
					Utils.showDialog(BadDetail.this, R.string.err_unexpect);
					e.printStackTrace();
				}

			}

		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
}
