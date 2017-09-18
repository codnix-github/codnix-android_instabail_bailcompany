package com.bailcompany.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.GetAnAgentModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.StateModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.FilePath;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class FugitiveAgent extends CustomFragment implements
		OnCheckedChangeListener {
	private Spinner selectState;

	private Spinner selectCountryCode, selectCountryCode2;

	private RadioButton radioPaymentAlr;
	private RadioButton radioPaymentToBe;
	public static String location_entered;
	private EditText defFName;
	private EditText defLName;
	private EditText SSN;
	private EditText warrentNumber;
	private EditText editAmountTo;
	private EditText homeNumber;
	private EditText cellNumber;

	private Spinner male;
	private Button addMore;
	private EditText instruction;
	private EditText bail;

	static EditText dateOfBirth;
	private Button btnSubmit;

	private AutoCompleteTextView location;

	private String locLatt = "0.0", locLng = "0.0";
	ArrayList<String[]> resList;

	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	private ArrayList<GetAnAgentModel> listWarrent;

	ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
	ArrayList<StateModel> insListState = new ArrayList<StateModel>();
	private ImageView addPhoto;
	private static File file;
	private static final int SELECT_PHOTO = 1;
	private static final int CAMERA_REQUEST = 1888;
	static Uri imgUri;
	static InputStream imgIS = null;
	private LinearLayout warrentLayout;
	private EditText warAmount;
	private EditText warTown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fugitive_agent, null);
		setHasOptionsMenu(true);
		getAllStates();
		initValues(v);
		loadValues();

		setTouchNClick(addMore);
		setTouchNClick(dateOfBirth);
		setTouchNClick(btnSubmit);
		setTouchNClick(addPhoto);
		setTouchNClick(radioPaymentAlr);
		setTouchNClick(radioPaymentToBe);
		return v;
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean checked) {
		editAmountTo.setText("");
		editAmountTo.setEnabled(true);

	}

	private void initValues(View v) {
		selectState = (Spinner) v.findViewById(R.id.state_sel);
		addMore = (Button) v.findViewById(R.id.add_more_charge);
		selectCountryCode = (Spinner) v.findViewById(R.id.sel_country_code);
		selectCountryCode2 = (Spinner) v.findViewById(R.id.sel_country_code2);
		dateOfBirth = (EditText) v.findViewById(R.id.dob);
		location = (AutoCompleteTextView) v.findViewById(R.id.location_def);

		homeNumber = (EditText) v.findViewById(R.id.home_number);
		cellNumber = (EditText) v.findViewById(R.id.cell_number);

		male = (Spinner) v.findViewById(R.id.gender_sel2);
		bail = (EditText) v.findViewById(R.id.bail);
		radioPaymentAlr = (RadioButton) v.findViewById(R.id.radio1);
		radioPaymentToBe = (RadioButton) v.findViewById(R.id.radio2);
		warrentLayout = (LinearLayout) v.findViewById(R.id.warrent_layout);
		defFName = (EditText) v.findViewById(R.id.def_fname);
		defLName = (EditText) v.findViewById(R.id.def_lname);

		SSN = (EditText) v.findViewById(R.id.ssn);
		warrentNumber = (EditText) v.findViewById(R.id.bkng_num);
		instruction = (EditText) v.findViewById(R.id.instructions);

		editAmountTo = (EditText) v.findViewById(R.id.edit_amount_to);
		addPhoto = (ImageView) v.findViewById(R.id.add_photo);
		radioPaymentAlr.setOnCheckedChangeListener(this);
		radioPaymentToBe.setOnCheckedChangeListener(this);
		btnSubmit = (Button) v.findViewById(R.id.btn_submit);
		editAmountTo.setEnabled(false);
		location.setAdapter(new PlacesAdaper(getActivity(),
				android.R.layout.simple_list_item_1));
		phoneFormat(homeNumber);
		phoneFormat(cellNumber);

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
		addWarrant(false);

		editAmountTo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				NumberFormat nf = NumberFormat.getNumberInstance();

				nf.setMaximumFractionDigits(2);
				nf.setMinimumFractionDigits(2);
				// nf2.setMaximumFractionDigits(2);
				// nf2.setMinimumFractionDigits(2);

				if (!hasFocus)
					editAmountTo.setText(Commons.strToDouble(editAmountTo
							.getText().toString()) + "");

				if (hasFocus
						&& (editAmountTo.getText().toString().startsWith("$") || editAmountTo
								.getText().toString().endsWith("%"))) {
					editAmountTo.setText(editAmountTo.getText().toString()
							.replace("$", ""));
					editAmountTo.setText(editAmountTo.getText().toString()
							.replace("%", ""));
				} else if (!hasFocus
						&& (!editAmountTo.getText().toString().startsWith("$") || !editAmountTo
								.getText().toString().endsWith("%"))) {
					String str = editAmountTo.getText().toString();
					if (radioPaymentAlr.isChecked())
						editAmountTo.setText("$"
								+ nf.format(Double.parseDouble(str)));
					else if (radioPaymentToBe.isChecked()) {
						editAmountTo.setText(""
								+ nf.format(Double.parseDouble(str)) + "%");
					}

				}
			}
		});
	}

	private void loadValues() {
		ArrayList<String> l = new ArrayList<String>();
		l.add("Select Insurance");
		for (int i = 1; i < insList.size(); i++) {
			InsuranceModel ins = insList.get(i);
			l.add(ins.getName());

		}
		ArrayList<String> s = new ArrayList<String>();
		s.add("State in which recovery is needed");
		for (int i = 0; i < insListState.size(); i++) {
			StateModel ins = insListState.get(i);
			s.add(ins.getName());

		}

		if (s != null) {
			selectState
					.setAdapter(((MainActivity) getActivity()).getAdapter(s));
		}
		selectCountryCode
				.setAdapter(((MainActivity) getActivity())
						.getAdapter(((MainActivity) getActivity())
								.getCountryCodeList()));
		selectCountryCode2
				.setAdapter(((MainActivity) getActivity())
						.getAdapter(((MainActivity) getActivity())
								.getCountryCodeList()));
		ArrayList<String> gender = new ArrayList<String>();
		gender.add("Male");
		gender.add("Female");
		male.setAdapter(((MainActivity) getActivity()).getAdapter(gender));
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {

		case R.id.dob:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
			break;

		case R.id.btn_submit:
			if (isValid() && Radio() && getWarrents()) {
				if (Utils.isOnline(getActivity())) {
					submitReuest();
				}

				else
					Utils.noInternetDialog(getActivity());
			}
			break;
		case R.id.add_photo:
			file = new File(Const.TEMP_PHOTO + Const.getUniqueIdforImage()
					+ ".png");
			// ImageSelector.openChooser(getActivity(), file, null);
			boolean result = Utility.checkPermission(getActivity());
			if (result) {
				selectImage();
			}
			break;
		case R.id.add_more_charge:
			addWarrant(true);
			break;

		}

	}

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
				hideKeyboard();
				((LinearLayout) warrent.getParent()).removeView(warrent);
			}
		});

		warrentLayout.addView(warrent);
	}

	void hideKeyboard() {
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
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
				builder.setMessage(" Enter warrant number !")
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

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Fugitive Image!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent cameraIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);
				} else if (items[item].equals("Choose from Gallery")) {

					Intent gallery = new Intent();
					gallery.setType("image/*");
					gallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(
							Intent.createChooser(gallery, "Select Picture"),
							SELECT_PHOTO);

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
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

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
			imgUri = data.getData();

			if (imgUri != null) {
				try {
					String path = FilePath.getPath(getActivity(), imgUri);
					if (path.contains(".JPEG") || path.contains(".jpeg")
							|| path.contains(".JPG") || path.contains(".jpg")
							|| path.contains(".PNG") || path.contains(".png")) {
						displayImage(imgUri);
					} else {
						Utils.showDialog(getActivity(),
								"Error, This file type not allowed");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				// showToast("Error, Could not get image path");
				Utils.showDialog(getActivity(),
						"Error, Could not get image path");
		}
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			imgUri = ImageUtils.getImageUri(getActivity(), imageBitmap);
			imgUri = data.getData();
			if (imgUri != null) {
				try {
					displayImage(imgUri);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				Utils.showDialog(getActivity(),
						"Error, Could not get image path");
		}
	}

	public void displayImage(Uri imgUri) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		String selectedImagePath = FilePath.getPath(getActivity(), imgUri);
		// String selectedImagePath = getPath(imgUri);
		addPhoto.setImageBitmap(compressFile(selectedImagePath));
		Bitmap mapImage = compressFile(selectedImagePath);

		SaveImage(mapImage);
		File file = new File("/storage/emulated/0/temp.jpg");
		imgUri = getImageContentUri(getActivity(), file);
		imgIS = getActivity().getContentResolver().openInputStream(imgUri);

	}

	private void SaveImage(Bitmap finalBitmap) {

		try {
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			File file = new File(path, "temp.jpg"); // the File to save to
			fOut = new FileOutputStream(file);

			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();

			MediaStore.Images.Media.insertImage(getActivity()
					.getContentResolver(), file.getAbsolutePath(), file
					.getName(), file.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			return Uri.withAppendedPath(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public int rotateImage(int orientation) {
		if (orientation == 0) {
			return 0;
		} else if (orientation == 3) {
			return 180;

		} else if (orientation == 8) {
			return 270;

		} else if (orientation == 6) {
			return 90;

		} else {
			return 0;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Bitmap rotateImage(Bitmap src, float degree) {
		// create new matrix object
		Matrix matrix = new Matrix();
		// setup rotation degree
		matrix.postRotate(degree);
		// return new bitmap rotated using matrix
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
				matrix, true);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private Bitmap compressFile(String path) {
		File f = new File(path);
		Bitmap bm = ImageUtils.getOrientationFixedImage(f,
				StaticData.getDIP(110), StaticData.getDIP(110),
				ImageUtils.SCALE_FIT_CENTER);
		bm = ImageUtils.getCircularBitmap(bm);
		return bm;
	}

	private void submitReuest() {
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
		String amount = editAmountTo.getText().toString().replace("$", "")
				.replaceAll(",", "").replace("%", "").trim();
		RequestParams param = new RequestParams();
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		param.put("DefendantName", defFName.getText().toString()+" " +defLName.getText().toString());
		param.put("firstname", defFName.getText().toString());
		param.put("lastname", defLName.getText().toString());
		param.put("DefDOB", date);
		param.put("DefSSN", SSN.getText().toString());
		param.put("DefBookingNumber", warrentNumber.getText().toString());

		param.put("LocationLongitude", locLng);
		param.put("LocationLatitude", locLatt);
		param.put("AmountCompanyWillToPay", radioPaymentAlr.isChecked() ? 1 : 0);
		param.put("HomeNumber", selectCountryCode.getSelectedItem().toString()
				+ homeNumber.getText().toString());
		param.put("CellNumber", selectCountryCode2.getSelectedItem().toString()
				+ cellNumber.getText().toString());
		// param.put("Township", townShip.getText().toString());
		// param.put("ForfeitureAmount", forfeitureAmount.getText().toString());
		for (int i = 0; i < listWarrent.size(); i++) {
			param.put("Amount[" + i + "]", listWarrent.get(i)
					.getWarrentAmount());
			param.put("Township[" + i + "]", listWarrent.get(i)
					.getCharginTown());

		}
		String checkGender = male.getSelectedItem().toString();
		int i2 = 1;
		if (checkGender.equalsIgnoreCase("Male"))
			i2 = 0;
		param.put("IsFemale", i2);
		param.put("AmountorPercentage",
				Commons.isEmpty(amount) ? 0 : (amount.startsWith("$") ? amount
						.replace("$", "").replace(",", "") : amount));

		param.put("InstructionForAgent", instruction.getText().toString());

		param.put("Location", location_entered);

		StateModel sm = insListState
				.get(selectState.getSelectedItemPosition() - 1);

		param.put("RecoveryState", sm.getId());

		int i = Integer.parseInt(bail.getText().toString());
		param.put("DayLeftBeforeBailSeized", i);
		if (imgIS != null) {
			param.put("photo", imgIS, "mfile.jpg", "image/jpg");
		}

		String url = WebAccess.MAIN_URL + WebAccess.REQUEST_FUGITIVE_AGENT;
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
						deleteFile("/storage/emulated/0/temp.jpg");
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

	public boolean deleteFile(String selectedFilePath) {
		File file = new File(selectedFilePath);
		boolean deleted = false;
		if (file.exists())
			deleted = file.delete();

		return deleted;
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

	private boolean Radio() {
		if (!radioPaymentAlr.isChecked() && !radioPaymentToBe.isChecked()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Please select payment type !")
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
				builder.setMessage(
						" Enter Amount or percentage company will to pay !")
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

	private boolean isValid() {
		location_entered = location.getText().toString();
		if (defFName.getText().toString().length() == 0)
			Utils.showDialog(getActivity(), "Please enter defendant first name !");
		else {

			if (selectState.getSelectedItem().toString()
					.equalsIgnoreCase("State in which recovery is needed"))
				Utils.showDialog(getActivity(), "Please Select Recovery State!");

			else if (location_entered.length() == 0)
				Utils.showDialog(getActivity(),
						"Please enter location of defendant !");

			else if (bail.getText().toString().length() == 0)
				Utils.showDialog(getActivity(),
						"Please enter Days left ot seized bail");
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

	public static class  DatePickerFragment extends DialogFragment implements
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

	void getAllStates() {
		if (insListState == null || insListState.size() == 0) {
			if (Utils.isOnline()) {
				showProgressDialog("");
				String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_States;
				client.setTimeout(getCallTimeout);

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
								// chkItems = new boolean[insListState.size()];
								loadValues();
							}
						} else
							Utils.showDialog(getActivity(), "Error occurs");

					}

				});

			} else {
				Utils.noInternetDialog(getActivity());

			}
		}
	}
}
