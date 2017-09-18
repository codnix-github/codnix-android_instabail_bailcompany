package com.bailcompany.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.Login;
import com.bailcompany.R;
import com.bailcompany.Register;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.StateModel;
import com.bailcompany.model.User;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.FilePath;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Log;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class RegistrationPartOne extends CustomFragment {

    private EditText firstName;
    private EditText lastName;
    private EditText companyName;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText address;
    private EditText town;
    private EditText state;
    private EditText zipCode;
    private EditText phoneNumber;
    private EditText agencyLicenseNumber;
    private Spinner selectCountryCode, addressState;
    private EditText selectInsurance;

    private ImageView addPhoto;
    // private static File file;
    private ArrayList<String> selectedItems, selectedItems2;
    private boolean[] chkItems, chkItems2;
    User user;
    private String returnedPhotoPath;
    // private UiLifecycleHelper uiHelper;
    String response2;
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    String message;
    JSONObject jsonObj;
    String key;
    static int getCallTimeout = 50000;
    int i;

    private static final int SELECT_PHOTO = 1;
    private static final int CAMERA_REQUEST = 1888;
    ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
    static Uri imgUri;
    static InputStream imgIS = null;
    static ArrayList<StateModel> insListState = new ArrayList<StateModel>();

    private TextView fbSig;
    CallbackManager callbackManager;
    int REQUEST_CODE = 12345, selectedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        View v = inflater.inflate(R.layout.register, null);
        setActionBar();
        chkItems2 = null;

        StaticData.init(getActivity());
        getAllStates();
        selectedItems = new ArrayList<String>();
        selectedItems2 = new ArrayList<String>();
        fbSig = (TextView) v.findViewById(R.id.authButton);

        firstName = (EditText) v.findViewById(R.id.fname);
        lastName = (EditText) v.findViewById(R.id.lname);
        companyName = (EditText) v.findViewById(R.id.cname);
        username = (EditText) v.findViewById(R.id.uname);
        email = (EditText) v.findViewById(R.id.email);
        password = (EditText) v.findViewById(R.id.pass);
        address = (EditText) v.findViewById(R.id.address);
        town = (EditText) v.findViewById(R.id.town);
        state = (EditText) v.findViewById(R.id.sel_state);
        addressState = (Spinner) v.findViewById(R.id.state);
        zipCode = (EditText) v.findViewById(R.id.zip_code);
        phoneNumber = (EditText) v.findViewById(R.id.phone_number);
        agencyLicenseNumber = (EditText) v
                .findViewById(R.id.agency_license_num);

        selectCountryCode = (Spinner) v
                .findViewById(R.id.sel_country_code_register);

        selectInsurance = (EditText) v.findViewById(R.id.sel_insur_register);
        addPhoto = (ImageView) v.findViewById(R.id.add_photo);
        phoneFormat(phoneNumber);
        setTouchNClick(v.findViewById(R.id.btn_next));

        setTouchNClick(addPhoto);
        setTouchNClick(state);
        setTouchNClick(fbSig);
        setTouchNClick(selectInsurance);

        loadValues();

        return v;
    }

    private void phoneFormat(final EditText ph) {
        ph.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int maxLengthofEditText = 12;
                    ph.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            maxLengthofEditText)});

                    String str = PhoneNumberUtils.formatNumber("+1"
                            + ph.getText().toString());
                    ph.setText(str.replace("+1-", "").replace("+1", ""));

                    Log.e("ph.getText().toString()", ""
                            + ph.getText().toString());
                } else {
                    String str = (ph.getText().toString());
                    ph.setText(str.replace("-", ""));
                    int maxLengthofEditText = 10;
                    ph.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            maxLengthofEditText)});
                }
            }
        });
    }

    void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        hideKeyboard();
        if (v == v.findViewById(R.id.btn_next)) {
            if (isValid()) {
                selectedState = addressState.getSelectedItemPosition() - 1;
                user = getUser();

                doRegister();
            }
        }
        if (v == fbSig) {

            onCall();
        }
        if (v == addPhoto) {
            boolean result = Utility.checkPermission(getActivity());
            if (result) {
                selectImage();
            }

        }
        if (v == selectInsurance) {

            selectInsurances();
        }
        if (v == state) {

            showInsDialog2();
        }

    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Image!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity()
                            .startActivityForResult(camera, CAMERA_REQUEST);
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

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void onCall() {

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json,
                                                            GraphResponse response) {

                                        if (response.getError() != null) {
                                            Toast.makeText(getActivity(),
                                                    "Error occurs",
                                                    Toast.LENGTH_LONG).show();
                                            System.out.println("ERROR");
                                        } else {
                                            loadvalue(json);
                                        }
                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters
                                .putString("fields",
                                        "id ,name,first_name, last_name, picture.type(large),email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Utils.showDialog(getActivity(),
                                "Error occurs,some thing went wrong");
                    }

                });
    }

    void loadvalue(JSONObject json) {
        try {
            String id = json.getString("id");
            String fName = json.getString("first_name");
            String lName = json.getString("last_name");
            String email = json.getString("email");

            JSONObject jsobj = json.getJSONObject("picture");
            JSONObject jsobj2 = jsobj.getJSONObject("data");
            String imageUrl = jsobj2.getString("url");
            if (email != null) {
                this.email.setText(email);
                if (imageUrl != null) {
                    loadImage(imageUrl);
                }
                if (fName != null) {
                    firstName.setText(fName);
                }
                if (lName != null) {
                    lastName.setText(lName);
                }
                if (id != null) {
                    password.setText(id);
                    password.setVisibility(View.GONE);
                } else {
                    password.setText("123456");
                    password.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(
                        getActivity(),
                        "Account restriction not able to get email address of facebook account",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            // imgUri = data.getData();
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
        } else {

            callbackManager.onActivityResult(requestCode, resultCode, data);
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
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
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

    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
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

    private void setActionBar() {
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(getString(R.string.register));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private boolean isValid() {
        if (firstName.getText().toString().length() == 0
                || lastName.getText().toString().length() == 0
                || companyName.getText().toString().length() == 0
                || username.getText().toString().length() == 0
                || email.getText().toString().length() == 0
                || password.getText().toString().length() == 0
                || address.getText().toString().length() == 0
                || town.getText().toString().length() == 0
                || state.getText().toString().length() == 0
                || addressState.getSelectedItem().toString().length() == 0
                || zipCode.getText().toString().length() == 0
                || phoneNumber.getText().toString().length() == 0
                || agencyLicenseNumber.getText().toString().length() == 0
                || selectCountryCode.getSelectedItem().toString().length() == 0
                || selectInsurance.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "All fields are required to filled.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Utils.isValidEmail(email.getText().toString())) {
            Toast.makeText(getActivity(), "Invalid email address !.",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(getActivity(), "Password lenght is too short",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (zipCode.getText().toString().length() > 5) {
            Toast.makeText(getActivity(),
                    "ZipCode length should be in length 5", Toast.LENGTH_LONG)
                    .show();
            return false;
        } else if (username.getText().toString().length() < 3) {
            Toast.makeText(getActivity(),
                    "Username length should be greater then 6",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (phoneNumber.getText().toString().length() < 10) {
            Toast.makeText(getActivity(), "PhoneNumber length should be 10",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private User getUser() {
        User u = new User();
        u.setName(firstName.getText().toString() + " "
                + lastName.getText().toString());
        u.setFName(firstName.getText().toString());
        u.setLName(lastName.getText().toString());
        u.setCompanyName(companyName.getText().toString());
        u.setUsername(username.getText().toString());
        u.setEmail(email.getText().toString());
        u.setPassword(password.getText().toString());
        u.setAddress(address.getText().toString());
        u.setCity(town.getText().toString());
        u.setStates(selectedItems2);
        u.setZipCode(zipCode.getText().toString());
        u.setPhone(phoneNumber.getText().toString());
        u.setLicenseno(agencyLicenseNumber.getText().toString());

        u.setState(insListState.get(selectedState).getName());
        u.setCountry(selectCountryCode.getSelectedItem().toString());

        u.setInsurance(selectedItems);
        if (!Commons.isEmpty(returnedPhotoPath))
            u.setPhoto(returnedPhotoPath);
        else
            u.setPhoto("");

        return u;
    }

    private void showInsDialog() {
        final CharSequence[] items = new CharSequence[insList.size()];
        final ArrayList<String> selItems = new ArrayList<String>();
        selItems.addAll(selectedItems);
        for (int i = 0; i < insList.size(); i++) {
            items[i] = insList.get(i).getName();
        }

        if (chkItems == null || chkItems.length == 0)
            chkItems = new boolean[insList.size()];

        int count = 0;
        for (InsuranceModel mod : insList) {
            if (selectedItems.contains(mod.getId() + "")) {
                chkItems[count] = true;
            } else {
                chkItems[count] = false;
            }
            count++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Insurence")
                .setMultiChoiceItems(items, chkItems,
                        new OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                chkItems[which] = isChecked;
                                if (isChecked)
                                    selItems.add(selItems.size(),
                                            insList.get(which).getId() + "");
                                else if (selItems.contains(insList.get(which)
                                        .getId() + "")) {
                                    for (int i = 0; i < selItems.size(); i++) {
                                        if (selItems.get(i)
                                                .equals(insList.get(which)
                                                        .getId() + "")) {
                                            selItems.remove(i);
                                            break;
                                        }
                                    }
                                }

                            }
                        }).setPositiveButton("Ok", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selected = "";
                selectedItems.clear();
                selectedItems.addAll(selItems);

                for (InsuranceModel mod : insList) {
                    if (selectedItems.contains(mod.getId() + "")) {
                        if (!TextUtils.isEmpty(selected))
                            selected += ",";
                        selected += mod.getName();
                    }
                }

                selectInsurance.setText(selected);

            }
        }).setNegativeButton("Cancel", null).show();

    }

    private void showInsDialog2() {
        final CharSequence[] items = new CharSequence[insListState.size()];
        final ArrayList<String> selItems = new ArrayList<String>();
        selItems.addAll(selectedItems2);
        for (int i = 0; i < insListState.size(); i++) {
            items[i] = insListState.get(i).getName();
        }

        if (chkItems2 == null || chkItems2.length == 0)
            chkItems2 = new boolean[insListState.size()];

        int count = 0;
        for (StateModel mod : insListState) {
            if (selectedItems2.contains(mod.getId() + "")) {
                chkItems2[count] = true;
            } else {
                chkItems2[count] = false;
            }
            count++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select State")
                .setMultiChoiceItems(items, chkItems2,
                        new OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                chkItems2[which] = isChecked;
                                if (isChecked)
                                    selItems.add(selItems.size(), insListState
                                            .get(which).getId() + "");
                                else if (selItems.contains(insListState.get(
                                        which).getId()
                                        + "")) {
                                    for (int i = 0; i < selItems.size(); i++) {
                                        if (selItems.get(i).equals(
                                                insListState.get(which).getId()
                                                        + "")) {
                                            selItems.remove(i);
                                            break;
                                        }
                                    }
                                }

                            }
                        }).setPositiveButton("Ok", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selected = "";
                selectedItems2.clear();
                selectedItems2.addAll(selItems);

                for (StateModel mod : insListState) {
                    if (selectedItems2.contains(mod.getId() + "")) {
                        if (!TextUtils.isEmpty(selected))
                            selected += ",";
                        selected += mod.getName();
                    }
                }

                state.setText(selected);

            }
        }).setNegativeButton("Cancel", null).show();

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
                                chkItems2 = new boolean[insListState.size()];
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

    public void selectInsurances() {
        if (insList == null || insList.size() == 0) {
            if (Utils.isOnline()) {
                ((Register) getActivity()).showProgressDialog("");

                String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_INSURANCES;
                client.setTimeout(getCallTimeout);

                client.get(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                        ((Register) getActivity()).dismissProgressDialog();

                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        ((Register) getActivity()).dismissProgressDialog();

                        response2 = new String(responseBody);
                        if (response2 != null) {
                            insList = WebAccess.getAllInsurances(response2);

                            if (insList != null && insList.size() > 0) {
                                chkItems = new boolean[insList.size()];
                                showInsDialog();
                            }
                        } else
                            Utils.showDialog(getActivity(), "Error occurs");

                    }

                });
            } else {
                Utils.noInternetDialog(getActivity());
            }
        } else {
            showInsDialog();
        }
    }

    public void loadValues() {
        selectCountryCode.setAdapter(((Register) getActivity())
                .getAdapter(((Register) getActivity()).getCountryCodeList()));

        ArrayList<String> s = new ArrayList<String>();
        s.add("State");
        for (int i = 0; i < insListState.size(); i++) {
            StateModel ins = insListState.get(i);
            s.add(ins.getName());

        }

        if (s != null) {
            addressState.setAdapter(((Register) getActivity()).getAdapter(s));
        }
    }

    private void doRegister() {
        showProgressDialog("");
        RequestParams param = new RequestParams();

        param.put("CompanyName", user.getCompanyName());

        param.put("ZipCode", user.getZipCode());
        param.put("address", user.getAddress());
        param.put("city", user.getCity());
        param.put("country", user.getCountry());
        param.put("email", user.getEmail());
        param.put("licenseno", user.getLicenseno());
        param.put("name", user.getName());
        param.put("firstname", user.getFName());
        param.put("lastname", user.getLName());
        param.put("password", user.getPassword());
        param.put("phone", user.getPhone());

        param.put("state", user.getState());

        if (user.getStates() != null) {
            for (int s = 0; s < user.getStates().size(); s++) {
                String state = user.getStates().get(s);
                param.put("States[" + s + "]", state);
            }

        }
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

            public void onProgress(int bytesWritten, int totalSize) {
                // TODO Auto-generated method stub
                super.onProgress(bytesWritten, totalSize);
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

                            Utils.showDialog(
                                    getActivity(),
                                    "Thank You for choosing Insta-Bail.  You will receive 48hrs to review the full application for free.  After, your monthly subscription will continue until you choose to cancel.",
                                    new OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            getActivity().finish();

                                        }
                                    });
                        } else if (json.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(getActivity(),
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(getActivity());
                            sp.edit().putBoolean("isFbLogin", false);
                            sp.edit().putString("user", null).commit();
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

    // /////////////////////////////////////////////////////////////////////////////////
    public void loadImage(String url) {
        showProgressDialog("");
        client.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                Bitmap bm = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                addPhoto.setImageBitmap(ImageUtils.getCircularBitmap(bm));
                SaveImage(bm);
                File file = new File("/storage/emulated/0/temp.jpg");
                imgUri = getImageContentUri(getActivity(), file);

                try {
                    imgIS = getActivity().getContentResolver().openInputStream(
                            imgUri);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                dismissProgressDialog();
                Toast.makeText(getActivity(),
                        "Error ocurse please try again later",
                        Toast.LENGTH_LONG).show();
            }

        });
    }

}
