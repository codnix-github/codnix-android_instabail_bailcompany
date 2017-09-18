package com.bailcompany;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.ui.CallDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressWarnings("unused")
public class LocationSearchActivity extends CustomActivity{


	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;
	
	private TextView runTime;
	private TextView btnCall;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_hired_agent);
		setActionBar();
		try {
			initMap(savedInstanceState);
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	runTime = (TextView)findViewById(R.id.runtime);
		btnCall = (TextView)findViewById(R.id.btnCall);
		runTime.setText(Html.fromHtml("<font color=#F5F5F4>Run Time : </font><font color=#C9B445>01hr : 10 min"));
		setTouchNClick(R.id.btnCall);
		
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.btnCall)
			CallDialog.show(THIS);
	}
	private void initMap(Bundle savedInstanceState) throws GooglePlayServicesNotAvailableException
	{
		MapsInitializer.initialize(THIS);
		mMapView = (MapView)findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
	}
	
	private void setActionBar()
	{
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.trck_agnt));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	/**
	 * This method can be used to show the markers on the map. Current
	 * implementation of this method will show only a single Pin with title and
	 * snippet. You must customize this method to show the pins as per your
	 * need.
	 */
	private void setupMapMarkers()
	{

		mMap.clear();

		MarkerOptions opt = new MarkerOptions();
		LatLng l = new LatLng(37.42, -122.084);
		opt.position(l).title("Emily").snippet("8 min");
		opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
		
		mMap.addMarker(opt).showInfoWindow();
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 16));
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker arg0) {
				startActivity(new Intent(THIS,AgentProfile.class));
				
			}
		});
	}

	/**
	 * This class creates a Custom a InfoWindowAdapter that is used to show
	 * popup on map when user taps on a pin on the map. Current implementation
	 * of this class will show a Title and a snippet.
	 * 
	 */
	

	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		mMapView.onResume();

		mMap = mMapView.getMap();
		if (mMap != null)
		{
			mMap.setMyLocationEnabled(true);
			// mMap.setOnInfoWindowClickListener(this);
			//mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
			setupMapMarkers();
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

}
