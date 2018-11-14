package com.bailcompany;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.StateModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.FilePath;
import com.bailcompany.utils.ImageSelector;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressWarnings("deprecation")
public class DefendantBasicProfileDetails extends CustomActivity {

    private static final int Take_DROPBOX = 2;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;
    static EditText edtBirthdate;
    String message;
    JSONObject jsonObj;
    String key, apiUrl;
    DefendantModel defModel;
    String defId;
    EditText edtFirstName, edtLastName, edtTown, edtZipCode, edtHomeNumber, edtCellNumber, edtSSN, edtHeight, edtWeight, edtTattoos, edtPOB, edtFacebook, edtGoogle, edtTwitter;
    Spinner spState, spEyeColor, spHairColor;
    ImageView ivDefendantPic;
    String defPhotoPath;
    ArrayList<String[]> resList;
    private File file, defPhotoFile;
    private Uri defPhotoUri = null;
    private AutoCompleteTextView edtAddress;
    private String locLatt = "0.0", locLng = "0.0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendant_basic_details);
        setActionBar(getString(R.string.title_activity_defendant_profile_detail));

        if (getIntent().hasExtra("defendant")) {
            defModel = (DefendantModel) getIntent()
                    .getSerializableExtra("defendant");
            if (defModel != null) {
                defId = defModel.getId();
                showDetails();
            }
        }
    }

    public void showDetails() {
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);

        edtTown = (EditText) findViewById(R.id.edtTown);
        edtZipCode = (EditText) findViewById(R.id.edtZipCode);
        edtHomeNumber = (EditText) findViewById(R.id.edtHomeNumber);
        edtCellNumber = (EditText) findViewById(R.id.edtCellNumber);
        edtSSN = (EditText) findViewById(R.id.edtSSN);
        edtBirthdate = (EditText) findViewById(R.id.edtDateOfBirty);
        edtHeight = (EditText) findViewById(R.id.edtHeight);
        edtWeight = (EditText) findViewById(R.id.edtWeight);
        edtTattoos = (EditText) findViewById(R.id.edtTattoos);
        edtPOB = (EditText) findViewById(R.id.edtPOB);
        edtFacebook = (EditText) findViewById(R.id.edtFacebook);
        edtGoogle = (EditText) findViewById(R.id.edtGoogle);
        edtTwitter = (EditText) findViewById(R.id.edtTwitter);

        spState = (Spinner) findViewById(R.id.spState);
        spEyeColor = (Spinner) findViewById(R.id.spEyeColor);
        spHairColor = (Spinner) findViewById(R.id.spHairColor);
        ivDefendantPic = (ImageView) findViewById(R.id.ivDefendantProfilePic);

        edtAddress = (AutoCompleteTextView) findViewById(R.id.edtAddress);
      /*
        edtAddress.setAdapter(new PlacesAdaper(THIS,
                android.R.layout.simple_list_item_1));
*/
        edtFirstName.setText(defModel.getFirstName());
        edtLastName.setText(defModel.getLastName());
        edtAddress.setText(defModel.getAddress());
        edtTown.setText(defModel.getTown());
        edtZipCode.setText(defModel.getZipcode());
        edtHomeNumber.setText(defModel.getHomeTele());
        edtCellNumber.setText(defModel.getCellTele());
        edtSSN.setText(defModel.getSSN());
        edtHeight.setText(defModel.getHeight());
        edtWeight.setText(defModel.getWeight());
        edtTattoos.setText(defModel.getTattoos());
        edtPOB.setText(defModel.getPOB());
        edtBirthdate.setText(Const.getFormatedDate("yyyy-MM-dd", "MM/dd/yyyy", defModel.getDOB()
                .toString(), false));
        edtFacebook.setText(defModel.getFacebookURL());
        edtGoogle.setText(defModel.getGoogleURL());
        edtTwitter.setText(defModel.getTwitterURL());

        /*
        edtAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                        THIS.runOnUiThread(new Runnable() {
                            public void run() {
                                dismissProgressDialog();
                                locLatt = latLng[0];
                                locLng = latLng[1];

                            }
                        });
                    }
                }).start();
            }
        });
*/
        if (!defModel.getPhoto().equalsIgnoreCase("")) {
            Log.d("Profile:", defModel.getPhoto());
            Glide.with(getApplicationContext()).load(WebAccess.PHOTO + defModel.getPhoto()).placeholder(R.drawable.completion_form_add_photo_btn).error(R.drawable.completion_form_add_photo_btn).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(ivDefendantPic);
        }

        ArrayList<String> eyeColors = new ArrayList<String>();
        eyeColors.add("Select");
        eyeColors.add("Blue");
        eyeColors.add("Brown");
        eyeColors.add("Gray");
        eyeColors.add("Green");
        eyeColors.add("Hazel");
        eyeColors.add("Red");
        eyeColors.add("Amber");
        eyeColors.add("Black");
        eyeColors.add("Blonde");
        eyeColors.add("Other");
        ArrayAdapter<String> adapterEyeColor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eyeColors);
        spEyeColor.setAdapter(adapterEyeColor);
        spEyeColor.setSelection(1);

        ArrayList<String> hairColors = new ArrayList<String>();
        hairColors.add("Select");
        hairColors.add("Black");
        hairColors.add("Brown");
        hairColors.add("Blonde");
        hairColors.add("Red");
        hairColors.add("Bald");
        hairColors.add("Other");
        ArrayAdapter<String> adapterHairColor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hairColors);
        spHairColor.setAdapter(adapterHairColor);

        int hairColorPos = 0;
        for (int i = 0; i < hairColors.size(); i++) {
            if (hairColors.get(i).equalsIgnoreCase(defModel.getHairColor())) {
                hairColorPos = i;
            }
        }
        spHairColor.setSelection(hairColorPos);

        int eyeColorPos = 0;
        for (int i = 0; i < eyeColors.size(); i++) {
            if (eyeColors.get(i).equalsIgnoreCase(defModel.getEyeColor())) {
                eyeColorPos = i;
            }
        }
        spEyeColor.setSelection(eyeColorPos);

        getAllStates();

        edtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        ivDefendantPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result2 = Utility.checkPermission(THIS);

                if (result2) {
                    file = new File(Const.TEMP_PHOTO + "/" + Const.getUniqueIdforImage()
                            + ".png");
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    openChooser(THIS, file, null);
                }
            }
        });
        findViewById(R.id.btnUpdateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // popop
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (resultCode == RESULT_OK) {
            String path = "";
            if (requestCode == ImageSelector.IMAGE_CAPTURE) {
                try {
                    //  uri = data.getData();
                    uri = FileProvider.getUriForFile(THIS, Const.PROVIDER, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (uri == null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");


                    uri = ImageUtils.getImageUri(THIS, imageBitmap);

                    path = FilePath.getPath(THIS, uri);
                } else {
                    try {
                        //path = FilePath.getPath(THIS, uri);
                        path = file.getPath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == Take_DROPBOX) {
                path = data.getStringExtra("path");
                uri = Uri.fromFile(new File(path));

                if (path.contains(".pdf") || path.contains(".docx")) {
                    path = null;
                    uri = null;
                }


            } else if (requestCode == ImageSelector.IMAGE_GALLARY) {
                uri = data.getData();
                if (uri == null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    uri = ImageUtils.getImageUri(THIS, imageBitmap);
                }
                path = FilePath.getPath(THIS, uri);
                if (uri.toString().contains("com.dropbox")) {
                    if (path.contains(".pdf") || path.contains(".docx")) {
                        path = null;
                        uri = null;
                    }
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    uri = result.getUri();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
            if (uri != null && requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                Log.d("Path=", path);
                if (ImageSelector.isImage(path.trim().substring(path.lastIndexOf('/')))) {
                    defPhotoUri = uri;
                    CropImage.activity(defPhotoUri).setAspectRatio(1, 1)
                            .start(this);
                } else {
                    Toast.makeText(THIS, "Please select valid image", Toast.LENGTH_SHORT).show();
                }
                //ivDefendantPic.setImageResource(0);
                //Glide.with(getApplicationContext()).load(defPhotoUri).placeholder(R.drawable.completion_form_add_photo_btn).error(R.drawable.completion_form_add_photo_btn).into(ivDefendantPic);

            } else if (uri != null && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                defPhotoUri = uri;
                ivDefendantPic.setImageResource(0);
                Glide.with(getApplicationContext()).load(defPhotoUri).placeholder(R.drawable.completion_form_add_photo_btn).error(R.drawable.completion_form_add_photo_btn).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(ivDefendantPic);

            }


        }
    }

    public void openChooser(final Activity act, final File file,
                            final ImageSelector.RemoveListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Select Picture");
        builder.setItems(listener == null ? ImageSelector.DIALOG_OPTIONS1
                        : ImageSelector.DIALOG_OPTIONS,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0)
                            ImageSelector.openGallary(act);
                        else if (position == 1)
                            ImageSelector.openCamera(act, file);

                        else if (position == 2 && listener != null)
                            listener.onRemove();

                    }
                });
        builder.create().show();
    }


    public void updateProfile() {
        try {

            showProgressDialog("");
            RequestParams param = new RequestParams();
            String url = WebAccess.MAIN_URL + WebAccess.UPDATE_DEFENDENT_BASIC_PROFILE;
            client.setTimeout(getCallTimeout);
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("DefId", defId);
            param.put("FirstName", edtFirstName.getText().toString());
            param.put("LastName", edtLastName.getText().toString());
            param.put("Address", edtAddress.getText().toString());
            param.put("Town", edtTown.getText().toString());
            if (spState.getSelectedItemPosition() == 0) {
                param.put("State", "");
            } else
                param.put("State", spState.getSelectedItem().toString());

            param.put("Zipcode", edtZipCode.getText().toString());
            param.put("DOB", Const.getFormatedDate("MM/dd/yyyy", "yyyy-MM-dd", edtBirthdate.getText()
                    .toString(), false));

            param.put("CellTele", edtCellNumber.getText().toString());
            param.put("HomeTele", edtHomeNumber.getText().toString());

            param.put("SSN", edtSSN.getText().toString());
            param.put("Height", edtHeight.getText().toString());
            param.put("Weight", edtWeight.getText().toString());
            param.put("EyeColor", spEyeColor.getSelectedItemPosition() != 0 ? spEyeColor.getSelectedItem().toString() : "");

            param.put("HairColor", spHairColor.getSelectedItemPosition() != 0 ? spHairColor.getSelectedItem().toString() : "");
            param.put("Tattoos", edtTattoos.getText().toString());
            param.put("POB", edtPOB.getText().toString());
            param.put("FacebookURL", edtFacebook.getText().toString());
            param.put("TwitterURL", edtTwitter.getText().toString());
            param.put("GoogleURL", edtGoogle.getText().toString());

            if (defPhotoUri != null) {

                InputStream imgISdef;
                imgISdef = getContentResolver().openInputStream(defPhotoUri);

                param.put("DefPhoto", imgISdef, "defPhoto.jpg",
                        "image/jpg");

            } else {
                param.put("DefPhoto", "");

            }


            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissProgressDialog();

                    Utils.showDialog(THIS,
                            R.string.err_unexpect);

                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    dismissProgressDialog();
                    try {
                        String response2;
                        response2 = new String(responseBody);
                        JSONObject resObj;
                        resObj = new JSONObject(response2);

                        Log.d("Res=", response2);
                        if (resObj != null) {
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {
                                // setCompleteStatus();
                                message = resObj.optString("message");
                                if (!Commons.isEmpty(message)
                                        || message.equalsIgnoreCase("success")) {

                                    Utils.showDialog(THIS, message);

                                    Intent intent = new Intent();
                                    intent.putExtra(Const.RETURN_FLAG, Const.DEFENDANT_BASIC_DETAILS_UPDATED);
                                    setResult(RESULT_OK, intent);
                                    finish();

                                }

                            } else if (resObj.optString("status")
                                    .equalsIgnoreCase("3")) {
                                Toast.makeText(
                                        THIS,
                                        "Session was closed please login again",
                                        Toast.LENGTH_LONG).show();
                                MainActivity.sp.edit().putBoolean("isFbLogin",
                                        false);
                                MainActivity.sp.edit().putString("user", null)
                                        .commit();
                                startActivity(new Intent(DefendantBasicProfileDetails.this,
                                        Launcher.class));
                            } else {

                                Utils.showDialog(THIS,
                                        resObj.optString("message"));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(THIS, "Error occurs");
                        e.printStackTrace();
                    }


                }

            });


        } catch (Exception e) {
            e.printStackTrace();
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
                        ArrayList<StateModel> insListState = new ArrayList<StateModel>();
                        insListState = WebAccess.getAllStates(response2);

                        if (insListState != null && insListState.size() > 0) {

                            ArrayList<String> state = new ArrayList<String>();
                            state.add("Select State");
                            int pos = 0;
                            for (int i = 0; i < insListState.size(); i++) {
                                StateModel ins = insListState.get(i);
                                state.add(ins.getName());
                                if (ins.getName().equalsIgnoreCase(defModel.getState())) {
                                    pos = i + 1;
                                }
                            }
                            ArrayAdapter<String> adapterState = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, state);
                            spState.setAdapter(adapterState);
                            spState.setSelection(pos);

                        }
                    } else
                        Utils.showDialog(THIS, "Error occurs");

                }

            });

        } else {
            dismissProgressDialog();
            Utils.noInternetDialog(getApplicationContext());

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
        if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent
                || WebAccess.hireBailAgent || WebAccess.selfAssignAgent) {
            finish();
        }
    }

    void hideKeyboard() {
        View view = THIS.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) THIS
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setActionBar(String title) {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
            edtBirthdate.setText(date);
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
                            Toast.makeText(THIS, "Please wait...",
                                    Toast.LENGTH_SHORT).show();
                        resList = Utils.searchPlaces(constraint.toString());

                        Log.d("Result", resList.toArray().toString());
                        Log.e("Places", resList == null ? "No Place Found"
                                : resList.size() + " places found");
                        resultList.clear();
                        ArrayList<String[]> tempResList = new ArrayList<String[]>();
                        if (resList != null) {
                            for (String[] place : resList) {
                                if (!place[1].equals("")) {
                                    resultList.add(place[0]);

                                    tempResList.add(place);
                                }

                            }
                            resList = tempResList;
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
