package com.bailcompany;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.AgentModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;

public class AgentProfile extends CustomActivity {

	private TextView name;
	private TextView email;
	private TextView username;
	private TextView address;
	private TextView phoneNumber;
	private TextView licenseNumber;
	private TextView licenseExpire;
	private TextView approvalStatus;
	private TextView insurance;
	private TextView states;
	private AgentModel agentDetail;
	private ImageView imgProfile;
	static Bitmap bmCompany;
	String stList = "";
	TextView chatBtn;
	String isOnline = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agent_profile);
		agentDetail = (AgentModel) getIntent().getSerializableExtra("agent");
		chatBtn = (TextView) findViewById(R.id.btn_chat);
		setActionBar();
		initValues();
		setValues();
		Log.d("user",
				agentDetail.getUsername() + "===" + agentDetail.getEmail());
		imgProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				F1.newInstance().show(getFragmentManager(), null);

			}
		});
		chatBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(AgentProfile.this,
						IndividualChatActivity.class)
						.putExtra("agentId", agentDetail.getAgentId())
						.putExtra("agentName", agentDetail.getAgentName())
						.putExtra("agentImage", agentDetail.getPhotoUrl())
						.putExtra("agentOnline", agentDetail.getIsOnline()));

			}
		});

	}

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.ag_profile));
	}

	private void initValues() {
		name = (TextView) findViewById(R.id.ag_name);
		email = (TextView) findViewById(R.id.ag_email);
		username = (TextView) findViewById(R.id.ag_uname);
		address = (TextView) findViewById(R.id.ag_address);
		phoneNumber = (TextView) findViewById(R.id.ag_phone);
		licenseNumber = (TextView) findViewById(R.id.ag_lic_no);
		licenseExpire = (TextView) findViewById(R.id.ag_state_expire);//
		approvalStatus = (TextView) findViewById(R.id.ag_approval);
		insurance = (TextView) findViewById(R.id.ag_insure);
		states = (TextView) findViewById(R.id.ag_states_applicable);//

		Log.e("insurance", "=" + insurance.getText().toString());
		Log.e("states", "=" + states.getText().toString());

		imgProfile = (ImageView) findViewById(R.id.img_profile);
	}

	private void setValues() {
		name.setText(Utils.getFormattedText(agentDetail.getAgentName()));
		email.setText(Utils.getFormattedText(agentDetail.getEmail()));
		username.setText(Utils.getFormattedText(agentDetail.getUsername()));
		address.setText(Utils.getFormattedText(agentDetail.getAddress())
				+ (Commons.isEmpty(agentDetail.getCity()) ? "" : ", "
						+ agentDetail.getCity())
				+ (Commons.isEmpty(agentDetail.getState()) ? "" : ", "
						+ agentDetail.getState()));
		phoneNumber.setText(Utils.getFormattedText("+1"
				+ agentDetail.getPhone()));
		licenseNumber
				.setText(Utils.getFormattedText(agentDetail.getLicenseNo()));

		String lState = Utils.getFormattedText(agentDetail.getLicenseState());
		String lExp = Commons.extractDate(agentDetail.getLicenseExpire());

		String lic = lState;
		if (Commons.isEmpty(lState))
			lic = lExp;

		else if (Commons.isEmpty(lExp)) {
			lic = lState;
		} else
			lic = lState + " | " + lExp;

		// lic=stList+" | "+lExp;

		// licenseExpire.setText(lic);
		Log.e("rakeshlic ", "lic" + lic);
		approvalStatus.setText(Utils.getFormattedText(agentDetail
				.getAgentApprovalStatus()));

		ArrayList<String> ins = agentDetail.getInsuranceList();
		String insList = "";
		if (ins != null && ins.size() > 0) {
			for (int i = 0; i < ins.size(); i++) {
				if (i != 0)
					insList += "\n";
				insList += ins.get(i);
			}
		}
		/*
		 * if(ins.contains("[")) ins=ins.replace("[", ""); if(ins.contains("]"))
		 * ins=ins.replace("]", "");
		 */
		insurance.setText(insList);

		ArrayList<String> statesName = agentDetail.getStatesName();
		// String stList="";
		if (statesName != null && statesName.size() > 0) {
			for (int i = 0; i < statesName.size(); i++) {
				if (i != 0)
					stList += "\n";
				stList += statesName.get(i);

				lic = stList + "  |  " + lExp; // *******editing*******

				licenseExpire.setText(lic);
			}
		}
		/*
		 * if(statesName.contains("[")) statesName=statesName.replace("[", "");
		 * if(statesName.contains("]")) statesName=statesName.replace("]", "");
		 */

		states.setText(stList);

		Bitmap bm = new ImageLoader(StaticData.getDIP(100),
				StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
				agentDetail.getPhotoUrl(), new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap bm) {
						if (bm != null)
							bmCompany = ImageUtils.getCircularBitmap(bm);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	public static class F1 extends DialogFragment {

		public static F1 newInstance() {
			F1 f1 = new F1();
			f1.setStyle(DialogFragment.STYLE_NO_FRAME,
					android.R.style.Theme_DeviceDefault_Dialog);
			return f1;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// Remove the default background
			getDialog().getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));

			// Inflate the new view with margins and background
			View v = inflater.inflate(R.layout.popup_layout, container, false);
			ImageView bm = (ImageView) v.findViewById(R.id.bm_company);

			bm.setImageBitmap(bmCompany);

			// Set up a click listener to dismiss the popup if they click
			// outside
			// of the background view
			v.findViewById(R.id.popup_root).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dismiss();
						}
					});

			return v;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	void hideKeyboard() {
		View view = THIS.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) THIS
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideKeyboard();
	}
}
