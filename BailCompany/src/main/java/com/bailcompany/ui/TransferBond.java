package com.bailcompany.ui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bailcompany.DefendantSelectionActivity;
import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.GetAnAgentModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.StateModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class TransferBond extends CustomFragment implements
		OnCheckedChangeListener {

	private Spinner selectState;
	private Spinner selectInsurence;

	private Spinner selectCountryCode;

	private CheckBox chkCourtFill;
	private CheckBox chkBailSource;
	private CheckBox chkCallAgency;
	private CheckBox chkTaxFree;
	private CheckBox chkNoPaper;
	private CheckBox chkIndPaper;
	private CheckBox chkDefendantPaper;
	private RadioButton radioPaymentAlr;
	private RadioButton radioPaymentToBe;
	public static String location_entered;
	private EditText defFName;
	private EditText defLName;
	private EditText SSN;
	private EditText bookingNumber;
	private EditText editAmountTo;
	private EditText editPaymentPlan;
	private EditText instruction;
	private EditText indFName;
	private EditText indLName;
	private EditText indPhone;
	static EditText dateOfBirth;
	private Button addMore, btnSubmit;
	private LinearLayout warrentLayout;
	private AutoCompleteTextView location;
	private ArrayList<GetAnAgentModel> listWarrent;
	private ArrayList<GetAnAgentModel> listIndemnitors;
	private String locLatt = "0.0", locLng = "0.0";
	ArrayList<String[]> resList;
	private LinearLayout indDetail;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	private Button addMoreIndemnitors;
	private ArrayList<String> selectedItems;
	private boolean[] chkItems;
	ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
	ArrayList<StateModel> insListState = new ArrayList<StateModel>();
	private EditText warAmount;
	private EditText warTown;
	private EditText amountCompamnyOffer;
	private RadioButton rbNewDefendant, rbExistingDefendant;
	private String defId = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.transfer_bond, null);
		setHasOptionsMenu(true);
		initValues(v);
		loadValues();
		loadFunction(v);
		setTouchNClick(addMore);
		setTouchNClick(dateOfBirth);
		setTouchNClick(btnSubmit);

		return v;
	}

	private void initValues(View v) {
		selectState = (Spinner) v.findViewById(R.id.state_sel);
		selectInsurence = (Spinner) v.findViewById(R.id.Insur_sel);
		addMoreIndemnitors = (Button) v.findViewById(R.id.add_more_ind);
		selectCountryCode = (Spinner) v.findViewById(R.id.sel_country_code);
		dateOfBirth = (EditText) v.findViewById(R.id.dob);
		location = (AutoCompleteTextView) v.findViewById(R.id.location_def);
		chkCourtFill = (CheckBox) v.findViewById(R.id.chk1);
		chkBailSource = (CheckBox) v.findViewById(R.id.chk2);
		chkCallAgency = (CheckBox) v.findViewById(R.id.chk3);
		// chkTaxFree = (CheckBox) v.findViewById(R.id.chk4);
		chkNoPaper = (CheckBox) v.findViewById(R.id.txt5);
		chkIndPaper = (CheckBox) v.findViewById(R.id.txt6);
		chkDefendantPaper = (CheckBox) v.findViewById(R.id.txt7);

		radioPaymentAlr = (RadioButton) v.findViewById(R.id.radio1);
		radioPaymentToBe = (RadioButton) v.findViewById(R.id.radio2);
		indDetail = (LinearLayout) v.findViewById(R.id.ind_details);
		defFName = (EditText) v.findViewById(R.id.def_fname);
		defLName = (EditText) v.findViewById(R.id.def_lname);
		SSN = (EditText) v.findViewById(R.id.ssn);
		bookingNumber = (EditText) v.findViewById(R.id.bkng_num);
		instruction = (EditText) v.findViewById(R.id.instructions);
		indFName = (EditText) v.findViewById(R.id.ind_fname);
		indLName = (EditText) v.findViewById(R.id.ind_lname);
		indPhone = (EditText) v.findViewById(R.id.ind_phone);
		editAmountTo = (EditText) v.findViewById(R.id.edit_amount_to);
		amountCompamnyOffer = (EditText) v.findViewById(R.id.companyOffer);
		editPaymentPlan = (EditText) v.findViewById(R.id.edit_payment_plan);
		rbNewDefendant = (RadioButton) v.findViewById(R.id.rbNewDefendant);
		rbExistingDefendant = (RadioButton) v.findViewById(R.id.rbExistingDefendant);

		rbExistingDefendant.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					startActivityForResult(
							new Intent(getActivity(),
									DefendantSelectionActivity.class), 5555);
				}
			}
		});
		rbNewDefendant.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					defId = "";
				}
			}
		});

		addMore = (Button) v.findViewById(R.id.add_more);
		btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
		warrentLayout = (LinearLayout) v.findViewById(R.id.warrent_layout);
		location.setAdapter(new PlacesAdaper(getActivity(),
				android.R.layout.simple_list_item_1));
		getAllStates();
		getAllInsurances();
		radioPaymentAlr.setOnCheckedChangeListener(this);
		radioPaymentToBe.setOnCheckedChangeListener(this);
		editAmountTo.setEnabled(false);
		editPaymentPlan.setEnabled(false);
		phoneFormat(indPhone);
		location.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				final String[] place = resList.get(pos);
				Log.e("Place", place[0] + "==========" + place[1]);

				showProgressDialog("");
				new Thread(new Runnable() {
					public void run() {
						final String[] latLng = Utils.getLocationLatLng(
								getString(R.string.api_key), place[1]);
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								dismissProgressDialog();
								locLatt = latLng[0];
								locLng = latLng[1];
								Log.e("*****locLng", "locLatt=" + locLatt
										+ " locLng=" + locLng);
							}
						});
					}
				}).start();
			}
		});

		editAmountTo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(2);
				nf.setMinimumFractionDigits(2);
				if (!hasFocus)
					editAmountTo.setText(nf.format(Commons
							.strToDouble(editAmountTo.getText().toString())));

				if (hasFocus
						&& editAmountTo.getText().toString().startsWith("$")) {
					editAmountTo.setText(editAmountTo.getText().toString()
							.replace("$", ""));
				} else if (!hasFocus
						&& !editAmountTo.getText().toString().startsWith("$")) {
					editAmountTo.setText("$"
							+ editAmountTo.getText().toString());
				}
			}
		});
		amountCompamnyOffer
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						NumberFormat nf = NumberFormat.getNumberInstance();
						nf.setMaximumFractionDigits(2);
						nf.setMinimumFractionDigits(2);
						if (!hasFocus)
							amountCompamnyOffer.setText(nf.format(Commons
									.strToDouble(amountCompamnyOffer.getText()
											.toString())));

						if (hasFocus
								&& amountCompamnyOffer.getText().toString()
										.startsWith("$")) {
							amountCompamnyOffer.setText(amountCompamnyOffer
									.getText().toString().replace("$", ""));
						} else if (!hasFocus
								&& !amountCompamnyOffer.getText().toString()
										.startsWith("$")) {
							amountCompamnyOffer.setText("$"
									+ amountCompamnyOffer.getText().toString());
						}
					}
				});
		addWarrant(false);
		setTouchNClick(addMoreIndemnitors);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			String key = data.getStringExtra(Const.RETURN_FLAG);
			if (key.equalsIgnoreCase(Const.RETURN_DEFENDANT_DETAIL)) {

				DefendantModel defModel = (DefendantModel) data.getSerializableExtra(Const.EXTRA_DATA);
				if (defModel != null) {
					defId = defModel.getId();
					defFName.setText(defModel.getFirstName());
					defLName.setText(defModel.getLastName());
					SSN.setText(defModel.getSSN());
					dateOfBirth.setText(defModel.getDOB());
				} else {
					defId = "";
				}

			}
		}
	}

	void getInsurance() {

		for (int i = 0; i < MainActivity.user.getInsurance().size(); i++) {
			InsuranceModel model = new InsuranceModel();
			model.setId(Integer.parseInt(MainActivity.user.getInsurance()
					.get(i)));
			model.setName(MainActivity.user.getInsurancesName().get(i));
			insList.add(model);
		}
	}

	private void loadValues() {
		ArrayList<String> l = new ArrayList<String>();
		l.add("Select Insurance");
		for (int i = 0; i < insList.size(); i++) {
			InsuranceModel ins = insList.get(i);
			l.add(ins.getName());

		}
		ArrayList<String> s = new ArrayList<String>();
		s.add("Select State");
		for (int i = 0; i < insListState.size(); i++) {
			StateModel ins = insListState.get(i);
			s.add(ins.getName());

		}
		if (l != null) {
			selectInsurence.setAdapter(((MainActivity) getActivity())
					.getAdapter(l));
		}
		if (s != null) {
			selectState
					.setAdapter(((MainActivity) getActivity()).getAdapter(s));
		}
		selectCountryCode
				.setAdapter(((MainActivity) getActivity())
						.getAdapter(((MainActivity) getActivity())
								.getCountryCodeList()));
	}

	private void loadFunction(View v) {
		setClick(v.findViewById(R.id.txt1));
		setClick(v.findViewById(R.id.txt2));
		setClick(v.findViewById(R.id.txt3));
		// setClick(v.findViewById(R.id.txt4));
		// setClick(v.findViewById(R.id.txt5));
		// setClick(v.findViewById(R.id.txt6));
		// setClick(v.findViewById(R.id.txt7));
		setClick(v.findViewById(R.id.radio1));
		setClick(v.findViewById(R.id.radio2));
		setClick(chkNoPaper);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		switch (v.getId()) {
		case R.id.txt1:
			if (chkCourtFill.isChecked())
				chkCourtFill.setChecked(false);
			else
				chkCourtFill.setChecked(true);
			break;
		case R.id.txt2:
			if (chkBailSource.isChecked())
				chkBailSource.setChecked(false);
			else
				chkBailSource.setChecked(true);
			break;
		case R.id.txt3:
			if (chkCallAgency.isChecked())
				chkCallAgency.setChecked(false);
			else
				chkCallAgency.setChecked(true);
			break;
		// case R.id.txt4:
		// if (chkTaxFree.isChecked())
		// chkTaxFree.setChecked(false);
		// else
		// chkTaxFree.setChecked(true);
		// break;
		case R.id.txt5:
			// if (chkNoPaper.isChecked())
			// chkNoPaper.setChecked(false);
			// else
			// chkNoPaper.setChecked(true);
			boolean checked = ((CheckBox) v).isChecked();
			if (checked) {
				chkIndPaper.setChecked(false);
				chkDefendantPaper.setChecked(false);
				// chkIndPaper.setChecked(false);
				// chkDefendantPaper.setChecked(false);
				chkIndPaper.setEnabled(false);
				chkDefendantPaper.setEnabled(false);

			} else {
				// chkNoPaper.setChecked(true);
				chkIndPaper.setEnabled(true);
				chkDefendantPaper.setEnabled(true);

				// chkIndPaper .setEnabled(false);
				// chkDefendantPaper .setEnabled(false);
			}
			break;
		// case R.id.txt6:
		// if (chkIndPaper.isChecked())
		// chkIndPaper.setChecked(false);
		// else
		// chkIndPaper.setChecked(true);
		// break;
		// case R.id.txt7:
		// if (chkDefendantPaper.isChecked())
		// chkDefendantPaper.setChecked(false);
		// else
		// chkDefendantPaper.setChecked(true);
		// break;
		case R.id.radio1:

			editAmountTo.setEnabled(false);
			editPaymentPlan.setEnabled(false);

			break;
		case R.id.radio2:

			editAmountTo.setEnabled(true);
			editPaymentPlan.setEnabled(true);

			break;
		case R.id.dob:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
			break;
		case R.id.add_more:
			addWarrant(true);
			break;
		case R.id.add_more_ind:
			if (indDetail.getChildCount() <= 5) {
				final View indView = inflater.inflate(
						R.layout.indemnitor_layout, null);
				Spinner code = (Spinner) indView
						.findViewById(R.id.sel_country_code);
				code.setAdapter(((MainActivity) getActivity())
						.getAdapter(((MainActivity) getActivity())
								.getCountryCodeList()));
				// code.setEnabled(false);
				ImageButton removeInd = (ImageButton) indView
						.findViewById(R.id.remove);
				removeInd.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((LinearLayout) indView.getParent())
								.removeView(indView);

					}
				});
				indDetail.addView(indView);
				phoneFormat((EditText) indView.findViewById(R.id.ind_phone));
			}
			break;
		case R.id.btnSubmit:
			View view = getActivity().getCurrentFocus();
			if (view != null) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			if (isValid() && getWarrents() && getIndemnitors() && Radio()
					&& Checked()) {
				if (Utils.isOnline(getActivity()))
					transferBond();
				else
					Utils.noInternetDialog(getActivity());
			}
			break;
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

	// ********************************//editing1
	private void addWarrant(boolean showCancel) {
		// warrentLayout.removeAllViews();
		final View warrent = getActivity().getLayoutInflater().inflate(
				R.layout.add_warrent_layout, null);
		if (!showCancel)
			warrent.findViewById(R.id.remove).setVisibility(View.GONE);
		final EditText warAmount = (EditText) warrent.findViewById(R.id.amount);

		warAmount.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(2);
				nf.setMinimumFractionDigits(2);
				if (!hasFocus)
					warAmount.setText(Commons.strToDouble(warAmount.getText()
							.toString()) + "");

				Log.d("Amount", hasFocus + "==="
						+ warAmount.getText().toString());
				if (hasFocus && warAmount.getText().toString().startsWith("$")) {
					warAmount.setText(warAmount.getText().toString()
							.replace("$", ""));
				} else if (!hasFocus
						&& !warAmount.getText().toString().startsWith("$")) {
					String str = warAmount.getText().toString();

					warAmount.setText("$" + nf.format(Double.parseDouble(str)));

					/*
					 * boolean done = false;
					 * if(warAmount.getText().toString().length() == 0) {
					 * warAmount.setError("Please enter amount !");
					 * if(phone.getText().toString().length() == 0)
					 * phone.setError("enter number !"); done = false; //break;
					 * } else { done = true; warAmount.setError(null);
					 * phone.setError(null);
					 * model.setIndemnitorName(indName.getText().toString());
					 * model
					 * .setIndemnitorPhone(code.getSelectedItem().toString()
					 * +phone.getText().toString()); listIndemnitors.add(model);
					 * }
					 */

					/*
					 * Log.e("s",""+"$"+nf.format(Double.parseDouble(str)));
					 * //warAmount.setText("$"+warAmount.getText().toString());
					 * Log.e("amount2",""+"$"+warAmount.getText().toString());
					 * Log.e("amount3",""+warAmount.getText().toString()+"0");
					 */
				}
			}
		});

		ImageButton remove = (ImageButton) warrent.findViewById(R.id.remove);
		remove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((LinearLayout) warrent.getParent()).removeView(warrent);
			}
		});

		warrentLayout.addView(warrent);
	}

	private void transferBond() {
		if (!rbExistingDefendant.isChecked()) {
			defId = "";
		}

		showProgressDialog("");
		Date d = null;
		String date = "";
		SimpleDateFormat formatter;
		try {
			d = new Date();
			d = new SimpleDateFormat("MM/dd/yyyy").parse(dateOfBirth.getText()
					.toString());
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			date = formatter.format(d);

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String amount = editAmountTo.getText().toString().trim();
		String amountoffer = amountCompamnyOffer.getText().toString().trim();
		RequestParams param = new RequestParams();
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		param.put("DefendantName", defFName.getText().toString()+" " +defLName.getText().toString());
		param.put("firstname", defFName.getText().toString());
		param.put("lastname", defLName.getText().toString());
		param.put("DefDOB", date);
		param.put("LocationLongitude", locLng);
		param.put("LocationLatitude", locLatt);
		param.put("DefSSN", SSN.getText().toString());
		param.put("DefBookingNumber", bookingNumber.getText().toString());
		param.put("NeedCourtFee", chkCourtFill.isChecked() ? 1 : 0);
		param.put("NeedBailSource", chkBailSource.isChecked() ? 1 : 0);
		param.put("IsCallAgency", chkCallAgency.isChecked() ? 1 : 0);
		param.put("NumberIndemnitors", indDetail.getChildCount());
		param.put("NeedPaperWork", chkNoPaper.isChecked() ? 0 : 1);
		param.put("NeedIndemnitorPaperwork", chkIndPaper.isChecked() ? 1 : 0);
		param.put("NeedDefendantPaperwork", chkDefendantPaper.isChecked() ? 1
				: 0);
		param.put("PaymentAlreadyReceived", radioPaymentAlr.isChecked() ? 1 : 0);
		param.put("CompanyOfferToPay", Commons.isEmpty(amountoffer) ? 0
				: (amountoffer.startsWith("$") ? amountoffer.replace("$", "")
						.replace(",", "") : amountoffer));
		if (!radioPaymentAlr.isChecked()) {
			param.put("AmountToCollect",
					Commons.isEmpty(amount) ? 0
							: (amount.startsWith("$") ? amount.replace("$", "")
									.replace(",", "") : amount));
			param.put("PaymentPlan", editPaymentPlan.getText().toString());
		}
		param.put("InstructionForAgent", instruction.getText().toString());

		param.put("Location", location_entered);
		for (int i = 0; i < listWarrent.size(); i++) {
			param.put("Amount[" + i + "]", listWarrent.get(i)
					.getWarrentAmount());
			param.put("Township[" + i + "]", listWarrent.get(i)
					.getCharginTown());

		}
		for (int i = 0; i < listIndemnitors.size(); i++) {
			param.put("Name[" + i + "]", listIndemnitors.get(i)
					.getIndemnitorName());
			param.put("ind_firstname[" + i + "]", listIndemnitors.get(i)
					.getIndemnitorFName());
			param.put("ind_lastname[" + i + "]", listIndemnitors.get(i)
					.getIndemnitorLName());
			param.put("PhoneNumber[" + i + "]", listIndemnitors.get(i)
					.getIndemnitorPhone());
		}
		StateModel sm = insListState
				.get(selectState.getSelectedItemPosition() - 1);

		param.put("TransferBondState", sm.getId());
		param.put("DefId", defId);

		int i = selectInsurence.getSelectedItemPosition();
		if (i != 0) {
			i = i - 1;
			InsuranceModel insur = insList.get(i);
			param.put("TransferBondInsurance", insur.getId());
		}

		String url = WebAccess.MAIN_URL + WebAccess.TRANSFER_BOND;
		client.setTimeout(getCallTimeout);
		client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				dismissProgressDialog();
			}

			@SuppressLint("ShowToast")
			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				dismissProgressDialog();
				try {
					String response2;
					response2 = new String(responseBody);
					if (response2 != null) {
						JSONObject json = new JSONObject(response2);
						if (json.optString("status").equalsIgnoreCase("1")) {
							message = json.optString("message");
							showDialog(getActivity(), json.optString("message"));

						} else if (json.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(getActivity(),
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(getActivity(), Login.class));
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
	}

	public AlertDialog showDialog(Context ctx, String msg)// ///hello
	{

		return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				dialog.dismiss();
				getFragmentManager().popBackStack();
				MainActivity.drawerLayout.openDrawer(MainActivity.drawerLeft);
			}
		});

	}

	public static AlertDialog showDialog(Context ctx, String msg,
			DialogInterface.OnClickListener listener) {

		return showDialog(ctx, msg, ctx.getString(android.R.string.ok), null,
				listener, null);
	}

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

	private boolean Checked() {
		if (chkNoPaper.isChecked() || chkIndPaper.isChecked()
				|| chkDefendantPaper.isChecked()) {
			return true;
		} else {
			Utils.showDialog(getActivity(), "Please select paperwork !");
			return false;
		}

	}

	private boolean Radio() {
		if (!radioPaymentAlr.isChecked() && !radioPaymentToBe.isChecked()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Please select payment status !")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			AlertDialog alert = builder.create();
			alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alert.show();
			return false;
		}

		else if (radioPaymentToBe.isChecked()) {
			// /if(editAmountTo.length()==0)
			if (editAmountTo.getText().toString().length() == 0) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(" Enter amount to collect !")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alert = builder.create();
				alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alert.show();
				return false;
			}

			/*
			 * else if(editPaymentPlan.getText().toString().length() == 0) {
			 * 
			 * AlertDialog.Builder builder1 = new
			 * AlertDialog.Builder(getActivity());
			 * builder1.setMessage("Enter payment plan.!") .setCancelable(false)
			 * .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 * public void onClick(DialogInterface dialog, int id) { //
			 * finish(); dialog.cancel(); } }) ;
			 * 
			 * AlertDialog alert1 = builder1.create();
			 * alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 * alert1.show(); return false; }
			 */
			else {
			}

		}
		return true;

	}

	private boolean getIndemnitors() {

		listIndemnitors = new ArrayList<GetAnAgentModel>();
		for (int i = 0; i < indDetail.getChildCount(); i++) {

			GetAnAgentModel model = new GetAnAgentModel();
			LinearLayout main1 = (LinearLayout) indDetail.getChildAt(i);
			LinearLayout main = (LinearLayout) main1.getChildAt(0);
			EditText indFName = (EditText) main.getChildAt(0);
			EditText indLName = (EditText) main.getChildAt(1);
			LinearLayout l = (LinearLayout) main.getChildAt(2);
			Spinner code = (Spinner) l.getChildAt(0);
			EditText phone = (EditText) l.getChildAt(2);
			// **********************************************
			if (indFName.getText().toString().length() == 0) {

				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// getActivity());
				// builder.setMessage("Enter indemnitor's name !")
				// .setCancelable(false)
				// .setPositiveButton("OK",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int id) {
				// dialog.cancel();
				// }
				// });
				//
				// AlertDialog alert = builder.create();
				// alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
				// alert.show();
				return true;
			}

			else {

				model.setIndemnitorName(indFName.getText().toString()+" "+indLName.getText().toString());
				model.setIndemnitorFName(indFName.getText().toString());
				model.setIndemnitorLName(indLName.getText().toString());
				model.setIndemnitorPhone(code.getSelectedItem().toString()
						+ phone.getText().toString());
				listIndemnitors.add(model);
			}
		}
		return true;
	}

	private boolean getWarrents() {
		listWarrent = new ArrayList<GetAnAgentModel>();
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		for (int i = 0; i < warrentLayout.getChildCount(); i++) {
			GetAnAgentModel model = new GetAnAgentModel();
			LinearLayout outer = (LinearLayout) warrentLayout.getChildAt(i);

			// final EditText warAmount = (EditText)outer.getChildAt(0);
			warAmount = (EditText) outer.getChildAt(0);
			warTown = (EditText) outer.getChildAt(2);

			String amt = warAmount.getText().toString();
			try {
				amt = nf.parse(amt).toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (amt.startsWith("$"))
					amt = amt.replace("$", "");
				amt = amt.replaceAll(",", "");
			}

			if (amt.length() == 0) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(" Enter warrant amount !")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alert = builder.create();
				alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alert.show();
				return false;
			}

			else if (warTown.length() == 0) {
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						getActivity());
				builder1.setMessage("Enter charging township!")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alert1 = builder1.create();
				alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alert1.show();
				// Utils.showDialog(getActivity(), "Please enetr warAmount !");
				return false;
			} else {
				model.setWarrentAmount(Double.parseDouble(amt));
				model.setCharginTown(warTown.getText().toString());
				listWarrent.add(model);
			}

			// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

			/*
			 * if(amt.length() == 0) {
			 * 
			 * warAmount.setError("Enter amount !");
			 * if(warTown.getText().toString().length() == 0)
			 * warTown.setError("Enter town !"); done = false; break; } else {
			 * done = true; warAmount.setError(null); warTown.setError(null);
			 * model.setWarrentAmount(Double.parseDouble(amt));
			 * model.setCharginTown(warTown.getText().toString());
			 * listWarrent.add(model); }
			 */
		}
		return true;
	}

	private boolean isValid() {
		location_entered = location.getText().toString();
		if (defFName.getText().toString().length() == 0)
			Utils.showDialog(getActivity(), "Please enter defendant first name !");

		else {
			if (selectState.getSelectedItem().toString()
					.equalsIgnoreCase("Select State"))
				Utils.showDialog(getActivity(), "Please Select State!");
			if (amountCompamnyOffer.getText().toString().equalsIgnoreCase(""))
				Utils.showDialog(getActivity(),
						"Please enter amount company offer to pay!");
			else if (selectInsurence.getSelectedItem().toString()
					.equalsIgnoreCase("Select insurance"))
				Utils.showDialog(getActivity(), "Please Select Insurance!");
			else if (location_entered.length() == 0)
				Utils.showDialog(getActivity(),
						"Please enter location of defendant !");
			else
				return true;

		}
		return false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, 1990, 0, 1);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			int selectedYear, month, day;
			selectedYear = year;
			month = monthOfYear;
			day = dayOfMonth;

			String date = pad(month + 1) + "/" + pad(day) + "/" + selectedYear;
			dateOfBirth.setText(date);
		}

		private String pad(int c) {
			return c >= 10 ? "" + c : "0" + c;
		}

	}

	private class PlacesAdaper extends ArrayAdapter<String> implements
			Filterable {

		ArrayList<String> resultList = new ArrayList<String>();

		public PlacesAdaper(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return resultList.size();
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return resultList.get(position);
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			Filter filter = new Filter() {

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					// TODO Auto-generated method stub
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					// TODO Auto-generated method stub
					FilterResults fRes = new FilterResults();

					if (constraint != null) {
						if (constraint.length() == 3)
							Toast.makeText(getActivity(), "Please wait...",
									Toast.LENGTH_SHORT).show();
						resList = Utils.searchPlaces(constraint.toString());
						Log.e("Places", resList == null ? "No Place Found"
								: resList.size() + " places found");
						resultList.clear();
						ArrayList<String[]> temp_resList=new ArrayList<String[]>();
						if (resList != null) {
							for (String[] place : resList) {
								if (!place[1].equals("")) {
									resultList.add(place[0]);
									temp_resList.add(place);
								}
							}
							resList=temp_resList;
						}

						fRes.values = resultList;
						fRes.count = resultList.size();
					}
					return fRes;
				}
			};
			return filter;
		}

	}

	void getAllInsurances() {
		getInsurance();
		if (insList != null && insList.size() > 0) {
			loadValues();
		}

	}

	void getAllStates() {

		if (Utils.isOnline()) {
			String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_States;
			client.setTimeout(getCallTimeout);
			showProgressDialog("");
			client.get(url, new AsyncHttpResponseHandler() {


				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					dismissProgressDialog();
				}

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
					dismissProgressDialog();
					String response2;
					response2 = new String(responseBody);
					if (response2 != null) {
						insListState = WebAccess.getAllStates(response2);

						if (insListState != null && insListState.size() > 0) {
							chkItems = new boolean[insListState.size()];
							loadValues();
						}
					} else
						Utils.showDialog(getActivity(), "Error occurs");

				}

			});

		} else {
			dismissProgressDialog();
			Utils.noInternetDialog(getActivity());

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

}
