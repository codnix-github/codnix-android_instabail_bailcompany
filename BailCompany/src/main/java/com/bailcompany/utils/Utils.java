package com.bailcompany.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bailcompany.R;

// TODO: Auto-generated Javadoc

/**
 * The Class Utils is also a utility class provide the basic operations in the
 * app.
 */
@SuppressLint("DefaultLocale")
@SuppressWarnings("deprecation")
public class Utils {

    /**
     * Show dialog.
     *
     * @param ctx       the ctx
     * @param msg       the msg
     * @param btn1      the btn1
     * @param btn2      the btn2
     * @param listener1 the listener1
     * @param listener2 the listener2
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg, String btn1,
                                         String btn2, DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        // builder.setTitle(R.string.app_name);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(btn1, listener1);
        if (btn2 != null && listener2 != null)
            builder.setNegativeButton(btn2, listener2);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;

    }

    /**
     * Show dialog.
     *
     * @param ctx       the ctx
     * @param msg       the msg
     * @param btn1      the btn1
     * @param btn2      the btn2
     * @param listener1 the listener1
     * @param listener2 the listener2
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg, int btn1,
                                         int btn2, DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2) {

        return showDialog(ctx, ctx.getString(msg), ctx.getString(btn1),
                ctx.getString(btn2), listener1, listener2);

    }

    /**
     * Show dialog.
     *
     * @param ctx      the ctx
     * @param msg      the msg
     * @param btn1     the btn1
     * @param btn2     the btn2
     * @param listener the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg, String btn1,
                                         String btn2, DialogInterface.OnClickListener listener) {

        return showDialog(ctx, msg, btn1, btn2, listener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

    }

    /**
     * Show dialog.
     *
     * @param ctx      the ctx
     * @param msg      the msg
     * @param btn1     the btn1
     * @param btn2     the btn2
     * @param listener the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg, int btn1,
                                         int btn2, DialogInterface.OnClickListener listener) {

        return showDialog(ctx, ctx.getString(msg), ctx.getString(btn1),
                ctx.getString(btn2), listener);

    }

    /**
     * Show dialog.
     *
     * @param ctx      the ctx
     * @param msg      the msg
     * @param listener the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg,
                                         DialogInterface.OnClickListener listener) {

        return showDialog(ctx, msg, ctx.getString(android.R.string.ok), null,
                listener, null);
    }

    /**
     * Show dialog.
     *
     * @param ctx      the ctx
     * @param msg      the msg
     * @param listener the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg,
                                         DialogInterface.OnClickListener listener) {

        return showDialog(ctx, ctx.getString(msg),
                ctx.getString(android.R.string.ok), null, listener, null);
    }

    /**
     * Show dialog.
     *
     * @param ctx the ctx
     * @param msg the msg
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg)// ///hello
    {

        return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });

    }

    /**
     * Show dialog.
     *
     * @param ctx the ctx
     * @param msg the msg
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg) {

        return showDialog(ctx, ctx.getString(msg));

    }

    /**
     * Show dialog.
     *
     * @param ctx      the ctx
     * @param title    the title
     * @param msg      the msg
     * @param listener the listener
     */
    public static void showDialog(Context ctx, int title, int msg,
                                  DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener);
        builder.setTitle(title);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void noInternetDialog(Context ctx) {
        showDialog(ctx, "No internet connection !");
    }

