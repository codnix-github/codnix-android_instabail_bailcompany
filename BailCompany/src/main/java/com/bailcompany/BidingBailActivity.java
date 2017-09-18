package com.bailcompany;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.CompanyBidingModel;
import com.bailcompany.model.User;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BidingBailActivity extends CustomActivity {
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	ArrayList<CompanyBidingModel> ReqList = new ArrayList<CompanyBidingModel>();

	static int getCallTimeout = 50000;
	private ListView incomingRequestList;
	private BailRequestModel bm;
	String res = "0";
	int pos;
	IncomingListAdapter adapter;
	static Bitmap bitmap;
	TextView chatBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_biding_bail);
		bm = (BailRequestModel) getIntent().getSerializableExtra("req");
		res = getIntent().getStringExtra("Accepted");
		pos = getIntent().getIntExtra("position", 0);

		ReqList = WebAccess.AllBidListCompany.get(pos);
		incomingRequestList = (ListView) findViewById(R.id.incoming_request_list_bail);
		adapter = new IncomingListAdapter(this);
		incomingRequestList.setAdapter(adapter);
		setActionBar();

	}

	private void getCompanyDetail(String id) {
		showProgressDialog("");
		RequestParams param = new RequestParams();

		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		param.put("companyid", id);
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
				Utils.showDialog(BidingBailActivity.this, R.string.err_unexpect);

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
							User user = WebAccess.getCompanyResponse(response2);
							startActivity(new Intent(THIS, CompanyProfile.class)
									.putExtra("user", user));
						} else {
							Utils.showDialog(BidingBailActivity.this,
									resObj.optString("message"));
						}
					}
				} catch (JSONException e) {
					Utils.showDialog(BidingBailActivity.this,
							R.string.err_unexpect);
					e.printStackTrace();
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
		actionBar.setTitle(getString(R.string.biding));
	}

	@SuppressLint("InflateParams")
	private class IncomingListAdapter extends BaseAdapter {
		public IncomingListAdapter(Context context) {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ReqList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ReqList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null)
				convertView = BidingBailActivity.this.getLayoutInflater()
						.inflate(R.layout.biding_item_company, null);
			RelativeLayout lp = (RelativeLayout) convertView
					.findViewById(R.id.lp);
			if (position % 2 == 0)
				lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
			else
				lp.setBackgroundColor(Color.WHITE);

			((TextView) convertView.findViewById(R.id.textView1))
					.setText(ReqList.get(position).getCompanyName());
			((TextView) convertView.findViewById(R.id.textView3))
					.setText("Amount/Percentage: "
							+ ReqList.get(position).getAmount());
			((TextView) convertView.findViewById(R.id.textView4))
					.setText(ReqList.get(position).getDescription());

			final ImageView image = (ImageView) convertView
					.findViewById(R.id.imageView1);
			image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					hideKeyboard();
					bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
					F1.newInstance().show(getFragmentManager(), null);

				}
			});
			lp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					hideKeyboard();
					CompanyBidingModel biding = ReqList.get(position);
					getCompanyDetail(biding.getCompanyId());
				}
			});
			Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
					StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
					ReqList.get(position).getCompanyImage(),
					new ImageLoadedListener() {

						@Override
						public void imageLoaded(Bitmap bm) {
							Log.d("Bitmap", bm == null ? "Null Bitmap"
									: "Valid Bitmap");
							if (bm != null)
								image.setImageBitmap(ImageUtils
										.getCircularBitmap(bm));
						}
					});
			if (bmp != null)
				image.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
			else
				image.setImageBitmap(null);

			Button Accept = (Button) convertView.findViewById(R.id.button1);
			if (ReqList.get(position).getAcceptedStatus() != null
					&& ReqList.get(position).getAcceptedStatus()
							.equalsIgnoreCase("1")) {
				// if (ReqList.get(position).getAcceptedStatus()
				// .equalsIgnoreCase("1")) {
				Accept.setText("Accepted");
				Accept.setEnabled(false);
				res = "Accepted";
				// }
			} else {
				if (!bm.getSenderCompanyId().equalsIgnoreCase("")
						&& bm.getSenderCompanyId() != null) {
					Accept.setVisibility(View.GONE);
				} else {
					Accept.setText("Accept");
					Accept.setEnabled(true);
				}
				res = "";
			}
			// if (res.equalsIgnoreCase("Accepted")) {
			// String status = "In Progress";
			// if (fm.getIsComplete() != null
			// && fm.getIsComplete().equalsIgnoreCase("1")) {
			// status = "Completed";
			// } else if (fm.getIsCancel() != null
			// && fm.getIsCancel().equalsIgnoreCase("1")) {
			// if (fm.getIsAbort() != null
			// && fm.getIsAbort().equalsIgnoreCase("1")) {
			// status = "Aborted";
			// }
			// status = "Canceled";
			// }
			// ((TextView) convertView.findViewById(R.id.textView5))
			// .setText("Status: " + status);
			// } else {
			// ((TextView) convertView.findViewById(R.id.textView5))
			// .setVisibility(View.VISIBLE);
			// }
			Accept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					hideKeyboard();
					sendAcceptanceToCompany(position);
				}
			});
			return convertView;
		}
	}

	void sendAcceptanceToCompany(final int position) {
		if (Utils.isOnline()) {
			showProgressDialog("");
			RequestParams param = new RequestParams();
			String bidId = ReqList.get(position).getRequestId();
			param.put("TransferBondId", bidId);
			param.put("UserName", MainActivity.user.getUsername());
			param.put("CompanyId", ReqList.get(position).getCompanyId());
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());

			param.put("LocationLongitude", bm.getLocationLatitude());
			param.put("LocationLatitude", bm.getLocationLongitude());

			String url = WebAccess.MAIN_URL + WebAccess.ACCEPT_BOND_REQUEST;
			client.setTimeout(getCallTimeout);
			client.post(this, url, param, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					dismissProgressDialog();
					Utils.showDialog(BidingBailActivity.this,
							R.string.err_unexpect);
				}

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
					dismissProgressDialog();
					String response2;
					response2 = new String(responseBody);
					JSONObject json;
					try {
						json = new JSONObject(response2);
						if (json.optString("status").equalsIgnoreCase("1")) {
							ReqList.get(position).setAcceptedStatus("1");
							res = "1";
							WebAccess.AllBidListCompany.set(position, ReqList);
							bm.setSenderCompanyId(ReqList.get(position)
									.getCompanyId());
							bm.setSenderCompanyName(ReqList.get(position)
									.getCompanyName());
							bm.setSenderCompanyImage(ReqList.get(position)
									.getCompanyImage());
							adapter.notifyDataSetChanged();
							Utils.showDialog(BidingBailActivity.this,
									json.optString("message")).show();
						} else if (json.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(THIS,
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(BidingBailActivity.this,
									Login.class));
						} else {
							Utils.showDialog(BidingBailActivity.this,
									json.optString("message")).show();
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});
		} else {
			Utils.noInternetDialog(THIS);
		}
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

			bm.setImageBitmap(bitmap);

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

}
