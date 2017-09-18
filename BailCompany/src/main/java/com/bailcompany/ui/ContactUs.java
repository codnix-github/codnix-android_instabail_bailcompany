package com.bailcompany.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class ContactUs extends CustomFragment {

	private EditText name, email, query;
	private Button send;

	static int getCallTimeout = 50000;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	private Spinner selectCountryCode;
	private EditText telNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.contact_us, null);
		setHasOptionsMenu(true);
		initValues(v);
		setTouchNClick(v.findViewById(R.id.call));
		setTouchNClick(send);
		return v;
	}

	private void initValues(View v) {
		name = (EditText) v.findViewById(R.id.contact_name);
		email = (EditText) v.findViewById(R.id.contact_email);
		query = (EditText) v.findViewById(R.id.query);
		send = (Button) v.findViewById(R.id.send_contact);
		selectCountryCode = (Spinner) v.findViewById(R.id.sel_country_code);
		telNumber = (EditText) v.findViewById(R.id.tel_number);
		selectCountryCode
				.setAdapter(((MainActivity) getActivity())
						.getAdapter(((MainActivity) getActivity())
								.getCountryCodeList()));
		phoneFormat(telNumber);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.call)
			CallDialog.show(getActivity());
		if (v.getId() == R.id.send_contact) {
			if (isValid()) {
				if (Utils.isOnline(getActivity()))
					sendQuery();
				else
					Utils.noInternetDialog(getActivity());
			}
		}
	}

	private void phoneFormat(final EditText ph) {
		ph.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					int maxLengthofEditText = 12;
					ph.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLengthofEditText) });

					String str = PhoneNumberUtils.formatNumber("+1"
							+ ph.getText().toString());
					ph.setText(str.replace("+1-", "").replace("+1", ""));

					Log.e("ph.getText().toString()", ""
							+ ph.getText().toString());
				} else {
					String str = (ph.getText().toString());
					ph.setText(str.replace("-", ""));
					int maxLengthofEditText = 10;
					ph.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLengthofEditText) });
				}
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

	private boolean isValid() {
		if (name.getText().toString().length() == 0)
			Utils.showDialog(getActivity(), "Please enter Name !");
		else {
			if (!Utils.isValidEmail(email.getText().toString()))
				Utils.showDialog(getActivity(), "Please enter valid email !");
			else if (query.getText().toString().length() == 0)
				Utils.showDialog(getActivity(), "Please leave a query !");
			else
				return true;
		}
		return false;
	}

	// private void sendQuery() {
	// ((MainActivity) getActivity()).showProgressDialog("");
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// response = WebAccess.contactUs(email.getText().toString(), name
	// .getText().toString(), query.getText().toString());
	// getActivity().runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// ((MainActivity) getActivity()).dismissProgressDialog();
	// if (!Commons.isEmpty(response))
	// Utils.showDialog(getActivity(),
	// "Your query has been sent , Thank You.",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(
	// DialogInterface arg0, int arg1) {
	// // TODO Auto-generated method stub
	// getActivity().finish();
	// }
	// });
	// else
	// Utils.showDialog(getActivity(),
	// R.string.err_unexpect);
	//
	// }
	// });
	// }
	// }).start();
	// }

	public void sendQuery() {
		showProgressDialog("");
		RequestParams param = new RequestParams();

		param.put("Name", name.getText().toString());
		param.put("Email", email.getText().toString().trim());
		param.put("PhoneNumber", selectCountryCode.getSelectedItem().toString()
				+ telNumber.getText().toString());
		param.put("Query", query.getText().toString());
		String url = WebAccess.MAIN_URL + WebAccess.CONTACT_US;
		client.setTimeout(getCallTimeout);

		client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


				dismissProgressDialog();

			}
			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


				try {
					dismissProgressDialog();
					String response2;
					response2 = new String(responseBody);
					if (response2 != null) {
						JSONObject json;

						json = new JSONObject(response2);
						if (json.optString("status").equalsIgnoreCase("1")) {
							name.setText("");
							email.setText("");
							telNumber.setText("");
							query.setText("");
						}
						message = json.optString("message");

						Utils.showDialog(getActivity(), message);

					} else
						Utils.showDialog(getActivity(), R.string.err_unexpect);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}
}
