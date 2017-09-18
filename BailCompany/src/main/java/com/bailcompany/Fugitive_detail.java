package com.bailcompany;

import java.util.ArrayList;

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
import android.net.Uri;
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
import com.bailcompany.model.AgentModel;
import com.bailcompany.model.FugitiveRequestModel;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class Fugitive_detail extends CustomActivity {
	static FugitiveRequestModel bm;
	TextView bid, daysLeft;
	int pos;
	static Bitmap bmDef, bmAgent;
	ImageView imgCompany, agentCompany;
	String status;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	static ProgressDialog pd;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fugitive_detail);
		bid = (TextView) findViewById(R.id.btnCheckin);
		daysLeft = (TextView) findViewById(R.id.days_left);
		setActionBar();
		if (getIntent().hasExtra("req")) {
			bm = (FugitiveRequestModel) getIntent().getSerializableExtra("req");
			pos = getIntent().getIntExtra("position", 0);
			showDetail();
		} else {
			Utils.showDialog(Fugitive_detail.this, "No detail found").show();
		}
		bid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				if (bm != null) {
					String AgentId = bm.getAgentId();
					String accept = "0";
					if (!AgentId.equalsIgnoreCase("")) {
						accept = "1";
					}
					Intent intent = new Intent(Fugitive_detail.this,
							BidingActivity.class);
					intent.putExtra("Accepted", accept);
					intent.putExtra("req", bm);
					intent.putExtra("position", pos);
					startActivity(intent);

				}
			}
		});
		imgCompany.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				F1.newInstance().show(getFragmentManager(), null);

			}
		});
	}

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.fugitive_detail));
	}

	private void showDetail() {

		if (!bm.getAgentId().equalsIgnoreCase("") && bm.getAgentId() != null) {
			((LinearLayout) findViewById(R.id.sender_company))
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.company_header))
					.setVisibility(View.VISIBLE);
			status = "Accepted";
		} else {
			status = "pending";
		}
		((TextView) findViewById(R.id.defAddr)).setText(bm.getLocation());
		((TextView) findViewById(R.id.defName)).setText(bm.getDefendantName());
		((TextView) findViewById(R.id.cell_no)).setText(bm.getCellNumber());
		((TextView) findViewById(R.id.phone_num)).setText(bm.getHomeNumber());
		((TextView) findViewById(R.id.warrant_num)).setText(bm
				.getDefBookingNumber());

		TextView defAddr = (TextView) findViewById(R.id.defAddr);
		defAddr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double latitude = Double.parseDouble(bm.getLocationLatitude());
				double longitude = Double.parseDouble(bm.getLocationLongitude());
				String label = Utils.getFormattedText(bm.getLocation());
				String uriBegin = "geo:" + latitude + "," + longitude;
				String query = latitude + "," + longitude + "(" + label + ")";
				String encodedQuery = Uri.encode(query);
				String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
				Uri uri = Uri.parse(uriString);
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		daysLeft.setText(bm.getDayLeftBeforeBailSeized());
		TextView gender = (TextView) findViewById(R.id.gender_text);
		if (bm.getIsFemale().equalsIgnoreCase("1")) {
			gender.setText("Female");
		} else {
			gender.setText("Male");
		}
		((TextView) findViewById(R.id.defName)).setText(bm.getDefendantName());
		TextView str = ((TextView) findViewById(R.id.paymentStatus));

		((TextView) findViewById(R.id.splInstruction)).setText(bm
				.getInstructionForAgent() + "");
		if (bm.getAmountCompanyWillToPay().equalsIgnoreCase("0")) {
			str.setText(bm.getAmountorPercentage() + "%");
		} else {
			str.setText("$" + bm.getAmountorPercentage());
		}
		LinearLayout llWarrant = (LinearLayout) findViewById(R.id.llWarrant);

		ArrayList<WarrantModel> wList = bm.getWarrantList();
		if (wList != null && wList.size() > 0) {
			int count = 0;
			for (WarrantModel wMod : wList) {
				View v = getLayoutInflater()
						.inflate(R.layout.row_warrant, null);
				if (count == 0)
					v.findViewById(R.id.divider).setVisibility(View.GONE);
				if (wMod != null) {
					((TextView) v.findViewById(R.id.wrntAmount))
							.setText("Forfeiture Amount:   $"
									+ wMod.getAmount());
					((TextView) v.findViewById(R.id.wrntTownship))
							.setText("Township:   " + wMod.getTownship() + "");

					Log.e("wMod.getAmount()", "" + wMod.getAmount());
					Log.e("wMod.getTownship()", "" + wMod.getTownship());

					llWarrant.addView(v);
				}
				count++;
			}
		}
		imgCompany = (ImageView) findViewById(R.id.img_company2);
		Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
				StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
				bm.getDefendantImage(), new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap bm) {
						Log.d("Bitmap", bm == null ? "Null Bitmap"
								: "Valid Bitmap");
						if (bm != null) {
							bmDef = ImageUtils.getCircularBitmap(bm);
							imgCompany.setImageBitmap(ImageUtils
									.getCircularBitmap(bm));
						}
					}
				});
		if (bmp != null)
			imgCompany.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
		else
			imgCompany.setImageBitmap(null);
		if (status.equalsIgnoreCase("Accepted")) {
			((TextView) findViewById(R.id.temp))
					.setText(bm.getAgentName() + "");

			agentCompany = (ImageView) findViewById(R.id.img_company);
			Bitmap bmp2 = new ImageLoader(StaticData.getDIP(100),
					StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
					bm.getAgentImage(), new ImageLoadedListener() {

						@Override
						public void imageLoaded(Bitmap bm) {
							Log.d("Bitmap", bm == null ? "Null Bitmap"
									: "Valid Bitmap");
							if (bm != null) {
								bmAgent = ImageUtils.getCircularBitmap(bm);
								agentCompany.setImageBitmap(ImageUtils
										.getCircularBitmap(bm));
							}
						}
					});
			if (bmp2 != null)
				agentCompany.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
			else
				agentCompany.setImageBitmap(null);

			((LinearLayout) findViewById(R.id.sender_company))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							getAgentDetail();

						}
					});
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

	private void getAgentDetail() {
		showProgressDialog("");
		RequestParams param = new RequestParams();

		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		param.put("agentid", bm.getAgentId());
		String url = WebAccess.MAIN_URL + WebAccess.GET_AGENT_DETAIL;
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
				Utils.showDialog(Fugitive_detail.this, R.string.err_unexpect);

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
							AgentModel model = WebAccess.getAgent(response2);
							if (model != null)
								startActivity(new Intent(THIS,
										AgentProfile.class).putExtra("agent",
										model));
						} else if (resObj.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(THIS,
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(Fugitive_detail.this,
									Login.class));
						} else {
							Utils.showDialog(Fugitive_detail.this,
									resObj.optString("message"));
						}
					}
				} catch (JSONException e) {
					Utils.showDialog(Fugitive_detail.this,
							R.string.err_unexpect);
					e.printStackTrace();
				}

			}

		});

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

			bm.setImageBitmap(bmDef);

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

}
