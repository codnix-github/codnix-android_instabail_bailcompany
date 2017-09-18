package com.bailcompany.ui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.HistoryRequestList;
import com.bailcompany.IncomingRequestDetail;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
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

@SuppressLint("InflateParams")
public class IncomingRequest extends CustomFragment {
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	public static ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
	static int getCallTimeout = 50000;
	private ListView incomingRequestList;
	int page = 0;
	boolean loadingMore;
	IncomingListAdapter adapter;
	public static boolean incomTranferBond;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.incoming_request, null);
		getAllRequest();
		incomingRequestList = (ListView) v
				.findViewById(R.id.incoming_request_list);

		adapter = new IncomingListAdapter(getActivity());
		incomingRequestList.setAdapter(adapter);
		incomingRequestList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				bailReqList.get(position).setRead("1");
				setReadStatus(bailReqList.get(position).getAgentRequestId(),
						position);
				startActivityForResult(
						new Intent(getActivity(), IncomingRequestDetail.class)
								.putExtra("bail", bailReqList.get(position))
								.putExtra("position", position), 4444);
				// startActivity(new Intent(getActivity(),
				// IncomingRequestDetail.class).putExtra("bail",
				// bailReqList.get(position)));
			}
		});
		setHasOptionsMenu(true);
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		incomingRequestList.setAdapter(adapter);
		if (resultCode == Activity.RESULT_OK) {
			getFragmentManager().popBackStack();
			MainActivity.drawerLayout.openDrawer(MainActivity.drawerLeft);
			// if (data != null) {
			// BailRequestModel brMod = (BailRequestModel) data
			// .getSerializableExtra("bail");
			// locLatt = data.getStringExtra("locLatt");
			// locLng = data.getStringExtra("locLng");
			// if (brMod != null) {
			// WebAccess.agentRequestId = String.valueOf(brMod
			// .getAgentRequestId());
			// startActivity(new Intent(getActivity(), FindBestAgent.class)
			// .putExtra("agents", MainActivity.agentList)
			// .putExtra("reqid", brMod.getAgentRequestId())
			// .putExtra("locLatt", locLatt)
			// .putExtra("locLng", locLng));
			//
			// }
			// }
		}
	}

	@SuppressLint("InflateParams")
	public class IncomingListAdapter extends BaseAdapter {
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
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.incoming_request_item, null);
			LinearLayout lp = (LinearLayout) convertView.findViewById(R.id.lp);
			if (position % 2 == 0)
				lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
			else
				lp.setBackgroundColor(Color.WHITE);

			((TextView) convertView.findViewById(R.id.company_name))
					.setText(bailReqList.get(position).getSenderCompanyName()
							+ "");
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
			((TextView) convertView.findViewById(R.id.date)).setText(Utils
					.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
							"MM/dd/yyyy", bailReqList.get(position)
									.getCreatedDate()));
			if (bailReqList.get(position).getRead().equalsIgnoreCase("0")) {
				ImageView badge = (ImageView) convertView
						.findViewById(R.id.new_badge);
				badge.setVisibility(View.VISIBLE);
			} else {
				ImageView badge = (ImageView) convertView
						.findViewById(R.id.new_badge);
				badge.setVisibility(View.GONE);
			}
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.image);
			// Picasso.with(getActivity())
			// .load(bailReqList.get(position).getSenderCompanyImage())
			// .placeholder(R.drawable.logo1).error(R.drawable.logo1)
			// .into(image);

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
		if (Utils.isOnline(getActivity())) {
			if (page == 0)
				showProgressDialog("");
			RequestParams param = new RequestParams();
			if (page == 0) {
				bailReqList.clear();
				WebAccess.AllBidListCompany.clear();
				HistoryRequestList.IsBailRequest = false;
			}
			loadingMore = true;
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("Page", page);
			String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_BOND_REQUESTS;
			client.setTimeout(getCallTimeout);
			client.post(getActivity(), url, param,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
							if (page == 0)
								dismissProgressDialog();
							Utils.showDialog(getActivity(),
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
										incomTranferBond = true;
										WebAccess.getALLRequest(response2);
										if (bailReqList != null
												&& bailReqList.size() > 0) {
											adapter.notifyDataSetChanged();
										} else {
											Utils.showDialog(getActivity(),
													"No Bail Request Available")
													.show();
											adapter.notifyDataSetChanged();
										}
									} else if (resObj.optString("status")
											.equalsIgnoreCase("3")) {
										Toast.makeText(
												getActivity(),
												"Session was closed please login again",
												Toast.LENGTH_LONG).show();
										MainActivity.sp.edit().putBoolean(
												"isFbLogin", false);
										MainActivity.sp.edit()
												.putString("user", null)
												.commit();
										startActivity(new Intent(getActivity(),
												Launcher.class));
									} else {

										if (page == 0) {
											Utils.showDialog(getActivity(),
													"No Bail Request Available")
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
			Utils.noInternetDialog(getActivity());
	}

	void setReadStatus(int reqId, final int position) {
		if (Utils.isOnline(getActivity())) {
			RequestParams param = new RequestParams();

			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("RequestId", reqId);

			String url = WebAccess.MAIN_URL + WebAccess.MARK_REQUEST_READ;
			client.setTimeout(getCallTimeout);
			client.post(getActivity(), url, param,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
						}

						@Override
						public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


						}

					});

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

}
