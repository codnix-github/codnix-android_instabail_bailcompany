package com.bailcompany.custom;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.bailcompany.utils.StaticData;

// TODO: Auto-generated Javadoc
/**
 * The Class LocationImpl.
 */
public class LocationImpl implements LocationListener {

	/** The Constant THIS. */
	private static final LocationImpl THIS = new LocationImpl();

	/** The Constant LISTENER_LIST. */
	private static final ArrayList<LocationAdapter> LISTENER_LIST = new ArrayList<LocationAdapter>();

	/** The LocationManager object lManager. */
	private static LocationManager lManager;

	public static Location current;

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 * @return true, if successful
	 */
	public static boolean addListener(LocationAdapter listener) {

		if (LISTENER_LIST.contains(listener))
			LISTENER_LIST.remove(listener);
		if (current != null)
			listener.onLocationChange(current);

		if (StaticData.appContext == null)
			return false;
		if (lManager == null)
			lManager = (LocationManager) StaticData.appContext
					.getSystemService(Context.LOCATION_SERVICE);

		LISTENER_LIST.add(listener);

		lManager.removeUpdates(THIS);

		String providerGPS = null;
		if (lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.e("GPS", "AVAIL");
			providerGPS = LocationManager.GPS_PROVIDER;
		}

		String providerNet = null;
		if (lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Log.e("NET", "AVAIL");
			providerNet = LocationManager.NETWORK_PROVIDER;
		}

		if (providerNet == null && providerGPS == null) {
			LISTENER_LIST.clear();
			return false;
		}
		if (providerGPS != null) {
			lManager.requestLocationUpdates(providerGPS, 0, 0, THIS);
			if (providerNet != null)
				lManager.requestLocationUpdates(providerNet, 0, 0, THIS.temp);
		} else
			lManager.requestLocationUpdates(providerNet, 0, 0, THIS);

		/*
		 * Location loc = lManager.getLastKnownLocation(provider); if (loc !=
		 * null) listener.onLocationChange(loc);
		 */

		return true;
	}

	public static double getDistanceMeter2(double slat, double slng,
			double elat, double elng) {

		float dist[] = new float[3];
		Location.distanceBetween(slat, slng, elat, elng, dist);
		return dist[0];
	}

	public static double[] getAvailableLocation() {

		if (lManager == null)
			lManager = (LocationManager) StaticData.appContext
					.getSystemService(Context.LOCATION_SERVICE);

		if (current != null)
			return new double[] { current.getLatitude(), current.getLongitude() };

		Location l = lManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (l != null)
			return new double[] { l.getLatitude(), l.getLongitude() };

		l = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (l != null)
			return new double[] { l.getLatitude(), l.getLongitude() };

		return new double[] { 0, 0 };

	}

	public static double getDistanceMeter(double lat, double lng) {

		double s[] = getAvailableLocation();

		float dist[] = new float[3];
		Log.e("###dist", s[0] + ":" + s[1] + "====" + lat + ":" + lng);
		Location.distanceBetween(s[0], s[1], lat, lng, dist);
		return dist[0];
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public static void removeListener(LocationAdapter listener) {

		LISTENER_LIST.remove(listener);
		if (LISTENER_LIST.size() == 0 && lManager != null)
			lManager.removeUpdates(THIS);
	}

	/**
	 * Clear.
	 */
	public static void clear() {

		if (lManager != null)
			lManager.removeUpdates(THIS);
		LISTENER_LIST.clear();
		lManager = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	@Override
	public void onLocationChanged(Location location) {

		if (isBetterLocation(location, current)) {
			current = location;
			Iterator<LocationAdapter> i = LISTENER_LIST.iterator();
			while (i.hasNext())
				i.next().onLocationChange(current);
			if (location != null
					&& location.getProvider().equals(
							LocationManager.GPS_PROVIDER)) {
				lManager.removeUpdates(temp);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int ONE_MINUTES = 1000 * 60 * 1;
	private static final int HALF_MINUTES = 1000 * 30 * 1;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */

	public static boolean isBetterLocation(Location location,
			Location currentBestLocation) {

		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		if (location.getLatitude() == currentBestLocation.getLatitude()
				&& location.getLongitude() == currentBestLocation
						.getLongitude())
			return false;
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {

		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private LocationListener temp = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {

			LocationImpl.THIS.onLocationChanged(location);
		}
	};
}
