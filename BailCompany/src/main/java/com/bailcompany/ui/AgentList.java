package com.bailcompany.ui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.FindBestAgent;
import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.TrackAgents;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.custom.LocationAdapter;
import com.bailcompany.custom.LocationImpl;
import com.bailcompany.model.AgentModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Log;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class AgentList extends CustomFragment {
	private ListView hiredAgent;
	boolean isHired;

	private ArrayList<AgentModel> agentList = new ArrayList<AgentModel>();
	private ImageLoader loader;
	GetAnAgent ga;
	double lat1, lng1;

	static int getCallTimeout = 50000;
	int i;
	String response2;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static boolean hireReq;
	int page = 0;
	boolean loadingMore;
	HiredAgentAdapter adapter;
	String latt, lngg;

	@SuppressLint("InflateParams")
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list_hired_agent, null);
		setHasOptionsMenu(true);
		WebAccess.fromFindMe = true;
		FindBestAgent.isRequestedAgent = false;
		loader = new ImageLoader(StaticData.getDIP(60), StaticData.getDIP(60),
				ImageLoader.SCALE_FITXY);
		isHired = getArguments().getBoolean("isHired");
		if (isHired) {

			((TextView) v.findViewById(R.id.header_list))
					.setVisibility(View.VISIBLE);
		} else {
			latt = FindBestAgent.locLatt;
			lngg = FindBestAgent.locLng;
			((TextView) v.findViewById(R.id.header_list))
					.setVisibility(View.GONE);
			agentList = (ArrayList<AgentModel>) getArguments().getSerializable("agents");
			sortAgentByDistance2();
		}

		hiredAgent = (ListView) v.findViewById(R.id.hired_agent_list);

		getLocation();
		setupAgentList();

		return v;
	}

	private void getLocation() {
		LocationImpl.addListener(new LocationAdapter() {

			@Override
			public void onLocationChange(Location location) {
				lat1 = location.getLatitude();
				lng1 = location.getLongitude();

			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocationImpl.removeListener(new LocationAdapter() {

			@Override
			public void onLocationChange(Location location) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void setupAgentList() {

		/*
		 * ArrayList<Feed> al = new ArrayList<Feed>(); al.add(new Feed("Emily",
		 * "Address 1","0.2 KM", R.drawable.hired_agent_pic1, true)); al.add(new
		 * Feed("Ella", "Address 2","1.2 KM", R.drawable.hired_agent_pic1,
		 * false)); al.add(new Feed("Daniel", "Address 3","1.3 KM",
		 * R.drawable.hired_agent_pic1, false)); al.add(new Feed("Jacob",
		 * "Address 4","0.4 KM", R.drawable.hired_agent_pic1, true));
		 */

		if (isHired) {
			if (Utils.isOnline(getActivity()))
				getAgentList();
			else
				Utils.noInternetDialog(getActivity());
		}

		adapter = new HiredAgentAdapter(getActivity());
		hiredAgent.setAdapter(adapter);

		hiredAgent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isHired) {
					Log.e("Agent list user", agentList.get(position)
							.getUsername()
							+ "==="
							+ agentList.get(position).getEmail());
					startActivity(new Intent(getActivity(), TrackAgents.class)
							.putExtra("agent", agentList.get(position))
							.putExtra("defendant",
									WebAccess.defendantDetail.get(position)));
				} else {
					Bundle b = new Bundle();
					b.putSerializable("agent", agentList.get(position));

					Fragment f = new MainFragment();
					f.setArguments(b);
					getActivity().getSupportFragmentManager()
							.beginTransaction().replace(R.id.content_frame, f)
							.addToBackStack(getString(R.string.hired_ag))
							.commit();
				}
			}
		});
	}

	private void sortAgentByDistance() {
		Collections.sort(agentList, new Comparator<AgentModel>() {

			@Override
			public int compare(AgentModel lhs, AgentModel rhs) {
				// TODO Auto-generated method stub
				// correction

				return (int) Math.abs(getDistance(lhs.getLatitude(),
						lhs.getLongitude(), lhs.getLocationLatitude(),
						lhs.getLocationLongitude()) - getDistance(
						rhs.getLatitude(), rhs.getLongitude(),
						lhs.getLocationLatitude(), lhs.getLocationLongitude()));

			}
		});
	}

	private void sortAgentByDistance2() {

		Collections.sort(agentList, new Comparator<AgentModel>() {

			@Override
			public int compare(AgentModel lhs, AgentModel rhs) {
				// TODO Auto-generated method stub
				// correction


				return (int) Math.abs(getDistance(lhs.getLatitude(),
						lhs.getLongitude(), latt, lngg) - getDistance(
						rhs.getLatitude(), rhs.getLongitude(), latt, lngg));

			}
		});

		Collections.reverse(agentList);
	}

	double d;

	private double getDistance(String lat, String longt, String deflat,
			String deflongt) {
		if (!Commons.isEmpty(lat) && !Commons.isEmpty(longt)) {
			if (deflat.equalsIgnoreCase("")) {
				deflat = "0.0";
				deflongt = "0.0";
			}
			double lat2 = Double.parseDouble(lat);
			double lng2 = Double.parseDouble(longt);
			double deflat2 = Double.parseDouble(deflat);
			double deflng2 = Double.parseDouble(deflongt);
			// if (isHired) {
			// d = distance(deflat2, deflng2, lat2, lng2);
			// //
			// d=LocationImpl.getDistanceMeter(Double.parseDouble(lat),Double.parseDouble(longt))/1000;
			// } else {
			// // d= distance(lat1,lng1,lat2,lng2);
			// float dist[] = new float[3];
			// Location.distanceBetween(deflat2, deflng2,
			// Double.parseDouble(lat), Double.parseDouble(longt),
			// dist);
			// d = dist[0] / 1000;
			// }
			d = distance(deflat2, deflng2, lat2, lng2);
			// return d;
		}

		Log.e("dist2", "" + d);
		return d * 0.62137;
	}

	/*
	 * double d;
	 * 
	 * 
	 * // Log.e("distance Miles",""+d* 0.62137); return d ; //* 0.62137; }
	 * 
	 * return 0;
	 */

	private double distance(double lat12, double lng12, double lat2, double lng2) {
		double a = (lat12 - lat2) * AgentList.distPerLat(lat12);
		double b = (lng12 - lng2) * AgentList.distPerLng(lat12);
		Log.e("dist", "" + Math.sqrt(a * a + b * b) / 1000);
		return Math.sqrt(a * a + b * b) / 1000;
	}

	private static double distPerLng(double lat) {
		return 0.0003121092 * Math.pow(lat, 4) + 0.0101182384
				* Math.pow(lat, 3) - 17.2385140059 * lat * lat + 5.5485277537
				* lat + 111301.967182595;
	}

	private static double distPerLat(double lat) {
		return -0.000000487305676 * Math.pow(lat, 4) - 0.0033668574
				* Math.pow(lat, 3) + 0.4601181791 * lat * lat - 1.4558127346
				* lat + 110579.25662316;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e("AgentList", "Option: " + item.getItemId());
		if (!isHired)
			if (item.getItemId() == android.R.id.home)
				((FindBestAgent) getActivity()).onBackPressed();
		if (item.getItemId() == R.id.menu_map) {
			if (agentList != null && agentList.size() > 0) {
				Bundle b = new Bundle();
				Fragment f = new ShowAllOnMap();
				b.putSerializable("agents", agentList);
				f.setArguments(b);

				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, f)
						.addToBackStack(getString(R.string.ag_near)).commit();
			}

		}
		return super.onOptionsItemSelected(item);
	}

	private void getAgentList() {

		if (isHired) {
			if (page == 0)
				showProgressDialog("");
			WebAccess.defendantDetail.clear();
		} else {
			if (page == 0)
				showProgressDialog("");
		}

		if (page == 0) {
			agentList.clear();
		}
		loadingMore = true;
		RequestParams param = new RequestParams();
		param.put("CompanyId", MainActivity.user.getCompanyId());
		param.put("Page", page);
		param.put("UserName", MainActivity.user.getUsername());
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());

		String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_AGENT;
		client.setTimeout(getCallTimeout);
		client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

				if (isHired) {
					if (page == 0)
						dismissProgressDialog();
				} else {
					if (page == 0)
						dismissProgressDialog();
				}
				loadingMore = false;
				if (page > 0)
					page--;

			}

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				if (isHired) {
					if (page == 0)
						dismissProgressDialog();
				} else {
					if (page == 0)
						dismissProgressDialog();
				}
				try {
					loadingMore = false;

					response2 = new String(responseBody);
					if (response2 != null) {
						JSONObject json = new JSONObject(response2);
						if (json.optString("status").equalsIgnoreCase("1")) {
							JSONArray jArray = json
									.getJSONArray("list_of_agents");
							WebAccess.hireReq = true;
							agentList = WebAccess.parseAgentList(jArray);
							WebAccess.hireReq = false;
							if (agentList != null && agentList.size() > 0) {
								sortAgentByDistance();
								adapter.notifyDataSetChanged();
							}
						} else if (json.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(getActivity(),
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(getActivity(), Login.class));
						} else {
							// Utils.showDialog(getActivity(),
							// json.optString("message"));
							adapter.notifyDataSetChanged();
							if (page > 0)
								page--;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	private class HiredAgentAdapter extends BaseAdapter {
		// private double lat;
		// private double lng;

		public HiredAgentAdapter(Context context) {

		}

		@Override
		public int getCount() {
			return agentList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.right_nav_item, null);
			ImageView profile = (ImageView) convertView.findViewById(R.id.img);
			TextView name = (TextView) convertView.findViewById(R.id.lbl1);
			TextView address = (TextView) convertView.findViewById(R.id.lbl2);
			TextView distance = (TextView) convertView
					.findViewById(R.id.distance);

			AgentModel info = agentList.get(position);


			((TextView) convertView.findViewById(R.id.statusStr)).setText(info
					.getReqStatus());
			if (!isHired)
				convertView.findViewById(R.id.statusStr).setVisibility(
						View.GONE);
			Log.e("rakesh", "rakesh");
			Log.e(info.getAgentName());
			Log.e(info.getAddress());
			Log.e(info.getLatitude() + "  " + info.getLongitude()
					+ "   isonline = " + info.getIsOnline());

			Bitmap bm = loader.loadImage(info.getPhotoUrl(),
					new ImageLoadedListener() {

						@Override
						public void imageLoaded(Bitmap bm) {
							if (bm != null)
								notifyDataSetChanged();
						}
					});
			if (bm != null)
				profile.setImageBitmap(ImageUtils.getCircularBitmap(bm));
			else
				profile.setImageBitmap(null);

			if (info.getInstabailAgent().equals("1"))
				name.setText("* "+ info.getAgentName() + "");
			else
				name.setText(info.getAgentName() + "");
			address.setText(info.getAddress());

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			if (isHired) {
				if (!(info.getLocationLatitude().equalsIgnoreCase("0.0"))
						&& !(info.getLocationLatitude().equalsIgnoreCase("")))
					distance.setText(nf.format(getDistance(info.getLatitude(),
							info.getLongitude(), info.getLocationLatitude(),
							info.getLocationLongitude()))
							+ "Miles");
				else
					distance.setText("-----");
			} else {
				if (!latt.equalsIgnoreCase("0.0") || !latt.equalsIgnoreCase(""))
					distance.setText(nf.format(getDistance(info.getLatitude(),
							info.getLongitude(), latt, lngg)) + "Miles");
				else
					distance.setText("-----");
			}
			// Editing*****************
			if (!Commons.isEmpty(info.getIsOnline())) {
				ImageView status = ((ImageView) convertView
						.findViewById(R.id.status));
				if (info.getIsOnline().equalsIgnoreCase("1")) {
					status.setImageResource(R.drawable.online);
				} else {
					status.setImageResource(R.drawable.offline);
				}

			}

			// .setImageResource((info
			// .getIsOnline()) ? R.drawable.online
			// : R.drawable.offline);
			int i = hiredAgent.getLastVisiblePosition();
			int j = hiredAgent.getAdapter().getCount();
			i++;
			if (i == j && !(loadingMore)) {

				page++;

				getAgentList();

			}
			return convertView;
		}

	}

}
