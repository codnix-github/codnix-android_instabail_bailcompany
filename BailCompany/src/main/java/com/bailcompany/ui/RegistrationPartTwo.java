package com.bailcompany.ui;

import java.io.File;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.User;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegistrationPartTwo extends CustomFragment {

	private EditText cardNumber;
	private TextView disclaimer;
	private EditText mm;
	private EditText yy;
	private EditText cvv;
	private EditText nameOnCard;
	private Spinner countryList;
	private Button register;
	User user;
	private String response;
	static int getCallTimeout = 50000;
	int i;
	String response2;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.credit_card_details, null);
		setActionBar();
		user = (User) getArguments().getSerializable("user");
		cardNumber = (EditText) v.findViewById(R.id.card_number);
		disclaimer = (TextView) v.findViewById(R.id.disclaimer);
		mm = (EditText) v.findViewById(R.id.mm);
		yy = (EditText) v.findViewById(R.id.yyyy);
		cvv = (EditText) v.findViewById(R.id.cvv);
		nameOnCard = (EditText) v.findViewById(R.id.name_on_card);
		setTouchNClick(v.findViewById(R.id.btn_register));
		disclaimer.setText(Html
				.fromHtml("<b>Disclaimer :</b> <font color=#7a7a7a>"
						+ getString(R.string.disclaimer) + ""));
		return v;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == v.findViewById(R.id.btn_register)) {
			if (isValid()) {
				user.setCardNumber(cardNumber.getText().toString());
				user.setCardExpiry(yy.getText().toString() + "-"
						+ mm.getText().toString());
				user.setCVV(cvv.getText().toString());
				user.setNameOnCard(nameOnCard.getText().toString());
				if (Utils.isOnline(getActivity()))
					doRegister();
				else
					Utils.noInternetDialog(getActivity());
			}
		}
	}

	private void setActionBar() {
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(getString(R.string.credit_detail));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	private boolean isValid() {
		// if (cardNumber.getText().toString().length() == 0
		// || mm.getText().toString().length() == 0
		// || yy.getText().toString().length() == 0
		// || cvv.getText().toString().length() == 0
		// || nameOnCard.getText().toString().length() == 0) {
		// Toast.makeText(getActivity(),
		// "All fields are required to be filled.", Toast.LENGTH_LONG)
		// .show();
		// return false;
		// }
		if (Integer.parseInt(mm.getText().toString()) > 12) {
			Toast.makeText(getActivity(), "Invalid months detail !",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (Integer.parseInt(yy.getText().toString()) > 2090) {
			Toast.makeText(getActivity(), "Invalid Year detail !",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void doRegister() {
		showProgressDialog("");
		RequestParams param = new RequestParams();

		param.put("CVV", user.getCVV());
		param.put("CardExpiry", user.getCardExpiry());
		param.put("CardNumber", user.getCardNumber());
		param.put("CardType", "visa");
		param.put("CompanyName", user.getCompanyName());

		// param.put("NameOnCard", user.getNameOnCard());
		param.put("ZipCode", user.getZipCode());
		param.put("address", user.getAddress());
		param.put("city", user.getCity());
		param.put("country", user.getCountry());
		param.put("email", user.getEmail());
		param.put("licenseno", user.getLicenseno());
		param.put("name", user.getName());
		param.put("password", user.getPassword());
		param.put("phone", user.getPhone());
		// param.put("photo",
		// Commons.isEmpty(user.getPhoto()) ? "" : user.getPhoto());
		param.put("state", "LA");

		// StateModel sm = RegistrationPartOne.insListState
		// .get(RegistrationPartOne.selectedState);
		// param.put("States[0]", sm.getId());
		param.put("username", user.getUsername());
		if (user.getInsurance() != null) {
			for (int s = 0; s < user.getInsurance().size(); s++) {
				String insurance = user.getInsurance().get(s);
				param.put("Insurance[" + s + "]", insurance);
			}

		}
		if (RegistrationPartOne.imgIS != null) {
			param.put("photo", RegistrationPartOne.imgIS, "mfile.jpg",
					"image/jpg");
		}

		String url = WebAccess.MAIN_URL + WebAccess.REGISTER_URL;
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
				dismissProgressDialog();
				try {
					response2 = new String(responseBody);
					if (response2 != null) {
						deleteFile("/storage/emulated/0/temp.jpg");
						JSONObject json = new JSONObject(response2);
						if (json.optString("status").equalsIgnoreCase("1")) {

							Utils.showDialog(getActivity(),
									"Registration Successfull",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											getActivity().finish();

										}
									});
						} else {
							Utils.showDialog(getActivity(),
									json.optString("message"));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// response = WebAccess.doRegister(user);
		// getActivity().runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// ((Register) getActivity()).dismissProgressDialog();
		// if (!Commons.isEmpty(response)
		// && response.equalsIgnoreCase("done")) {
		// Utils.showDialog(getActivity(),
		// "Registration Successfull",
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(
		// DialogInterface dialog,
		// int which) {
		// getActivity().finish();
		//
		// }
		// });
		//
		// } else
		// Utils.showDialog(getActivity(), response);
		// }
		// });
		//
		// }
		// }).start();
	}

	public boolean deleteFile(String selectedFilePath) {
		File file = new File(selectedFilePath);
		boolean deleted = false;
		if (file.exists())
			deleted = file.delete();

		return deleted;
	}
}
