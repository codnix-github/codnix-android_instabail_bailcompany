package com.bailcompany;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
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

public class IndividualChatActivity extends CustomActivity {
	Context mCtx;

	ListView liveChat;
	EditText typeChat;
	Button btnSend;

	private WebSocketClient mWebSocketClient;

	String chatUrl;
	String myUserId;
	String toUserId;
	String toUserName;
	String toUserImage;
	String requestId;

	String chatType;
	String socketID;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_chat);
		setActionBar();

		mCtx = this;
		WebAccess.isIndividualChatOpen = true;
		liveChat = (ListView) findViewById(R.id.live_chat);
		typeChat = (EditText) findViewById(R.id.type_chat);
		btnSend = (Button) findViewById(R.id.btn_send);
		statusBg = (TextView) findViewById(R.id.btn_avail);
		mWebSocketClient = null;

		chatUrl = "ws://162.144.85.22:9090/chat/server.php";
		myUserId = MainActivity.user.getCompanyId();
		if (getIntent().hasExtra("companyId")) {
			toUserId = getIntent().getExtras().getString("companyId");
			toUserImage = getIntent().getExtras().getString("companyImage");
			toUserName = getIntent().getExtras().getString("companyName");
			isOnline = getIntent().getExtras().getString("companyOnline");
		} else if (getIntent().hasExtra("agentId")) {
			toUserId = getIntent().getExtras().getString("agentId");
			toUserImage = getIntent().getExtras().getString("agentImage");
			toUserName = getIntent().getExtras().getString("agentName");
			isOnline = getIntent().getExtras().getString("agentOnline");
		}
		chatType = "chatType_User"; // can be "chatType_User" or
		requestId = "0"; // "chatType_Request"

		_messages = new ArrayList<String>();

		adapter = new IncomingListAdapter();
		liveChat.setAdapter(adapter);
		if (isOnline.equalsIgnoreCase("1")) {
			statusBg.setBackgroundResource(R.drawable.btn_available_green_bg);
			statusBg.setText("Online");
		} else {
			statusBg.setBackgroundResource(R.drawable.btn_not_available_red_bg);
			statusBg.setText("Offline");
		}
		WebAccess.indUserId = toUserId;
		setupSendButton();
		getAllMessages();
	}

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.instant));
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideKeyboard();
		connectWebSocket();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		WebAccess.isIndividualChatOpen = false;
		WebAccess.indUserId = null;
	}

	void hideKeyboard() {
		View view = THIS.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) THIS
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void setupSendButton() {
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				if (mWebSocketClient != null) {
					try {
						String msgTxt = typeChat.getText().toString().trim();
						if (msgTxt.length() == 0)
							return;
						typeChat.setText("");

						JSONObject json = new JSONObject();
						if (chatType.equals("chatType_User")) {
							json.put("Type", "user_message")
									.put("UserId", myUserId)
									.put("RequestId", "0")
									.put("ToUserId", toUserId)
									.put("MessageText", msgTxt);
						} else if (chatType.equals("chatType_Request")) {
							json.put("Type", "request_message")
									.put("UserId", myUserId)
									.put("RequestId", requestId)
									.put("ToUserId", toUserId)
									.put("MessageText", msgTxt);
						}
						mWebSocketClient.send(json.toString());
					} catch (Exception e) {
						Toast.makeText(
								IndividualChatActivity.this,
								"Connection Problem,Please check Your Internet And try again later",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				} else {
					Toast.makeText(
							IndividualChatActivity.this,
							"Connection Problem,Please check Your Internet And try again later",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void connectWebSocket() {
		URI uri;
		try {
			// uri = new URI("ws://74.208.70.149:9090/chat/server.php");
			uri = new URI("ws://162.144.85.22:9090/chat/server.php");
		} catch (URISyntaxException e) {
			Toast.makeText(
					IndividualChatActivity.this,
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
							IndividualChatActivity.this,
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
			final String message = jsonObj.getString("Message");
			if (status.equals("1")) {
				if (type.equals("ChatConnected")) {
					btnSend.post(new Runnable() {
						@Override
						public void run() {
							// Toast.makeText(mCtx,
							// "Connections are Ready to Comunicate",
							// Toast.LENGTH_LONG).show();
							btnSend.setEnabled(true);
						}
					});
				} else if (type.equals("NewMessage")) {
					if (chatType.equals("chatType_User")) {
						String from = jsonObj.getString("FromUserId");
						String to = jsonObj.getString("ToUserId");

						if ((toUserId.equalsIgnoreCase(to) && MainActivity.user
								.getCompanyId().equalsIgnoreCase(from))
								|| (toUserId.equalsIgnoreCase(from) && MainActivity.user
										.getCompanyId().equalsIgnoreCase(to))) {
							msgListObj.add(parseMessage(jsonObj));
							reloadChatMessages();
						}

					}
				} else if (type.equals("NewRequestMessage")) {
					if (chatType.equals("chatType_Request")) {
						msgListObj.add(parseMessage(jsonObj));
						reloadChatMessages();
					}
				}

			} else {
				Toast.makeText(
						IndividualChatActivity.this,
						"Connection Problem,Please check Your Internet And try again later",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(
					IndividualChatActivity.this,
					"Connection Problem,Please check Your Internet And try again later",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public MessageModel parseMessage(JSONObject chatJsonObj) {
		MessageModel msgObj = new MessageModel();

		try {
			msgObj.setMessageRead("0");
			msgObj.setMessageId(chatJsonObj.getString("MessageId"));
			msgObj.setType(chatJsonObj.getString("Type"));
			msgObj.setMessage(chatJsonObj.getString("Message"));
			msgObj.setTime(chatJsonObj.getString("Time"));
			msgObj.setFromUserId(chatJsonObj.getString("FromUserId"));
			msgObj.setFromRole(chatJsonObj.getString("FromRole"));
			msgObj.setFromName(chatJsonObj.getString("FromName"));
			msgObj.setFromPhoto(chatJsonObj.getString("FromPhoto"));
			msgObj.setFromSocketID(chatJsonObj.getString("FromSocketID"));
			msgObj.setToUserId(chatJsonObj.getString("ToUserId"));
			msgObj.setToRole(chatJsonObj.getString("ToRole"));
			msgObj.setToName(chatJsonObj.getString("ToName"));
			msgObj.setToSocketID(chatJsonObj.getString("ToSocketID"));
			msgObj.setToPhoto(chatJsonObj.getString("ToPhoto"));

			return msgObj;
		} catch (Exception e) {
			Toast.makeText(
					IndividualChatActivity.this,
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
				convertView = getLayoutInflater().inflate(R.layout.chat_sender,
						null);
				imageUrl = WebAccess.PHOTO + MainActivity.user.getPhoto();
				((TextView) convertView.findViewById(R.id.message))
						.setText(Html
								.fromHtml("<b>ME</b><br> <font color=#7a7a7a>"
										+ msgListObj.get(position).getMessage()
										+ ""));
				if (msgListObj.get(position).getToSocketID() != null)
					if (msgListObj.get(position).getToSocketID()
							.equalsIgnoreCase("")) {
						statusBg.setBackgroundResource(R.drawable.btn_not_available_red_bg);
						statusBg.setText("Offline");
					} else {
						statusBg.setBackgroundResource(R.drawable.btn_available_green_bg);
						statusBg.setText("Online");
					}
			} else {
				convertView = getLayoutInflater().inflate(
						R.layout.chat_reciever, null);
				imageUrl = WebAccess.PHOTO + toUserImage;

				((TextView) convertView.findViewById(R.id.message))
						.setText(Html.fromHtml("<b>" + toUserName
								+ "</b><br> <font color=#7a7a7a>"
								+ msgListObj.get(position).getMessage() + ""));
				if (msgListObj.get(position).getMessageRead()
						.equalsIgnoreCase("0")
						&& chatType.equals("chatType_User")
						&& mWebSocketClient != null) {
					setReadToSocket(msgListObj.get(position).getMessageId());
					msgListObj.get(position).setMessageRead("1");
				}
				if (msgListObj.get(position).getFromSocketID() != null)
					if (msgListObj.get(position).getFromSocketID()
							.equalsIgnoreCase("")) {
						statusBg.setBackgroundResource(R.drawable.btn_not_available_red_bg);
						statusBg.setText("Offline");
					} else {
						statusBg.setBackgroundResource(R.drawable.btn_available_green_bg);
						statusBg.setText("Online");
					}
			}
//			String format = "yyyy-MM-dd HH:mm:ss";
//			Date localTime = null;
//			try {
//				SimpleDateFormat sdf = new SimpleDateFormat(format);
//				// Convert Local Time to UTC (Works Fine)
//
//				sdf.setTimeZone(TimeZone.getTimeZone("+00"));
//				localTime = sdf.parse(msgListObj.get(position).getTime());
//
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			SimpleDateFormat sdf2 = new SimpleDateFormat(format);
//			String dte = sdf2.format(localTime);
			String datefrm = Utils.lastMessage(msgListObj.get(position)
					.getTime());
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

	void setReadToSocket(String messageId) {

		if (mWebSocketClient != null) {
			try {

				JSONObject json = new JSONObject();
				if (chatType.equals("chatType_User")) {
					json.put("UserId", myUserId).put("MessageId", messageId)
							.put("Type", "messageread");
					mWebSocketClient.send(json.toString());
				}

			} catch (Exception e) {
				Toast.makeText(
						IndividualChatActivity.this,
						"Connection Problem,Please check Your Internet And try again later",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
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
			param.put("UserId", toUserId);
			String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_MESSAGES;
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
								WebAccess.getALLIndividualMessages(response2);
								if (msgListObj != null && msgListObj.size() > 0) {
									adapter.notifyDataSetChanged();
								}
							} else if (resObj.optString("status")
									.equalsIgnoreCase("3")) {
								Toast.makeText(
										THIS,
										"Session was closed please login again",
										Toast.LENGTH_LONG).show();
								MainActivity.sp.edit().putBoolean("isFbLogin",
										false);
								MainActivity.sp.edit().putString("user", null)
										.commit();
								startActivity(new Intent(
										IndividualChatActivity.this,
										Login.class));
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);

	}
	// ///////////////////////////////////////////////
}
