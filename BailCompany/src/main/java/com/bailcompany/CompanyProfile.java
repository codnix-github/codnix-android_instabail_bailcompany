package com.bailcompany;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.User;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

public class CompanyProfile extends CustomActivity {
	private User user;
	private TextView name;
	private TextView companyName;
	private TextView email;

	private TextView address;
	private TextView licenseNo;
	private TextView phone;
	private TextView insur;
	private TextView state;
	private ImageView imgProfile;
	private TextView btnMessage;
	private TextView btnCall;

	String companyId;
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	static ProgressDialog pd;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String response;
	Button payment;
	TextView chatBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_profile);
		payment = (Button) findViewById(R.id.pay);
		chatBtn = (TextView) findViewById(R.id.btn_chat);
		setActionBar();
		initViews();
		setValues();
		if (!MainActivity.user.getCompanyId().equalsIgnoreCase(
				user.getCompanyId())) {
			payment.setVisibility(View.GONE);

		} else {
			chatBtn.setVisibility(View.GONE);
		}
		payment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(CompanyProfile.this, PaymentInfo.class));

			}
		});
		chatBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(CompanyProfile.this,
						IndividualChatActivity.class)
						.putExtra("companyId", user.getCompanyId())
						.putExtra("companyName", user.getCompanyName())
						.putExtra("companyImage", user.getPhoto())
						.putExtra("companyOnline", "1"));

			}
		});
	}

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.profile));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideKeyboard();
	}

	private void initViews() {
		name = (TextView) findViewById(R.id.company_owner_name);
		companyName = (TextView) findViewById(R.id.company_name);
		email = (TextView) findViewById(R.id.company_email);
		address = (TextView) findViewById(R.id.company_addr);
		licenseNo = (TextView) findViewById(R.id.company_licence);
		phone = (TextView) findViewById(R.id.company_phone);
		insur = (TextView) findViewById(R.id.company_insur);
		state = (TextView) findViewById(R.id.company_state);
		imgProfile = (ImageView) findViewById(R.id.img_profile);

	}

	void hideKeyboard() {
		View view = THIS.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) THIS
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void setValues() {
		user = (User) getIntent().getSerializableExtra("user");
		name.setText(Utils.getFormattedText(user.getName()));
		companyName.setText(Utils.getFormattedText(user.getCompanyName()));
		email.setText(Utils.getFormattedText(user.getEmail()));
		btnMessage = (TextView) findViewById(R.id.btnMessage);
		btnCall = (TextView) findViewById(R.id.btnCall);
		address.setText(Utils.getFormattedText(user.getAddress())
				+ (Commons.isEmpty(user.getCity()) ? "" : ", " + user.getCity())
				+ (Commons.isEmpty(user.getStatesName()) ? "" : ", "
						+ user.getState()));
		licenseNo.setText(Utils.getFormattedText(user.getLicenseno()));
		phone.setText(Utils.getFormattedText("+1" + user.getPhone()));
		btnMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				Utils.sendMessage(CompanyProfile.this, "+1" + user.getPhone());

			}
		});
		btnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				Utils.makeCall(CompanyProfile.this, "+1" + user.getPhone());

			}
		});

		ArrayList<String> ins = user.getInsurancesName();
		String insList = "";
		if (ins != null && ins.size() > 0) {
			for (int i = 0; i < ins.size(); i++) {
				if (i != 0)
					insList += "\n";
				insList += ins.get(i);
			}
		}
		insur.setText(insList);

		ArrayList<String> sta = user.getStatesName();
		Log.e("sta", "sta=" + sta);
		String staa = "";
		if (sta != null && sta.size() > 0) {
			for (int i = 0; i < sta.size(); i++) {
				if (i != 0)
					staa += "\n";
				staa += sta.get(i);
			}
		}
		state.setText(staa);

		Bitmap bm = new ImageLoader(StaticData.getDIP(110),
				StaticData.getDIP(110), ImageLoader.SCALE_FITXY).loadImage(
				user.getPhoto(), new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap bm) {
						if (bm != null)
							imgProfile.setImageBitmap(ImageUtils
									.getCircularBitmap(bm));
					}
				});
		if (bm != null)
			imgProfile.setImageBitmap(ImageUtils.getCircularBitmap(bm));
		else
			imgProfile.setImageBitmap(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
}
