package com.bailcompany.ui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.IndividualChatActivity;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.ChatUser;
import com.bailcompany.model.MessageModel;
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
public class InstantChat extends CustomFragment {
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	boolean refreshCall;
	static int getCallTimeout = 50000;
	private ListView incomingRequestList;
	public static ArrayList<ChatUser> msgListObj = new ArrayList<ChatUser>();
	int page = 0;
	boolean loadingMore;
	IncomingListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.instant_chat, null);
		msgListObj.clear();
		getAllRequest();
		incomingRequestList = (ListView) v
				.findViewById(R.id.incoming_request_list);

		adapter = new IncomingListAdapter(getActivity());
		incomingRequestList.setAdapter(adapter);

		incomingRequestList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				MessageModel model = msgListObj.get(position).getLastMessage();
				String socket = msgListObj.get(position).getSocketId();
				String isOnline = "1";
				if (socket.equalsIgnoreCase("")) {
					isOnline = "0";
				}
				String userId = null;
				refreshCall = true;
				if (model.getFromUserId().equalsIgnoreCase(
						MainActivity.user.getCompanyId())) {
					userId = model.getToUserId();
				} else {
					userId = model.getFromUserId();
				}
				if (msgListObj.get(position).getUserRole()
						.equalsIgnoreCase("Agent")) {
					startActivity(new Intent(getActivity(),
							IndividualChatActivity.class)
							.putExtra("agentId", userId)
							.putExtra("agentName",
									msgListObj.get(position).getUserName())
							.putExtra("agentImage",
									msgListObj.get(position).getUserPhoto())
							.putExtra("agentOnline", isOnline));
				} else if (msgListObj.get(position).getUserRole()
						.equalsIgnoreCase("Company")) {
					startActivity(new Intent(getActivity(),
							IndividualChatActivity.class)
							.putExtra("companyId", userId)
							.putExtra("companyName",
									msgListObj.get(position).getUserName())
							.putExtra("companyImage",
									msgListObj.get(position).getUserPhoto())
							.putExtra("companyOnline", isOnline));
				}

			}
		});
		setHasOptionsMenu(true);
		return v;
	}

	@Override
	public void onResume() {

		super.onResume();
		if (refreshCall) {
			refreshCall = false;
			getAllRequest();
			adapter.notifyDataSetChanged();
			msgListObj.clear();
		}
	}

	@SuppressLint("InflateParams")
	public class IncomingListAdapter extends BaseAdapter {
		public IncomingListAdapter(Context context) {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return msgListObj.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return msgListObj.get(position);
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
						R.layout.instant_chat_item, null);
			RelativeLayout lp = (RelativeLayout) convertView
					.findViewById(R.id.lp);
			ImageView mask = (ImageView) convertView.findViewById(R.id.mask);
			ImageView status = (ImageView) convertView
					.findViewById(R.id.status);
			if (position % 2 == 0) {
				mask.setBackgroundResource(R.drawable.hired_agent_pic_mask);
				lp.setBackgroundColor(Color.parseColor("#f8f6f6"));
			} else {
				mask.setBackgroundResource(R.drawable.hired_agent_pic_mask_white);
				lp.setBackgroundColor(Color.parseColor("#ffffff"));

			}
			MessageModel model = msgListObj.get(position).getLastMessage();
			TextView msg = ((TextView) convertView.findViewById(R.id.message));
			TextView cmpName = ((TextView) convertView.findViewById(R.id.name));
			TextView time = ((TextView) convertView.findViewById(R.id.time));
			cmpName.setText(msgListObj.get(position).getUserName());
			// String format = "yyyy-MM-dd HH:mm:ss";
			// Date localTime = null;
			// try {
			// SimpleDateFormat sdf = new SimpleDateFormat(format);
			// sdf.setTimeZone(TimeZone.getTimeZone("+00"));
			// localTime = sdf.parse(model.getTime());
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// SimpleDateFormat sdf2 = new SimpleDateFormat(format);
			// String dte = sdf2.format(localTime);
			String datefrm = Utils.lastMessage(model.getTime());
			if (datefrm != null) {
				time.setText(datefrm);
			} else {
				time.setText("--/--/--");
			}
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.image);
			image.setBackgroundResource(R.drawable.default_profile_image);
			Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
					StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
					msgListObj.get(position).getUserPhoto(),
					new ImageLoadedListener() {

						@Override
						public void imageLoaded(Bitmap bm) {
							Log.d("Bitmap", bm == null ? "Null Bitmap"
									: "Valid Bitmap");
							if (bm != null) {
								image.setImageBitmap(ImageUtils
										.getCircularBitmap(bm));
							} else {
								image.setBackgroundResource(R.drawable.default_profile_image);
							}
						}
					});
			if (bmp != null)
				image.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
			else
				image.setImageBitmap(null);

			msg.setText(model.getMessage());
			if (!model.getFromUserId().equalsIgnoreCase(
					MainActivity.user.getCompanyId())
					&& model.getMessageRead().equalsIgnoreCase("0")) {
				setColor(msg);
				setColor(cmpName);
				setColor(time);

			}
			if (msgListObj.get(position).getSocketId().equalsIgnoreCase("")) {
				status.setBackgroundResource(R.drawable.offline);
			} else {
				status.setBackgroundResource(R.drawable.online);
			}
			return convertView;
		}
	}

	void setColor(TextView txtView) {
		txtView.setTypeface(null, Typeface.BOLD);
		txtView.setTextColor(Color.parseColor("#050505"));
	}

	// String getDate(String dte) {
	// String date2 = null;
	// try {
	// String date[] = dte.split(" ");
	//
	// String currentDate = currentDate();
	//
	// int type = compareDate(currentDate, dte);
	//
	// if (type == 0) {
	//
	// Date d = new SimpleDateFormat("HH:mm:ss").parse(date[1]);
	// date2 = new SimpleDateFormat("hh:mm a").format(d);
	// return date2;
	//
	// } else {
	//
	// Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
	// date2 = new SimpleDateFormat("MM/dd/yyyy").format(d);
	// return date2;
	//
	// }
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return date2;
	// }

	// public String currentDate() {
	// Calendar c = Calendar.getInstance();
	// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
	// String formattedDate = df.format(c.getTime());
	// return formattedDate;
	// }

	// public int compareDate(String currentDate, String userDate) {
	// int type = 0;
	// try {
	//
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	//
	// Date date1 = formatter.parse(currentDate);
	// Date date2 = formatter.parse(userDate);
	// if (date1.compareTo(date2) < 0) {
	// type = -1;// date2 greater
	// } else if (date1.compareTo(date2) > 0) {
	// type = 1;// date2 smaller
	// } else {
	// type = 0;// equal date
	// }
	//
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return type;
	// }

	void getAllRequest() {
		if (Utils.isOnline(getActivity())) {
			msgListObj.clear();
			showProgressDialog("");
			RequestParams param = new RequestParams();
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_USER_CHAT;
			client.setTimeout(getCallTimeout);
			client.post(getActivity(), url, param,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
							dismissProgressDialog();
							Utils.showDialog(getActivity(),
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

										WebAccess.getChatRequest(response2);
										if (msgListObj != null
												&& msgListObj.size() > 0) {
											adapter.notifyDataSetChanged();
										} else {
											Utils.showDialog(getActivity(),
													"No Chat Thread Available")
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

}
