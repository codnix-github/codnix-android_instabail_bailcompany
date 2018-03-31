package com.bailcompany.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Const {

    public static final String INSTABAIL_COMPANY_LOGIN_URL = "https://instabailapp.com/web/company";

    public static final String RETURN_FLAG = "flag";
    public static final String BOND_DETAILS_UPDATED = "5001";
    public static final String BOND_DOCUMENT_UPLOADED = "5002";
    public static final String DEFENDANT_BASIC_DETAILS_UPDATED = "5003";
    public static final String RETURN_DEFENDANT_DETAIL = "5004";

    public static final String PROVIDER="com.bailcompany.provider";


    public static final String LANGUAGE = "language";// 0=eng
    public static final String EXTRA_DATA = "extraData";

    public static final File ROOT_DIR;

    public static final File TEMP_PHOTO;
    // public static String SENDER_ID="279522591784";
    public static String SENDER_ID = "796858150390";
    private static String uniqueIdForPhoto;
    private static String uniqueIdForSign;
    public class Menu{
        public  static final boolean SHOW_LIMTED_MENU = true;
        public  static final String GET_AN_AGENT = "Get An Agent";
        public  static final String SELF_ASSIGNED = "Self Assigned";
        public  static final String TRACK_AGENT = "Track Agent";
        public  static final String TRANSFER_BOND = "Transfer Bond";
        public  static final String INCOMING_TRANSFER_BOND_REQUEST = "Incoming Transfer Bond Request";
        public  static final String REFFER_BAIL = "Refer Bail";
        public  static final  String INCOMING_REFFER_BAIL = "Incoming Bail Referral";
        public  static final  String BLACK_LIST_MEMBER = "Blacklist Members";
        public  static final String BAD_DEBT_MEMBER = "Bad Debt Members";
        public  static final String DEFENDANTS = "Defendants";
        public  static final String CALENDAR = "Calendar";
        public  static final String HISTORY = "History";
        public  static final String GET_FUGITIVE_AGENT = "Get A Fugitive Agent";
        public  static final String SENT_FUGITIVE_REQUEST = "Sent Fugitive Request";
        public  static final String INSTANT_CHAT = "Instant Chat";
        public  static final String INSTANT_GROUP_CHAT = "Instant Group Chat";
        public  static final  String CONTACT_US = "Contact Us";
        public  static final String LOGOUT = "Logout";

    }

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

    public static String getFormatedDate(String fromFormtat, String toFormat, String date, boolean utcToLocal) {
        if (date.equalsIgnoreCase(""))
            return "";
        if (utcToLocal) {
            date = convertInLocalTime(fromFormtat, date);
        }

        Date d = null;
        String retdate = "";
        SimpleDateFormat formatter;
        try {
            //d = new Date();
            d = new SimpleDateFormat(fromFormtat).parse(date);
            formatter = new SimpleDateFormat(toFormat);


            // formatter.setTimeZone(TimeZone.getTimeZone("GMT"));


            retdate = formatter.format(d);


            int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
            // Log.d("date=", "Date To String >> " + offset + "-->" + d + "=>" + retdate + " " + formatter.getTimeZone());


        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return retdate;
    }

    private static String convertInLocalTime(String DATE_FORMAT, String serverDate) {
        // String DATE_FORMAT = "yyyy-MM-dd HH:mm";
        String strDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            TimeZone utcZone = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(utcZone);// Set UTC time z one
            Date myDate = sdf.parse(serverDate);
            sdf.setTimeZone(TimeZone.getDefault());// Set device time zone
            strDate = sdf.format(myDate);
            // Log.d("date=", "UTC to Local2 >> " + strDate);
            return strDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }


}
