package com.bailcompany.custom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.bailcompany.R;
import com.bailcompany.utils.ExceptionHandler;
import com.bailcompany.utils.Log;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.TouchEffect;

// TODO: Auto-generated Javadoc
/**
 * This is a common activity that all other activities of the app can extend to
 * inherit the common behaviors like setting a Theme to activity.
 */
public class CustomActivity extends FragmentActivity implements OnClickListener {

	/**
	 * Apply this Constant as touch listener for views to provide alpha touch
	 * effect. The view must have a Non-Transparent background.
	 */
	public static final TouchEffect TOUCH = new TouchEffect();
	public static CustomActivity THIS;
	private XmlPullParserFactory pullParserFactory;
	private XmlPullParser parser;
	ProgressDialog pDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StaticData.init(this);
		 Thread.setDefaultUncaughtExceptionHandler(new
		 ExceptionHandler(this));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		THIS = this;
		setupActionBar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		THIS = this;
	}

	/**
	 * This method will setup the top title bar (Action bar) content and display
	 * values. It will also setup the custom background theme for ActionBar. You
	 * can override this method to change the behavior of ActionBar for
	 * particular Activity
	 */
	protected void setupActionBar() {
		final ActionBar actionBar = getActionBar();
		if (actionBar == null)
			return;
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.action_bar_bg));
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
	}

	/**
	 * Sets the touch and click listener for a view with given id.
	 * 
	 * @param id
	 *            the id
	 * @return the view on which listeners applied
	 */
	public View setTouchNClick(int id) {

		View v = setClick(id);
		if (v != null)
			v.setOnTouchListener(TOUCH);
		return v;
	}

	/**
	 * Sets the click listener for a view with given id.
	 * 
	 * @param id
	 *            the id
	 * @return the view on which listener is applied
	 */
	public View setClick(int id) {

		View v = findViewById(id);
		if (v != null)
			v.setOnClickListener(this);
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

	}

	public ArrayAdapter<CharSequence> getAdapter(int arrayRes) {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				THIS, arrayRes, R.layout.spinner_text);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return adapter;
	}

	public ArrayAdapter<String> getAdapter(ArrayList<String> code) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(THIS,
				R.layout.spinner_text, code);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	public ArrayList<String> getCountryCodeList() {

		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			parser = pullParserFactory.newPullParser();

			InputStream is = THIS.getApplicationContext().getAssets()
					.open("Countries_code.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is, null);
			return parseXml(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ArrayList<String> parseXml(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		ArrayList<String> code = new ArrayList<String>();
		int event = parser.getEventType();
		// code.add("Select code");
		while (event != XmlPullParser.END_DOCUMENT) {
			Log.e("Event type", event + "");
			String name = null;
			String countryCodes = "";
			if (event == XmlPullParser.START_TAG) {
				name = parser.getName();
				if (name.equalsIgnoreCase("country")) {
					countryCodes = parser.nextText();
					code.add(countryCodes);
				}
			}
			event = parser.next();
		}
		return code;
	}

	/**
	 * Show progress dialog if not showing already
	 * 
	 * @param message
	 *            Message to display with progress dialog
	 */
	public void showProgressDialog(String message) {
		if (pDialog == null || !pDialog.isShowing())
			pDialog = ProgressDialog.show(THIS, "",
					message == "" ? getString(R.string.txt_progress) : message);
	}

	/**
	 * Dismiss progress dialog if showing
	 */
	public void dismissProgressDialog() {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}
}
