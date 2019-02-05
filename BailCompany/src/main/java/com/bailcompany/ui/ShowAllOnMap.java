package com.bailcompany.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bailcompany.AgentProfile;
import com.bailcompany.FindBestAgent;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.AgentModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowAllOnMap extends CustomFragment{


	

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;
	ArrayList<AgentModel> agentList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.show_all_on_map, null);
		setHasOptionsMenu(true);
		if(getArguments()!=null)
			agentList=(ArrayList<AgentModel>) getArguments().getSerializable("agents");
		Log.d("Agents", agentList==null?"No Agent":agentList.size()+" Agents");
		try {
			initMap(v, savedInstanceState);
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return v;
	}	

	/**
	 * Initialize the Map view.
	 * 
	 * @param v
	 *            the v
	 * @param savedInstanceState
	 *            the saved instance state object passed from OnCreateView
	 *            method of fragment.
	 */
	private void initMap(View v, Bundle savedInstanceState) throws GooglePlayServicesNotAvailableException
	{
		Log.d("map", "Init map");
		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) v.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
	}

	/**
	 * This method can be used to show the markers on the map. Current
	 * implementation of this method will show only a single Pin with title and
	 * snippet. You must customize this method to show the pins as per your
	 * need.
	 */
	private void setupMapMarkers()
	{
		Log.d("map", "SetupMapMarker");
		mMap.clear();
		ArrayList<LatLng> list = new ArrayList<LatLng>();
		for(AgentModel mod:agentList){
			LatLng ll=new LatLng(Double.parseDouble(mod.getLatitude()), Double.parseDouble(mod.getLongitude()));
			list.add(ll);
			MarkerOptions opt = new MarkerOptions();
			opt.position(ll).title(""+mod.getAgentName()).snippet("");
			opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			mMap.addMarker(opt);
		}
		
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(list.get(list.size()-1), 12));	
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker arg0) {
				String title=arg0.getTitle();
				LatLng mLL=arg0.getPosition();
				AgentModel agDetail=null;
				for(AgentModel am:agentList){
					LatLng aLL=new LatLng(Double.parseDouble(am.getLatitude()), Double.parseDouble(am.getLongitude()));
					if(am.getAgentName().equals(title) && mLL.latitude==aLL.latitude && mLL.longitude==aLL.longitude){
						agDetail=am;
						break;
					}
				}
				startActivity(new Intent(getActivity(),AgentProfile.class)
				.putExtra("agent", agDetail));
			}
		});
	}

	/**
	 * This class creates a Custom a InfoWindowAdapter that is used to show
	 * popup on map when user taps on a pin on the map. Current implementation
	 * of this class will show a Title and a snippet.
	 * 
	 */
	private class CustomInfoWindowAdapter implements InfoWindowAdapter
	{

		/** The contents view. */
		private final View mContents;

		/**
		 * Instantiates a new custom info window adapter.
		 */
		CustomInfoWindowAdapter()
		{

			Log.d("map", "CustomWindowAdapter");
			mContents = getActivity().getLayoutInflater().inflate(
					R.layout.map_popup, null);
		}

		/* (non-Javadoc)
		 * @see com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoWindow(com.google.android.gms.maps.model.Marker)
		 */
		@Override
		public View getInfoWindow(Marker marker)
		{

			render(marker, mContents);
			return mContents;
		}

		/* (non-Javadoc)
		 * @see com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoContents(com.google.android.gms.maps.model.Marker)
		 */
		@Override
		public View getInfoContents(Marker marker)
		{

			return null;
		}

		/**
		 * Render the marker content on Popup view. Customize this as per your
		 * need.
		 * 
		 * @param marker
		 *            the marker
		 * @param view
		 *            the content view
		 */
		private void render(final Marker marker, View view)
		{

			String title = marker.getTitle();
			TextView titleUi = (TextView) view.findViewById(R.id.title);
			if (title != null)
			{
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
						titleText.length(), 0);
				titleUi.setText(titleText);
			}
			else
			{
				titleUi.setText("");
			}

			String snippet = marker.getSnippet();
			TextView snippetUi = (TextView) view.findViewById(R.id.snippet);
			if (snippet != null)
			{
				SpannableString snippetText = new SpannableString(snippet);
				snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
						snippet.length(), 0);
				snippetUi.setText(snippetText);
			}
			else
			{
				snippetUi.setText("");
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		Log.d("map", "onResume");
		super.onResume();
		mMapView.onResume();

	//	mMap = mMapView.getMap();
		if (mMap != null)
		{
			Log.d("map", "map not null");
			mMap.setMyLocationEnabled(true);
			// mMap.setOnInfoWindowClickListener(this);
			mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
			setupMapMarkers();
		}else{
			Log.d("map", "map null");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{

		mMapView.onPause();
		if (mMap != null)
			mMap.setInfoWindowAdapter(null);
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		mMapView.onDestroy();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onLowMemory()
	 */
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_list)
		{
			if((getActivity() instanceof MainActivity))
				((MainActivity)getActivity()).onKeyDown(KeyEvent.KEYCODE_BACK, null);
			else
				((FindBestAgent)getActivity()).onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.list, menu);
	}
	

}