    /**
     * Checks if is online.
     *
     * @param ctx the ctx
     * @return true, if is online
     */
    public static final boolean isOnline(Context ctx) {

        ConnectivityManager conMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null

                && conMgr.getActiveNetworkInfo().isAvailable()

                && conMgr.getActiveNetworkInfo().isConnected())
            return true;
        return false;
    }

    public static final boolean isOnline() {

        return isOnline(StaticData.appContext);
    }

    /**
     * Checks if is valid email.
     *
     * @param email the email
     * @return true, if is valid email
     */
    public static boolean isValidEmail(String email) {

        String emailExp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,10}$";
        Pattern pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checks if is valid phone number.
     *
     * @param number the number
     * @return true, if is valid phone number
     */
    public static boolean isValidPhoneNumber(String number) {

        String numExp = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{2,15})$";
        Pattern pattern = Pattern.compile(numExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    /**
     * Checks if is numeric.
     *
     * @param number the number
     * @return true, if is numeric
     */
    public static boolean isNumeric(String number) {

        String numExp = "^[-+]?[0-9]*\\.?[0-9]+$";
        Pattern pattern = Pattern.compile(numExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    /**
     * Hide keyboard.
     *
     * @param ctx the ctx
     */
    public static final void hideKeyboard(Activity ctx) {

        if (ctx.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(),
                    0);
        }
    }

    /**
     * Hide keyboard.
     *
     * @param ctx the ctx
     * @param v   the v
     */
    public static final void hideKeyboard(Activity ctx, View v) {

        try {
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show keyboard.
     *
     * @param ctx the ctx
     * @param v   the v
     */
    public static final void showKeyboard(Activity ctx, View v) {

        try {
            v.requestFocus();
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
            // InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make call.
     *
     * @param act    the act
     * @param number the number
     */
    public static final void makeCall(final Activity act, final String number) {

        Utils.showDialog(act, "Call " + number.replace(" ", ""), "Ok",
                "Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent call = new Intent(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + number.trim()));
                        act.startActivity(call);
                    }
                }).show();
    }

    /**
     * De serialise obj.
     *
     * @param obj the obj
     * @return the object
     */
    public static final Object deSerialiseObj(byte[] obj) {

        if (obj != null && obj.length > 0) {
            try {
                ObjectInputStream in = new ObjectInputStream(
                        new ByteArrayInputStream(obj));
                Object o = in.readObject();
                in.close();
                return o;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Serialise obj.
     *
     * @param obj the obj
     * @return the byte[]
     */
    public static final byte[] serialiseObj(Serializable obj) {

        try {
            ByteArrayOutputStream bArr = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bArr);
            out.writeObject(obj);
            byte b[] = bArr.toByteArray();
            out.close();
            bArr.close();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedText(String text) {
        if (Commons.isEmpty(text))
            return "";

        return text;
    }

    /**
     * Copy file.
     *
     * @param src the src
     * @param dst the dst
     */
    public static void copyFile(File src, File dst) {

        try {
            if (!dst.exists())
                dst.createNewFile();
            FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dst);

            int size = (int) src.length();
            byte[] buf = new byte[size];
            in.read(buf);
            out.write(buf);
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the no media file.
     *
     * @param dir the dir
     */
    public static void createNoMediaFile(File dir) {

        try {
            File f = new File(dir, ".nomedia");
            if (!f.exists())
                f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the m d5 string.
     *
     * @param in the in
     * @return the m d5 string
     */
    public static String getMD5String(String in) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            // Log.e("MD5", in+"--"+sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Change locale.
     *
     * @param act    the act
     * @param locale the locale
     */
    public static void changeLocale(Activity act, Locale locale) {

        // Locale locale = new Locale(lang);
        // Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        act.getBaseContext()
                .getResources()
                .updateConfiguration(config,
                        act.getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Gets the formatted count.
     *
     * @param count the count
     * @return the formatted count
     */
    public static String getFormattedCount(int count) {

        if (count >= 100000)
            return "1M";
        if (count >= 10000)
            return "10K";
        if (count >= 1000)
            return "1K";
        return count + "";
    }

    public static void sendMessage(Context ctx, String telNum) {
        Uri uri = Uri.parse("smsto:" + telNum);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);

        ctx.startActivity(it);
    }

    /**
     * Gets the base64 image string.
     *
     * @param file the file
     * @return the base64 image string
     */
    public static String getBase64ImageString(String file) {

        if (file == null)
            return null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            BufferedInputStream bin = new BufferedInputStream(
                    new FileInputStream(file));
            int b;
            while ((b = bin.read()) != -1)
                bout.write(b);
            byte img[] = bout.toByteArray();
            bout.flush();
            bout.close();
            bin.close();
            return Base64.encodeToString(img, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRequiredDateFormatGMT(String from_format_string,
                                                  String to_format_string, String s) {

        SimpleDateFormat to_format = new SimpleDateFormat(to_format_string);
        SimpleDateFormat from_format = new SimpleDateFormat(from_format_string);

        from_format.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            // Convert a String to Date
            Date d = from_format.parse(s);
            // Convert Date object to string
            String strDate = to_format.format(d);
            return strDate;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getRequiredDateFormat(String from_format_string,
                                               String to_format_string, String s) {

        SimpleDateFormat to_format = new SimpleDateFormat(to_format_string);
        SimpleDateFormat from_format = new SimpleDateFormat(from_format_string);
        try {
            // Convert a String to Date
            Date d = from_format.parse(s);
            // Convert Date object to string
            String strDate = to_format.format(d);
            return strDate;
        } catch (Exception e) {
            return "";
        }
    }

    public static String lastMessage(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String value = "";
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            // Convert a String to Date
            Date createDate = df.parse(date);
            Date currDate = new Date();

            String strDate = df.format(currDate);
            Date currentDate = df.parse(strDate);

            long diffInMs = currentDate.getTime() - createDate.getTime();
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

            if (diffInSec < 3600) {
                value = String.format("%d minutes ago", diffInSec / 60);
            } else if (diffInSec < 3600 * 24) {
                value = String.format("%d hours ago", diffInSec / 3600);
            } else {
                df2 = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
                value = df2.format(createDate);
            }
        } catch (Exception e) {
            return "";
        }
        return value;
    }

    public static String getRealPathFromURI(Uri contentUri, Activity act) {
        // String[] proj = { MediaColumns.DATA };
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = act.getContentResolver().query(contentUri, proj, null,
                null, null);

        if (cursor == null) {
            return null;
        }

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);

        return path;
    }

    public static String getPath(Uri uri, Activity act) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = act.managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

	/*
     * public static String getStringFromAsset(String path) { try {
	 * BufferedReader din = new BufferedReader(new InputStreamReader(
	 * StaticData.appContext.getAssets().open(path)));
	 * 
	 * String str = ""; String s; while ((s = din.readLine()) != null) str = str
	 * + s; din.close(); return str; } catch (Exception e) {
	 * e.printStackTrace(); } return null; }
	 * 
	 * public static int parseDrawableId(String str) { return
	 * StaticData.res.getIdentifier(str, "drawable",
	 * StaticData.appContext.getPackageName()); }
	 */

    public static final String PLACES_API = "https://maps.googleapis.com/maps/api/place"
            + "/@@/json";
    public static final String AUTOCOMPLETE = "queryautocomplete";
    public static final String DETAIL = "details";

    public static ArrayList<String[]> searchPlaces(String input) {
        ArrayList<String[]> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API.replace("@@",
                    AUTOCOMPLETE));
            sb.append("?key="
                    + StaticData.appContext.getString(R.string.api_key));
            // sb.append("&components=country:" +
            // Locale.getDefault().getCountry());
            // double d[]=LocationImpl.getAvailableLocation();
            // sb.append("&location="+d[0]+","+d[1]);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&&location=0.0,0.0&radius=20000000");
            Log.e("Url", "URL=" + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Log.d("Place Result",
            // jsonResults==null?"No result":jsonResults.toString());

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String[]>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                String s[] = {
                        predsJsonArray.getJSONObject(i)
                                .getString("description"),
                        predsJsonArray.getJSONObject(i).optString("reference")};
                resultList.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static String[] getLocationLatLng(String api_key, String ref_id) {
        String latLong[] = {"0.0", "0.0"};

        String locUrl = "https://maps.googleapis.com/maps/api/place/details/json?reference="
                + ref_id + "&sensor=true&key=" + api_key;

        HttpGet get = new HttpGet(locUrl);
        get.addHeader("Content-Type", "application/json");
        Log.e("Request", locUrl);
        HttpResponse res = null;
        try {
            res = new DefaultHttpClient().execute(get);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String strRes = "";
        if (res != null) {
            try {
                strRes = EntityUtils.toString(res.getEntity());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.d("Loc response", "Location: " + strRes);

        if (!Commons.isEmpty(strRes)) {
            try {
                JSONObject locObj = new JSONObject(strRes)
                        .getJSONObject("result").getJSONObject("geometry")
                        .getJSONObject("location");
                if (locObj != null) {
                    latLong[0] = locObj.optString("lat");
                    latLong[1] = locObj.optString("lng");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.d("Lat-Longt", latLong[0] + ":" + latLong[1]);

        return latLong;
    }

    public static String getPath() {
        String path = "";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if ((new File("/mnt/emmc")).exists()) {
            path = "/mnt/emmc";
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path + "/BailCompany";
    }

    public static String getVersion(Context ctx) {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
