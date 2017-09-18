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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.GetAnAgentModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class ReferBail extends CustomFragment {

	private EditText defFName;
	private EditText defLName;
	private EditText editAmountTo;
	private EditText colletralText;
	private EditText instruction;
	private EditText warAmount;
	private EditText warTown;
	private EditText ammountForCommission;
	static EditText dateOfBirth, number_cosigner;
	private Button addMore, btnSubmit;
	private LinearLayout warrentLayout;
	private AutoCompleteTextView location;
	private ArrayList<GetAnAgentModel> listWarrent;
	public static String location_entered;
	private String locLatt = "0.0", locLng = "0.0";
	ArrayList<String[]> resList;

	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;

	ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.refer_bail, null);
		setHasOptionsMenu(true);
		initValues(v);

		setTouchNClick(addMore);
		setTouchNClick(dateOfBirth);
		setTouchNClick(btnSubmit);
		addWarrant(false);
		return v;
	}

	private void initValues(View v) {

		dateOfBirth = (EditText) v.findViewById(R.id.dob);

		number_cosigner = (EditText) v.findViewById(R.id.no_cosigner);
		location = (AutoCompleteTextView) v.findViewById(R.id.location_def);
		defFName = (EditText) v.findViewById(R.id.def_fname);
		defLName = (EditText) v.findViewById(R.id.def_lname);
		ammountForCommission = (EditText) v.findViewById(R.id.companyComm);
		instruction = (EditText) v.findViewById(R.id.instructions);

		editAmountTo = (EditText) v.findViewById(R.id.edit_amount_to);
		colletralText = (EditText) v.findViewById(R.id.multiline_text);

		addMore = (Button) v.findViewById(R.id.add_more);
		btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
		warrentLayout = (LinearLayout) v.findViewById(R.id.warrent_layout);
		location.setAdapter(new PlacesAdaper(getActivity(),
				android.R.layout.simple_list_item_1));

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
		ammountForCommission
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						NumberFormat nf = NumberFormat.getNumberInstance();
						nf.setMaximumFractionDigits(2);
						nf.setMinimumFractionDigits(2);
						if (!hasFocus)
							ammountForCommission.setText(nf.format(Commons
									.strToDouble(ammountForCommission.getText()
											.toString())));

						if (hasFocus
								&& ammountForCommission.getText().toString()
										.startsWith("$")) {
							ammountForCommission.setText(ammountForCommission
									.getText().toString().replace("$", ""));
						} else if (!hasFocus
								&& !ammountForCommission.getText().toString()
										.startsWith("$")) {
							ammountForCommission
									.setText("$"
											+ ammountForCommission.getText()
													.toString());
						}
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

	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		super.onClick(v);
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		switch (v.getId()) {

		case R.id.dob:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
			break;
		case R.id.add_more:
			addWarrant(true);
			break;

		case R.id.btnSubmit:
			if (isValid() && getWarrents()) {
				if (Utils.isOnline(getActivity()))
					referBail();
				else
					Utils.noInternetDialog(getActivity());
			}
			break;
		}
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

	private void referBail() {
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
		String amountoffer = ammountForCommission.getText().toString().trim();
		RequestParams param = new RequestParams();
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());

		param.put("DefendantName", defFName.getText().toString()+" " +defLName.getText().toString());
		param.put("firstname", defFName.getText().toString());
		param.put("firstname", defFName.getText().toString());
		param.put("lastname", defLName.getText().toString());
		param.put("DefDOB", date);
		param.put("LocationLongitude", locLng);
		param.put("LocationLatitude", locLatt);
		param.put("AmountToCollect",
				Commons.isEmpty(amount) ? 0 : (amount.startsWith("$") ? amount
						.replace("$", "").replace(",", "") : amount));
		param.put("PaymentPlan", colletralText.getText().toString());

		param.put("InstructionForAgent", instruction.getText().toString());

		param.put("Location", location_entered);
		for (int i = 0; i < listWarrent.size(); i++) {
			param.put("Amount[" + i + "]", listWarrent.get(i)
					.getWarrentAmount());
			param.put("Township[" + i + "]", listWarrent.get(i)
					.getCharginTown());

		}
		if (number_cosigner.getText().toString().equalsIgnoreCase(""))
			param.put("NumberofCosigner", number_cosigner.getText().toString());
		else
			param.put("NumberofCosigner",
					Integer.parseInt(number_cosigner.getText().toString()));
		param.put("AmountForCommission", Commons.isEmpty(amountoffer) ? 0
				: (amountoffer.startsWith("$") ? amountoffer.replace("$", "")
						.replace(",", "") : amountoffer));
		String url = WebAccess.MAIN_URL + WebAccess.REFER_BAIL;
		client.setTimeout(getCallTimeout);
		client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

				dismissProgressDialog();

			}

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

		}
		return true;
	}

	private boolean isValid() {
		location_entered = location.getText().toString();

		if (location_entered.length() == 0) {
			Utils.showDialog(getActivity(),
					"Please enter location of defendant !");
			return false;
		} else if (editAmountTo.length() == 0) {
			Utils.showDialog(getActivity(), "Please enter premium amount !");
			return false;
		}
		if (ammountForCommission.getText().toString().equalsIgnoreCase(""))
			Utils.showDialog(getActivity(),
					"Please enter amount for commision!");
		if (colletralText.length() == 0) {
			Utils.showDialog(getActivity(), "Please enter collateral detail !");
			return false;
		}

		return true;
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

}
