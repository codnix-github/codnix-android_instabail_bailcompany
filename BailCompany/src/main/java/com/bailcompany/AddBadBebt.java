package com.bailcompany;

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
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.StateModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.FilePath;
import com.bailcompany.utils.ImageSelector;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Methods;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AddBadBebt extends CustomActivity {
	private ImageView addPhoto;
	private static File file;
	private static final int SELECT_PHOTO = 1;
	private static final int CAMERA_REQUEST = 1888;
	static Uri imgUri;
	public static String location_entered;
	static InputStream imgIS = null;
	ArrayList<String[]> resList;
	static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
	String message;
	JSONObject jsonObj;
	String key;
	static int getCallTimeout = 50000;
	private EditText dateOfBirth;
	private TextView btnAdd;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
	ArrayList<StateModel> insListState = new ArrayList<StateModel>();
	private AutoCompleteTextView location;
	private String locLatt = "0.0", locLng = "0.0";
	private Spinner selectCountryCode;
	private Calendar cal;
	private int day;
	private int month;
	private int year;
	EditText firstName, lastName, amountOwed, phoneNumber;
	Spinner defendant;
	String value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bad_bebt);
		list.add("Defendant");
		list.add("Cosigner");
		addPhoto = (ImageView) findViewById(R.id.add_photo);
		dateOfBirth = (EditText) findViewById(R.id.dob_txt);
		btnAdd = (TextView) findViewById(R.id.btn_add);

		firstName = (EditText) findViewById(R.id.fname);
		lastName = (EditText) findViewById(R.id.lname);
		defendant = (Spinner) findViewById(R.id.defendant);
		amountOwed = (EditText) findViewById(R.id.amount_owned);

		phoneNumber = (EditText) findViewById(R.id.phone_number);

		selectCountryCode = (Spinner) findViewById(R.id.sel_country_code_register);
		location = (AutoCompleteTextView) findViewById(R.id.location_def);
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		setActionBar();
		// getAllStates();
		loadValues();
		phoneFormat(phoneNumber);
		location.setAdapter(new PlacesAdaper(this,
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
						AddBadBebt.this.runOnUiThread(new Runnable() {
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
		amountOwed.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(2);
				nf.setMinimumFractionDigits(2);
				if (!hasFocus)
					amountOwed.setText(nf.format(Commons.strToDouble(amountOwed
							.getText().toString())));

				if (hasFocus && amountOwed.getText().toString().startsWith("$")) {
					amountOwed.setText(amountOwed.getText().toString()
							.replace("$", ""));
				} else if (!hasFocus
						&& !amountOwed.getText().toString().startsWith("$")) {
					amountOwed.setText("$" + amountOwed.getText().toString());
				}
			}
		});
		addPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				file = new File(Const.TEMP_PHOTO + Const.getUniqueIdforImage()
						+ ".png");
				boolean result = Utility.checkPermission(THIS);
				if (result) {
					selectImage();
				}

			}
		});
		dateOfBirth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				DateDialog(dateOfBirth);
			}
		});
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				if (isValid()) {
					if (Utils.isOnline(AddBadBebt.this)) {
						submitReuest();
					}

					else
						Utils.noInternetDialog(AddBadBebt.this);
				}

			}
		});
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

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {

		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			Utility.checkPermission(this);

		} else {
			Methods.showToast(this, "Permission Denny", Toast.LENGTH_SHORT);
		}

	}

	protected void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getString(R.string.bad_debt_detail));
	}

	private boolean isValid() {
		location_entered = location.getText().toString();
		value = list.get(defendant.getSelectedItemPosition() - 1);
		if (firstName.getText().toString().length() == 0)
			Utils.showDialog(this, "Please enter First Name!");
		else {
			if (lastName.getText().toString().length() == 0)
				Utils.showDialog(this, "Please enter Last Name!");
			else {

				if (amountOwed.getText().toString().length() == 0)
					Utils.showDialog(this, "Please enter Amount Owed!");
				else if (value.length() == 0)
					Utils.showDialog(this,
							"Please enter Defendent Or Cosigner!");

				else if (location_entered.length() == 0)
					Utils.showDialog(this, "Please enter Address!");

				else
					return true;
			}
		}
		return false;
	}

	private void loadValues() {
		ArrayList<String> l = new ArrayList<String>();
		l.add("Select Defendant Or Cosigner");
		l.add("Defendant");
		l.add("Cosigner");

		ArrayList<String> s = new ArrayList<String>();
		s.add("Select State");
		for (int i = 0; i < insListState.size(); i++) {
			StateModel ins = insListState.get(i);
			s.add(ins.getName());

		}
		if (l != null) {
			defendant.setAdapter((AddBadBebt.this).getAdapter(l));
		}
		selectCountryCode.setAdapter((AddBadBebt.this)
				.getAdapter((AddBadBebt.this).getCountryCodeList()));

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
		String amount = amountOwed.getText().toString().replace("$", "").trim();
		RequestParams param = new RequestParams();
		param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
		param.put("UserName", MainActivity.user.getUsername());
		param.put("Name", firstName.getText().toString() + " "
				+ lastName.getText().toString());
		param.put("firstname", firstName.getText().toString() );
		param.put("lastname", lastName.getText().toString());
		param.put("Address", location_entered);
		String ph = "";
		if (!phoneNumber.getText().toString().equalsIgnoreCase(""))
			ph = selectCountryCode.getSelectedItem().toString()
					+ phoneNumber.getText().toString();

		param.put("Telephone", ph);
		param.put("DOB", date);
		param.put("DefendentOrCosigner", value);
		param.put("AmountOwed", amount);

		if (imgIS != null) {
			param.put("photo", imgIS, "mfile.jpg", "image/jpg");
		}

		String url = WebAccess.MAIN_URL + WebAccess.ADD_BAD_DEBT_MEMBER;
		client.setTimeout(getCallTimeout);
		client.post(this, url, param, new AsyncHttpResponseHandler() {
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
							Toast.makeText(AddBadBebt.this,
									json.optString("message"),
									Toast.LENGTH_SHORT).show();
							setResult(RESULT_OK, null);
							finish();
						} else if (json.optString("status").equalsIgnoreCase(
								"3")) {
							Toast.makeText(THIS,
									"Session was closed please login again",
									Toast.LENGTH_LONG).show();
							MainActivity.sp.edit().putBoolean("isFbLogin",
									false);
							MainActivity.sp.edit().putString("user", null)
									.commit();
							startActivity(new Intent(AddBadBebt.this,
									Launcher.class));
						} else {
							Utils.showDialog(AddBadBebt.this,
									json.optString("message"));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				dismissProgressDialog();
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

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
			imgUri = data.getData();
			if (imgUri != null) {
				try {
					String path = FilePath.getPath(this, imgUri);
					if (path.contains(".JPEG") || path.contains(".jpeg")
							|| path.contains(".JPG") || path.contains(".jpg")
							|| path.contains(".PNG") || path.contains(".png")) {
						displayImage(imgUri);
					} else {
						Utils.showDialog(this,
								"Error, This file type not allowed");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				// showToast("Error, Could not get image path");
				Utils.showDialog(this, "Error, Could not get image path");
		}
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			imgUri = ImageUtils.getImageUri(THIS, imageBitmap);
			imgUri = data.getData();
			if (imgUri != null) {
				try {
					displayImage(imgUri);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				Utils.showDialog(this, "Error, Could not get image path");
		}
	}

	public void displayImage(Uri imgUri) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		String selectedImagePath = FilePath.getPath(THIS, imgUri);
		// String selectedImagePath = getPath(imgUri);
		addPhoto.setImageBitmap(compressFile(selectedImagePath));
		Bitmap mapImage = compressFile(selectedImagePath);

		SaveImage(mapImage);
		File file = new File("/storage/emulated/0/temp.jpg");
		imgUri = getImageContentUri(this, file);
		imgIS = this.getContentResolver().openInputStream(imgUri);

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

			MediaStore.Images.Media.insertImage(this.getContentResolver(),
					file.getAbsolutePath(), file.getName(), file.getName());
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
		Cursor cursor = this.managedQuery(uri, projection, null, null, null);
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

	public void DateDialog(final EditText fieldView) {

		OnDateSetListener listener = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				monthOfYear = monthOfYear + 1;
				if (monthOfYear == 11 || monthOfYear == 12 || monthOfYear == 10) {

					if (dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3
							|| dayOfMonth == 4 || dayOfMonth == 5
							|| dayOfMonth == 6 || dayOfMonth == 7
							|| dayOfMonth == 8 || dayOfMonth == 9) {
						// fieldView.setText(year + "-" + monthOfYear + "-0"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					} else {
						// fieldView.setText(year + "-" + monthOfYear + "-"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					}
				} else {
					if (dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3
							|| dayOfMonth == 4 || dayOfMonth == 5
							|| dayOfMonth == 6 || dayOfMonth == 7
							|| dayOfMonth == 8 || dayOfMonth == 9) {
						// fieldView.setText(year + "-0" + monthOfYear + "-0"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					} else {
						// fieldView.setText(year + "-0" + monthOfYear + "-"
						// + dayOfMonth);
						fieldView.setText(monthOfYear + "/" + dayOfMonth + "/"
								+ year);
					}
				}
			}

		};

		DatePickerDialog dpDialog = new DatePickerDialog(this, listener, 1990,
				0, 1);
		dpDialog.show();

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
							Toast.makeText(AddBadBebt.this, "Please wait...",
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

	//
	// void getAllStates() {
	// if (insListState == null || insListState.size() == 0) {
	// if (Utils.isOnline()) {
	// showProgressDialog("");
	// String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_States;
	// client.setTimeout(getCallTimeout);
	//
	// client.get(url, new AsyncHttpResponseHandler() {
	//
	// @Override
	// public void onFailure(int arg0, Header[] arg1, byte[] arg2,
	// Throwable arg3) {
	// dismissProgressDialog();
	//
	// }
	//
	// @SuppressWarnings("unused")
	// @Override
	// public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	// dismissProgressDialog();
	// String response2;
	// response2 = new String(arg2);
	// if (response2 != null) {
	// insListState = WebAccess.getAllStates(response2);
	//
	// if (insListState != null && insListState.size() > 0) {
	// // chkItems = new boolean[insListState.size()];
	// loadValues();
	// }
	// } else
	// Utils.showDialog(AddBadBebt.this, "Error occurs");
	//
	// }
	//
	// });
	//
	// } else {
	// Utils.noInternetDialog(this);
	//
	// }
	// }
	// }

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(AddBadBebt.this);
		builder.setTitle("Add Image!");
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
}
