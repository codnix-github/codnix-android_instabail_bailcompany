package com.bailcompany.utils;

import java.io.File;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

public class Const {

	public static final String INSTABAIL_COMPANY_LOGIN_URL = "https://instabailapp.com/web/company";

	public static final String RETURN_FLAG="flag";
	public static final String BOND_DETAILS_UPDATED="5001";
	public static final String BOND_DOCUMENT_UPLOADED="5002";
	public static final String DEFENDANT_BASIC_DETAILS_UPDATED="5003";


	public static final String LANGUAGE = "language";// 0=eng
	public static final String EXTRA_DATA = "extraData";

	public static final File ROOT_DIR;

	public static final File TEMP_PHOTO;

	private static String uniqueIdForPhoto;
	private static String uniqueIdForSign;

	// public static String SENDER_ID="279522591784";
	public static String SENDER_ID = "796858150390";

	static {
		ROOT_DIR = new File(Environment.getExternalStorageDirectory(),
				"Company");
		if (!ROOT_DIR.exists()) {
			ROOT_DIR.mkdirs();
		}
		TEMP_PHOTO = new File(Environment.getExternalStorageDirectory(),
				"Temp_Photo");

		if (!TEMP_PHOTO.exists()) {
			TEMP_PHOTO.mkdirs();
		}
	}

	public static String getUniqueIdforImage() {
		uniqueIdForPhoto = "" + getCurrentTime();
		return uniqueIdForPhoto;
	}

	public static String getUniqueIdForSignature() {
		uniqueIdForSign = getTodaysDate() + "_" + getCurrentTime() + "_Sign"
				+ Math.random();
		return uniqueIdForSign + ".png";
	}

	private static String getTodaysDate() {

		final Calendar c = Calendar.getInstance();
		int todaysDate = (c.get(Calendar.YEAR) * 10000)
				+ ((c.get(Calendar.MONTH) + 1) * 100)
				+ (c.get(Calendar.DAY_OF_MONTH));
		Log.w("DATE:", String.valueOf(todaysDate));
		return (String.valueOf(todaysDate));

	}

	private static String getCurrentTime() {

		final Calendar c = Calendar.getInstance();
		int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000)
				+ (c.get(Calendar.MINUTE) * 100) + (c.get(Calendar.SECOND));
		Log.w("TIME:", String.valueOf(currentTime));
		return (String.valueOf(currentTime));

	}

}
