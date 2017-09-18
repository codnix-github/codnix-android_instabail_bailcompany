package com.bailcompany.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.Header;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.Launcher;
import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
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
public class InstantGroupChat extends CustomFragment {
	Context mCtx;

	ListView liveChat;
	EditText typeChat;
	Button btnSend;
	String chatType;
	String chatUrl;
	String myUserId;
	String toUserId;
	String toUserName;
	String toUserImage;
	String requestId;
	ArrayList<String> _messages;
	public static ArrayList<MessageModel> msgListObj = new ArrayList<MessageModel>();
	IncomingListAdapter adapter;
	Bitmap company, agent;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	String isOnline = "1";
	TextView statusBg;
	private WebSocketClient mWebSocketClient;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.instant_group_chat, null);
		setActionBar();
		liveChat = (ListView) v.findViewById(R.id.live_chat);
		typeChat = (EditText) v.findViewById(R.id.type_chat);
		btnSend = (Button) v.findViewById(R.id.btn_send);
		mCtx = getActivity();
		WebAccess.isPersonalGroupChatOpen = true;
		chatUrl = "ws://162.144.85.22:9090/chat/server.php";
		myUserId = MainActivity.user.getCompanyId();
		chatType = "chatType_Group"; // can be "chatType_User" or
		requestId = "0"; // "chatType_Request"
		toUserId = "0";
		_messages = new ArrayList<String>();

		adapter = new IncomingListAdapter();
		liveChat.setAdapter(adapter);

		setupSendButton();
		getAllMessages();
		setHasOptionsMenu(true);
		return v;
	}

	protected void setActionBar() {
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.instant));
	}

	@Override
	public void onResume() {
		super.onResume();

		connectWebSocket();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		WebAccess.isPersonalGroupChatOpen = false;

	}

	private void setupSendButton() {
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWebSocketClient != null) {
					try {
						String msgTxt = typeChat.getText().toString().trim();
						if (msgTxt.length() == 0)
							return;
						typeChat.setText("");

						JSONObject json = new JSONObject();
						if (chatType.equals("chatType_Group")) {
							json.put("Type", "group_message")
									.put("UserId", myUserId)
									.put("RequestId", "0").put("ToUserId", "0")
									.put("MessageText", msgTxt);
						}
						mWebSocketClient.send(json.toString());
					} catch (Exception e) {
						Toast.makeText(
								getActivity(),
								"Connection Problem,Please check Your Internet And try again later",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Connection Problem,Please check Your Internet And try again later",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void connectWebSocket() {
		URI uri;
		try {
			uri = new URI("ws://162.144.85.22:9090/chat/server.php");
		} catch (URISyntaxException e) {
			Toast.makeText(
					getActivity(),
					"Connection Problem,Please check Your Internet And try again later",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}

		mWebSocketClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				Log.d("BailChat", "Opened");

				try {
					JSONObject json = new JSONObject();
					json.put("Type", "connected").put("UserId", myUserId)
							.put("RequestId", "0").put("ToUserId", toUserId)
							.put("MessageText", "");
					mWebSocketClient.send(json.toString());

				} catch (Exception e) {
					Toast.makeText(
							getActivity(),
							"Connection Problem,Please check Your Internet And try again later",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(String s) {
				Log.d("BailChat", "Rec: " + s);
				handleReceivedMessage(s);
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				Log.d("BailChat", "Closed: " + s);
				connectWebSocket();
			}

			@Override
			public void onError(Exception e) {
				Log.d("BailChat", "Error: " + e.getMessage());
				mWebSocketClient = null;
				connectWebSocket();
			}
		};
		mWebSocketClient.connect();
	}

	void handleReceivedMessage(String s) {
		try {
			JSONObject jsonObj = new JSONObject(s);
			String status = jsonObj.getString("Status");
			String type = jsonObj.getString("Type");
			// final String message = jsonObj.getString("Message");
			if (status.equals("1")) {
				if (type.equals("ChatConnected")) {
					btnSend.post(new Runnable() {
						@Override
						public void run() {

							btnSend.setEnabled(true);
						}
					});
				} else if (type.equals("NewGroupMessage")) {
					if (chatType.equals("chatType_Group")) {
						msgListObj.add(parseMessage(jsonObj));
						reloadChatMessages();
					}
				}

			} else {
				Toast.makeText(
						getActivity(),
						"Connection Problem,Please check Your Internet And try again later",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(
					getActivity(),
					"Connection Problem,Please check Your Internet And try again later",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public MessageModel parseMessage(JSONObject chatJsonObj) {
		MessageModel msgObj = new MessageModel();

		try {

			msgObj.setType(chatJsonObj.getString("Type"));
			msgObj.setMessage(chatJsonObj.getString("Message"));
			msgObj.setTime(chatJsonObj.getString("Time"));
			msgObj.setFromUserId(chatJsonObj.getString("FromUserId"));
			msgObj.setFromRole(chatJsonObj.getString("FromRole"));
			msgObj.setFromName(chatJsonObj.getString("FromName"));
			msgObj.setFromPhoto(chatJsonObj.getString("FromPhoto"));
			msgObj.setToUserId(chatJsonObj.getString("ToUserId"));
			msgObj.setToRole(chatJsonObj.getString("ToRole"));
			msgObj.setToName(chatJsonObj.getString("ToName"));
			msgObj.setToPhoto(chatJsonObj.getString("ToPhoto"));

			return msgObj;
		} catch (Exception e) {
			Toast.makeText(
					getActivity(),
					"Connection Problem,Please check Your Internet And try again later",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return null;
	}

	void reloadChatMessages() {
		liveChat.post(new Runnable() {
			@Override
			public void run() {
				liveChat.invalidateViews();
			}
		});

	}

	@SuppressLint("InflateParams")
	public class IncomingListAdapter extends BaseAdapter {

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
			String imageUrl = "";

			if (MainActivity.user.getCompanyId().equalsIgnoreCase(
					msgListObj.get(position).getFromUserId())) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.chat_sender, null);
			} else {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.chat_reciever, null);
			}
			imageUrl = WebAccess.PHOTO
					+ msgListObj.get(position).getFromPhoto();
			((TextView) convertView.findViewById(R.id.message)).setText(Html
					.fromHtml("<b>" + msgListObj.get(position).getFromName()
							+ "</b><br> <font color=#7a7a7a>"
							+ msgListObj.get(position).getMessage() + ""));
			// String format = "yyyy-MM-dd HH:mm:ss";
			// Date localTime = null;
			// try {
			// SimpleDateFormat sdf = new SimpleDateFormat(format);
			// // Convert Local Time to UTC (Works Fine)
			//
			// sdf.setTimeZone(TimeZone.getTimeZone("+00"));
			// localTime = sdf.parse(msgListObj.get(position).getTime());
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// SimpleDateFormat sdf2 = new SimpleDateFormat(format);
			// String dte = sdf2.format(localTime);
			// String datefrm = getDate(dte);
			String datefrm = Utils.lastMessage(msgListObj.get(position).getTime());
			if (datefrm != null) {
				((TextView) convertView.findViewById(R.id.date))
						.setText(datefrm);
			} else {
				((TextView) convertView.findViewById(R.id.date))
						.setText("--/--/--");
			}
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.image);
			image.setBackgroundResource(R.drawable.default_profile_image);
			Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
					StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
					imageUrl, new ImageLoadedListener() {

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

			return convertView;
		}
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
	//
	// public String currentDate() {
	// Calendar c = Calendar.getInstance();
	// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
	// String formattedDate = df.format(c.getTime());
	// return formattedDate;
	// }
	//
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

	void getAllMessages() {
		if (Utils.isOnline(mCtx)) {
			showProgressDialog("");
			RequestParams param = new RequestParams();
			msgListObj.clear();
			param.put("TemporaryAccessCode",
					MainActivity.user.getTempAccessCode());
			param.put("UserName", MainActivity.user.getUsername());
			String url = WebAccess.MAIN_URL + WebAccess.GET_GROUP_CHAT_HISTORY;
			client.setTimeout(getCallTimeout);
			client.post(mCtx, url, param, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

					dismissProgressDialog();
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
								WebAccess.getGroupMessages(response2);
								if (msgListObj != null && msgListObj.size() > 0) {
									adapter.notifyDataSetChanged();
								}
							} else if (resObj.optString("status")
									.equalsIgnoreCase("3")) {
								Toast.makeText(
										getActivity(),
										"Session was closed please login again",
										Toast.LENGTH_LONG).show();
								MainActivity.sp.edit().putBoolean("isFbLogin",
										false);
								MainActivity.sp.edit().putString("user", null)
										.commit();
								startActivity(new Intent(getActivity(),
										Launcher.class));
							} else {
								adapter.notifyDataSetChanged();

							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

		} else
			Utils.noInternetDialog(mCtx);
	}

}
