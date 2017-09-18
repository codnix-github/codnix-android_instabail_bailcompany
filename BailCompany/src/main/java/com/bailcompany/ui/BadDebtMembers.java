package com.bailcompany.ui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.AddBadBebt;
import com.bailcompany.BadDetail;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.SearchBadDebt;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.BadDebtMember;
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
@SuppressLint({ "InflateParams", "DefaultLocale"})
public class BadDebtMembers extends CustomFragment {
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	public static ArrayList<BadDebtMember> badDebtList = new ArrayList<BadDebtMember>();
	static int getCallTimeout = 50000;
	public ListView incomingRequestList;
	TextView addNew;
	ImageView search, cancel;
	ArrayList<BadDebtMember> tempBadDebtList;
	IncomingListAdapter adapter;
	public static String name, city, dob;
	int page = 0;
	boolean isSearchCall, loadingMore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.incoming_request, null);
		RelativeLayout l = (RelativeLayout) v.findViewById(R.id.parent_add);
		// LinearLayout l2 = (LinearLayout) v.findViewById(R.id.parent_search);
		search = (ImageView) v.findViewById(R.id.btn_search);
		cancel = (ImageView) v.findViewById(R.id.btn_cancel);
		l.setVisibility(View.VISIBLE);
		// l2.setVisibility(View.VISIBLE);
		addNew = (TextView) v.findViewById(R.id.btn_add);
		cancel.setVisibility(View.GONE);

		getAllRequest();
		incomingRequestList = (ListView) v
				.findViewById(R.id.incoming_request_list);
		adapter = new IncomingListAdapter(getActivity(), badDebtList);
		incomingRequestList.setAdapter(adapter);

		incomingRequestList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// badDebtList.get(position).setRead("1");
				// setReadStatus(Integer.parseInt(badDebtList.get(position)
				// .getBadDebtMemberId()), position);
				startActivity(new Intent(getActivity(), BadDetail.class)
						.putExtra("badDebt", badDebtList.get(position)));
			}

		});
		addNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getActivity(),
						AddBadBebt.class), 1111);
			}
		});

		incomingRequestList
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						BadDebtMember badMem = badDebtList.get(arg2);
						dialog(badMem.getBadDebtMemberId(), arg2);
						return false;
					}
				});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				page = 0;
				isSearchCall = false;
				cancel.setVisibility(View.GONE);
				getAllRequest();
			}
		});

		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				startActivityForResult(new Intent(getActivity(),
						SearchBadDebt.class), 6666);
			}
		});

		setHasOptionsMenu(true);
		return v;
	}

	@Override
	public void onResume() {

		super.onResume();
		incomingRequestList.setAdapter(adapter);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		page = 0;
		incomingRequestList.setAdapter(adapter);
		if (requestCode == 1111) {
			getAllRequest();
		} else if (requestCode == 6666) {
			if (name != null || dob != null || city != null)
				getAllRequest2();
		}

	}

	@SuppressLint("InflateParams")
	private class IncomingListAdapter extends BaseAdapter {

		ArrayList<BadDebtMember> badDebtList2 = new ArrayList<BadDebtMember>();

		public IncomingListAdapter(Context context,
				ArrayList<BadDebtMember> badDebtList) {

			this.badDebtList2 = badDebtList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return badDebtList2.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return badDebtList2.get(position);
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
					.setText(badDebtList2.get(position).getName() + "");
			//
			// String date[] = badDebtList2.get(position).getCreatedDateTime()
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
					"MM/dd/yyyy", badDebtList2.get(position).getCreatedDateTime()));
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.image);
			// if (badDebtList2.get(position).getRead().equalsIgnoreCase("0")) {
			// ImageView badge = (ImageView) convertView
			// .findViewById(R.id.new_badge);
			// badge.setVisibility(View.VISIBLE);
			// } else {
			// ImageView badge = (ImageView) convertView
			// .findViewById(R.id.new_badge);
			// badge.setVisibility(View.GONE);
			// }
			Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
					StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
					badDebtList2.get(position).getPicture(),
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
				// Toast.makeText(getActivity(), "call " + i,
				// Toast.LENGTH_SHORT)
				// .show();
				page++;
				if (isSearchCall) {
					getAllRequest2();
				} else {
					getAllRequest();
				}
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
				badDebtList.clear();
			}
			loadingMore = true;
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("Page", page);
			String url = WebAccess.MAIN_URL + WebAccess.BAD_DEBT;
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
							if (page == 0)
								dismissProgressDialog();
							try {
								String response2;
								loadingMore = false;
								response2 = new String(responseBody);
								JSONObject resObj;

								resObj = new JSONObject(response2);

								if (resObj != null) {
									if (resObj.optString("status")
											.equalsIgnoreCase("1")) {
										WebAccess
												.getALLBadDebtRequest(response2);
										if (badDebtList != null
												&& badDebtList.size() > 0) {
											// adapter = new
											// IncomingListAdapter(
											// getActivity(), badDebtList);
											// incomingRequestList
											// .setAdapter(adapter);
											adapter.notifyDataSetChanged();
										} else {
											Utils.showDialog(getActivity(),
													"No Match Found").show();
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
													"No Results Found").show();
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

	void getAllRequest2() {
		cancel.setVisibility(View.VISIBLE);
		isSearchCall = true;
		if (Utils.isOnline(getActivity())) {
			if (page == 0)
				showProgressDialog("");
			RequestParams param = new RequestParams();
			if (page == 0) {
				badDebtList.clear();
			}
			loadingMore = true;
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			if (name != null && !name.equalsIgnoreCase(""))
				param.put("Name", name);
			if (dob != null && !dob.equalsIgnoreCase(""))
				param.put("DOB", dob);
			if (city != null && !city.equalsIgnoreCase(""))
				param.put("City", city);

			param.put("Page", page);
			String url = WebAccess.MAIN_URL + WebAccess.BAD_DEBT;
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
							if (page == 0)
								dismissProgressDialog();
							loadingMore = false;
							try {
								String response2;
								response2 = new String(responseBody);
								JSONObject resObj;

								resObj = new JSONObject(response2);
								if (resObj != null) {
									if (resObj.optString("status")
											.equalsIgnoreCase("1")) {
										WebAccess
												.getALLBadDebtRequest(response2);

										if (badDebtList != null
												&& badDebtList.size() > 0) {
											adapter.notifyDataSetChanged();
										} else {
											Utils.showDialog(getActivity(),
													"No Results Found")
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
													"No Results Found")
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

	void getDeleteRequest(String id, final int postion) {
		if (Utils.isOnline(getActivity())) {
			showProgressDialog("");
			RequestParams param = new RequestParams();
			loadingMore = true;
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			param.put("BadDebtMemberId", id);
			String url = WebAccess.MAIN_URL + WebAccess.REMOVE_BAD_DEBT;
			client.setTimeout(getCallTimeout);
			client.post(getActivity(), url, param,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
							dismissProgressDialog();
							Utils.showDialog(getActivity(),
									R.string.err_unexpect);
							loadingMore = false;
						}

						@Override
						public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

							dismissProgressDialog();
							String response2;
							loadingMore = false;
							response2 = new String(responseBody);
							JSONObject resObj;
							try {
								resObj = new JSONObject(response2);
								if (resObj != null) {
									if (resObj.optString("status")
											.equalsIgnoreCase("1")) {
										badDebtList.remove(postion);
										showDialog(getActivity(),
												resObj.optString("message"));
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
										Utils.showDialog(getActivity(),
												resObj.optString("message"))
												.show();
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

	public AlertDialog showDialog(Context ctx, String msg)// ///hello
	{

		return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				adapter.notifyDataSetChanged();
			}
		});

	}

	public static AlertDialog showDialog(Context ctx, String msg,
			DialogInterface.OnClickListener listener) {

		return showDialog(ctx, msg, ctx.getString(android.R.string.ok), null,
				listener, null);
	}

	public static AlertDialog showDialog(Context ctx, String msg, String btn1,
			String btn2, DialogInterface.OnClickListener listener1,
			DialogInterface.OnClickListener listener2) {

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		// builder.setTitle(R.string.app_name);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton(btn1, listener1);
		if (btn2 != null && listener2 != null)
			builder.setNegativeButton(btn2, listener2);

		AlertDialog alert = builder.create();
		alert.show();
		return alert;

	}

	public void dialog(final String id2, final int position) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
		builder1.setMessage("Are you sure to delete this member from record?");
		builder1.setCancelable(true);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						getDeleteRequest(id2, position);
						dialog.cancel();
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
	}
}
