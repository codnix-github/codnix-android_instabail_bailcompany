package com.bailcompany;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressWarnings("deprecation")
public class HistoryRequestList extends CustomActivity {

	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key, apiUrl;
	public static ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
	static int getCallTimeout = 50000;
	private ListView incomingRequestList;
	boolean isSender;
	public static String title;
	int page = 0;
	boolean loadingMore;
	IncomingListAdapter adapter;
	public static boolean IsBailRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_request_list);
		title = getIntent().getStringExtra("title");
		if (title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
			isSender = true;
			IsBailRequest = false;
			apiUrl = WebAccess.GET_SENT_BOND_REQUESTS_HISTORY;
		} else if (title.equalsIgnoreCase("My Accepted Transfer Bond Requests")) {
			apiUrl = WebAccess.GET_ACCEPTED_BOND_REQUESTS_HISTORY;
			isSender = false;
			IsBailRequest = false;
		} else if (title.equalsIgnoreCase("My Sent Referral Bail Requests")) {
			isSender = true;
			apiUrl = WebAccess.GET_SENT_REFER_BAIL_REQUESTS_HISTORY;
			IsBailRequest = false;
		} else if (title.equalsIgnoreCase("My Accepted Referral Bail Requests")) {
			apiUrl = WebAccess.GET_ACCEPTED_REFER_BAIL_REQUESTS_HISTORY;
			isSender = false;
			IsBailRequest = false;

		} else if (title.equalsIgnoreCase("All Posting Agent Transactions")) {
			apiUrl = WebAccess.GET_ALL_BAIL_REQUEST_HISTORY;
			isSender = false;
			IsBailRequest = true;

		}
		if (title != null) {
			setActionBar(title);
		}

		getAllRequest();
		incomingRequestList = (ListView) findViewById(R.id.incoming_request_list);
		// incomingRequestList.setAdapter(new IncomingListAdapter());
		adapter = new IncomingListAdapter(HistoryRequestList.this);
		incomingRequestList.setAdapter(adapter);
		incomingRequestList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (title
						.equalsIgnoreCase("My Accepted Referral Bail Requests")
						|| title.equalsIgnoreCase("My Sent Referral Bail Requests")) {
					// if (MainActivity.accessType == 1
					// || MainActivity.accessType == 3) {
					startActivity(new Intent(HistoryRequestList.this,
							HistoryReferRequestDetail.class)
							.putExtra("bail", bailReqList.get(position))
							.putExtra("title", title)
							.putExtra("position", position));
					// } else
					// Utils.showDialog(HistoryRequestList.this,
					// "Denied Acces");
				} else if (title
						.equalsIgnoreCase("My Accepted Transfer Bond Requests")
						|| title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
					// if (MainActivity.accessType == 1
					// || MainActivity.accessType == 3) {
					startActivity(new Intent(HistoryRequestList.this,
							HistoryBondRequestDetail.class)
							.putExtra("bail", bailReqList.get(position))
							.putExtra("title", title)
							.putExtra("position", position));
					// } else
					// Utils.showDialog(HistoryRequestList.this,
					// "Denied Acces");
				} else {
					startActivity(new Intent(HistoryRequestList.this,
							HistoryRequestDetail.class).putExtra("bail",
							bailReqList.get(position)).putExtra("title", title));
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideKeyboard();
		if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent
				|| WebAccess.hireBailAgent || WebAccess.selfAssignAgent) {
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

	protected void setActionBar(String title) {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(title);
	}

	@SuppressLint("InflateParams")
	private class IncomingListAdapter extends BaseAdapter {
		public IncomingListAdapter(Context context) {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bailReqList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return bailReqList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = HistoryRequestList.this.getLayoutInflater()
						.inflate(R.layout.incoming_request_item, null);
			LinearLayout lp = (LinearLayout) convertView.findViewById(R.id.lp);
			if (position % 2 == 0)
				lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
			else
				lp.setBackgroundColor(Color.WHITE);
			if (title.equalsIgnoreCase("My Sent Referral Bail Requests")
					|| title.equalsIgnoreCase("My Sent Transfer Bond Requests")
					|| title.equalsIgnoreCase("All Posting Agent Transactions")) {
				((TextView) convertView.findViewById(R.id.company_name))
						.setText(bailReqList.get(position).getDefendantName()
								+ "");
			} else {
				((TextView) convertView.findViewById(R.id.company_name))
						.setText(bailReqList.get(position)
								.getSenderCompanyName() + "");
			}
			//
			// String date[] = bailReqList.get(position).getCreatedDate()
			// .split(" ");
			// try {
			// Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
			// String date2 = new SimpleDateFormat("MM/dd/yyyy").format(d);
			// ((TextView) convertView.findViewById(R.id.date)).setText(date2);
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			((TextView) convertView.findViewById(R.id.date)).setText(Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
					"MM/dd/yyyy", bailReqList.get(position).getCreatedDate()));
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.image);

			Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
					StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
					bailReqList.get(position).getSenderCompanyImage(),
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
				image.setVisibility(View.GONE);
			image.setImageBitmap(null);
			int i = incomingRequestList.getLastVisiblePosition();
			int j = incomingRequestList.getAdapter().getCount();
			i++;
			if (i == j && !(loadingMore)) {

				page++;

				getAllRequest();

			}
			return convertView;
		}
	}

	void getAllRequest() {
		if (Utils.isOnline(HistoryRequestList.this)) {
			if (page == 0)
				showProgressDialog("");
			if (page == 0) {
				bailReqList.clear();

			}
			loadingMore = true;
			RequestParams param = new RequestParams();

			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("Page", page);

			String url = WebAccess.MAIN_URL + apiUrl;
			client.setTimeout(getCallTimeout);
			client.post(HistoryRequestList.this, url, param,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

							if (page == 0)
								dismissProgressDialog();
							Utils.showDialog(HistoryRequestList.this,
									R.string.err_unexpect);
							loadingMore = false;
							if (page > 0)
								page--;
						}

						@Override
						public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
							if (page == 0) {
								dismissProgressDialog();
								WebAccess.AllBidListCompany.clear();
							}
							try {
								String response2;
								loadingMore = false;
								response2 = new String(responseBody);
								JSONObject resObj;

								resObj = new JSONObject(response2);

								if (resObj != null) {
									if (resObj.optString("status")
											.equalsIgnoreCase("1")) {
										if (!isSender) {
											if (title
													.equalsIgnoreCase("My Accepted Referral Bail Requests"))
												WebAccess
														.getALLReferRequest(response2);
											else
												WebAccess
														.getALLRequest(response2);
										} else {
											if (title
													.equalsIgnoreCase("My Sent Referral Bail Requests"))
												WebAccess
														.getALLReferBailRequestSender(response2);
											else

												WebAccess
														.getALLRequestSender(response2);
										}
										if (bailReqList != null
												&& bailReqList.size() > 0) {
											adapter.notifyDataSetChanged();
										} else {
											Utils.showDialog(
													HistoryRequestList.this,
													"No Bail Request Available")
													.show();
											adapter.notifyDataSetChanged();
										}
									} else if (resObj.optString("status")
											.equalsIgnoreCase("3")) {
										Toast.makeText(
												THIS,
												"Session was closed please login again",
												Toast.LENGTH_LONG).show();
										MainActivity.sp.edit().putBoolean(
												"isFbLogin", false);
										MainActivity.sp.edit()
												.putString("user", null)
												.commit();
										startActivity(new Intent(
												HistoryRequestList.this,
												Launcher.class));
									} else {

										if (page == 0) {
											Utils.showDialog(
													HistoryRequestList.this,
													resObj.optString("message"))
													.show();
											adapter.notifyDataSetChanged();
										} else {
											if (page > 0)
												page--;
										}

									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});

		} else
			Utils.noInternetDialog(HistoryRequestList.this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
}
