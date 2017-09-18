package com.bailcompany.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Utility {
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
	public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 124;
	public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 127;
	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkPermission(final Activity context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder
							.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@TargetApi(Build.VERSION_CODES.M)
								public void onClick(DialogInterface dialog,
										int which) {
									ActivityCompat
											.requestPermissions(
													context,
													new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
													MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
								}
							});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat
							.requestPermissions(
									context,
									new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("Storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
								public void onClick(DialogInterface dialog,
										int which) {
									ActivityCompat
											.requestPermissions(
													context,
													new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
													MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
								}
							});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat
							.requestPermissions(
									context,
									new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						context, Manifest.permission.CAMERA)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("Storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
								public void onClick(DialogInterface dialog,
										int which) {
									ActivityCompat
											.requestPermissions(
													context,
													new String[] { Manifest.permission.CAMERA },
													MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
								}
							});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat.requestPermissions(context,
							new String[] { Manifest.permission.CAMERA },
							MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			}  else {
				return true;
			}

		} else {
			return true;
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkNotificationPermission(final Activity context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						context, Manifest.permission.ACCESS_FINE_LOCATION)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder
							.setMessage("Access Location permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@TargetApi(Build.VERSION_CODES.M)
								public void onClick(DialogInterface dialog,
										int which) {
									ActivityCompat
											.requestPermissions(
													context,
													new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
													MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
								}
							});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat
							.requestPermissions(
									context,
									new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
									MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
				}
				return false;
			} else if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder
							.setMessage("ACCESS_COARSE_LOCATION permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@TargetApi(Build.VERSION_CODES.M)
								public void onClick(DialogInterface dialog,
										int which) {
									ActivityCompat
											.requestPermissions(
													context,
													new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
													MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
								}
							});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat
							.requestPermissions(
									context,
									new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			}  else if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						context, Manifest.permission.CALL_PHONE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder
							.setMessage("CALL_PHONE permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@TargetApi(Build.VERSION_CODES.M)
								public void onClick(DialogInterface dialog,
										int which) {
									ActivityCompat
											.requestPermissions(
													context,
													new String[] { Manifest.permission.CALL_PHONE },
													MY_PERMISSIONS_REQUEST_CALL_PHONE);
								}
							});
					AlertDialog alert = alertBuilder.create();
					alert.show();

				} else {
					ActivityCompat.requestPermissions(context,
							new String[] { Manifest.permission.CALL_PHONE },
							MY_PERMISSIONS_REQUEST_CALL_PHONE);
				}
				return false;
			}else {
				return true;
			}

		} else {
			return true;
		}
	}
}
