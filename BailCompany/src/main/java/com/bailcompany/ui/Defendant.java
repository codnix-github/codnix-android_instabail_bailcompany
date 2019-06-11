package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.DefendantBasicProfileDetails;
import com.bailcompany.DefendantBondDetails;
import com.bailcompany.DefendantEmploymentDetails;
import com.bailcompany.DefendantNoteDetails;
import com.bailcompany.DefendantVehicleDetails;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.DefendantEmploymentModel;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.DefendantNotesModel;
import com.bailcompany.model.DefendantVehicleModel;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.tools.NonScrollListView;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("InflateParams")
public class Defendant extends CustomActivity {

    public static ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;
    static Bitmap bmDefendant;
    private static DefendantModel defModel;
    String defendantUrl, defendantBondUrl;
    boolean isSender, IsBailRequest;
    ListView incomingRequestList;
    int page = 0;
    boolean loadingMore;
    DefendanrBondAdapter adapter;
    CircleImageView defProfile;
    TextView tvDefName, tvDefLocation;
    String defId = "";
    private boolean showProgressDialog = true;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    private Menu menu;

    private Context _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendant_profile);
        setActionBar();
        _activity = Defendant.this;
        if (getIntent().hasExtra("defId")) {
            defId = getIntent().getStringExtra("defId");
            if (defId == null || defId.trim().equalsIgnoreCase("")) {
                finish();
            }
        } else {
            finish();

        }

        defProfile = (CircleImageView) findViewById(R.id.profile_pic);
        tvDefLocation = (TextView) findViewById(R.id.tvDefLocation);
        tvDefName = (TextView) findViewById(R.id.tvDefName);
        defendantUrl = WebAccess.GET_DEFENDANT_DETAIL;
        defendantBondUrl = WebAccess.GET_DEFENDANT_BOND_DETAIL;
        isSender = false;
        IsBailRequest = true;
        geDefendantProfile();
        incomingRequestList = (NonScrollListView) findViewById(R.id.incoming_request_list);
        ((Button) findViewById(R.id.btnBasicDetails)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(THIS,
                                DefendantBasicProfileDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });
        ((Button) findViewById(R.id.btnEmployment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(_activity,
                                DefendantEmploymentDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });
        ((Button) findViewById(R.id.btnVehicle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(_activity,
                                DefendantVehicleDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });
        ((Button) findViewById(R.id.btnNotes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(_activity,
                                DefendantNoteDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });


        adapter = new DefendanrBondAdapter(_activity);
        incomingRequestList.setAdapter(adapter);
        incomingRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                bailReqList.get(position).setRead("1");

                startActivityForResult(
                        new Intent(THIS,
                                DefendantBondDetails.class).putExtra(
                                "bail", bailReqList.get(position)).putExtra("defendant", defModel).putExtra(
                                "position", position), 5555);
            }
        });

        getBondDetails();

        defProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (defModel.getPhoto() == null || defModel.getPhoto().trim().equalsIgnoreCase(""))
                    return;
                // hideKeyboard();

                F1.newInstance().show(getFragmentManager(), null);
            }
        });
        ImageView ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (defModel != null && defModel.getFacebookURL() != null && !defModel.getFacebookURL().trim().equals("")) {
                    if (Patterns.WEB_URL.matcher(defModel.getGoogleURL()).matches()) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(defModel.getFacebookURL()));
                        startActivity(i);
                    }
                }
            }
        });

        ImageView ivInstagram = (ImageView) findViewById(R.id.ivInstagram);
        ivInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // google url is instagram url
                if (defModel != null && defModel.getGoogleURL() != null && !defModel.getGoogleURL().trim().equals("")) {
                    if (Patterns.WEB_URL.matcher(defModel.getGoogleURL()).matches()) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(defModel.getGoogleURL()));
                        startActivity(i);
                    }

                }
            }
        });
        ImageView ivTwitter = (ImageView) findViewById(R.id.ivTwitter);
        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (defModel != null && defModel.getTwitterURL() != null && !defModel.getTwitterURL().trim().equals("")) {
                    if (Patterns.WEB_URL.matcher(defModel.getGoogleURL()).matches()) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(defModel.getTwitterURL()));
                        startActivity(i);
                    }
                }
            }
        });
        findViewById(R.id.llSendNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationDialog.newInstance().show(getFragmentManager(), "dialog");
            }
        });


        ImageView ivCallDefendant = (ImageView) findViewById(R.id.ivCallDefendant);
        ivCallDefendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!defModel.getCellTele().equals("")) {
                    if (ActivityCompat.checkSelfPermission(Defendant.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + defModel.getCellTele()));
                    startActivity(callIntent);
                } else if (!defModel.getHomeTele().equals("")) {
                    if (ActivityCompat.checkSelfPermission(Defendant.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + defModel.getHomeTele()));
                    startActivity(callIntent);
                }

            }
        });

        ImageView ivSMSDefendant = (ImageView) findViewById(R.id.ivSMSDefendant);
        ivSMSDefendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!defModel.getCellTele().equals("")) {
                    Utils.sendMessage(Defendant.this, "+1" + defModel.getCellTele());
                } else if (!defModel.getHomeTele().equals("")) {
                    Utils.sendMessage(Defendant.this, "+1" + defModel.getHomeTele());
                }

            }
        });

        tvDefLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + tvDefLocation.getText().toString()));
                startActivity(i);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


    }

    protected void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(getString(R.string.title_activity_defendant_detail));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        if (item.getItemId() == R.id.action_delete)
            deleteDefendant();
        else if (item.getItemId() == R.id.action_request_checkin)
            sendNotificationToDefendant("", 105);

        return super.onOptionsItemSelected(item);

    }


    private void deleteDefendant() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(THIS, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(THIS);
        }
        if (defModel.getStatus().equalsIgnoreCase("D")) {
            builder.setTitle("Activate Defendant").setMessage("Are you sure you want to activate this defendant?");
        } else {
            builder.setTitle("Deactivate Defendant").setMessage("Are you sure you want to deactivate this defendant?");
        }


        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue
                updateDefendantActivationStatus();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_defendant, menu);
        this.menu = menu;

        // return true so that the menu pop up is opened
        return true;
    }


    void updateDefendantActivationStatus() {
        if (Utils.isOnline(_activity)) {
            if (showProgressDialog) {
                showProgressDialog("");
            }
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("DefId", defId);
            if (defModel.getStatus().equalsIgnoreCase("D"))
                param.put("Status", "N");
            else
                param.put("Status", "D");

            String url = WebAccess.MAIN_URL + WebAccess.UPDATE_DEFENDANT_ACCOUNT_STATUS;
            client.setTimeout(getCallTimeout);
            client.post(_activity, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            if (showProgressDialog)
                                dismissProgressDialog();
                            Utils.showDialog(_activity,
                                    R.string.err_unexpect);
                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                            if (showProgressDialog) {
                                dismissProgressDialog();

                            }
                            try {
                                String response2;

                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {
                                        MenuItem menu_action = menu.findItem(R.id.action_delete);

                                        if (defModel.getStatus().equalsIgnoreCase("D")) {
                                            menu_action.setTitle("Deactivate");
                                            defModel.setStatus("N");
                                        } else {
                                            menu_action.setTitle("Activate");
                                            defModel.setStatus("D");
                                        }

                                        Toast.makeText(
                                                _activity,
                                                resObj.optString("message"),
                                                Toast.LENGTH_LONG).show();

                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                _activity,
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(_activity,
                                                Launcher.class));
                                    } else {

                                        if (page == 0) {
                                            Utils.showDialog(THIS,
                                                    "Details not avaialble")
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            if (page > 0)
                                                page--;
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    });

        } else
            Utils.noInternetDialog(_activity);
    }


    void geDefendantProfile() {
        if (Utils.isOnline(_activity)) {
            if (showProgressDialog) {
                showProgressDialog("");
            }
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("Page", page);
            param.put("Id", defId);

            String url = WebAccess.MAIN_URL + defendantUrl;
            client.setTimeout(getCallTimeout);
            client.post(_activity, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            if (showProgressDialog)
                                dismissProgressDialog();
                            Utils.showDialog(_activity,
                                    R.string.err_unexpect);

                            if (page > 0)
                                page--;
                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                            if (showProgressDialog) {
                                dismissProgressDialog();
                                WebAccess.AllBidListCompany.clear();
                            }
                            try {
                                String response2;
                                loadingMore = false;
                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {


                                        Log.d("Data", resObj.toString());

                                        defModel = new DefendantModel();
                                        JSONArray arrDef = resObj.getJSONArray("profile");
                                        if (arrDef.length() > 0) {
                                            JSONObject defObject = arrDef.getJSONObject(0);
                                            defModel.setId(defObject.getString("Id"));
                                            defModel.setCompanyId(defObject.getString("CompanyId"));
                                            defModel.setFirstName(defObject.getString("FirstName"));
                                            defModel.setLastName(defObject.getString("LastName"));
                                            defModel.setAddress(defObject.getString("Address"));
                                            defModel.setTown(defObject.getString("Town"));
                                            defModel.setState(defObject.getString("State"));
                                            defModel.setZipcode(defObject.getString("Zipcode"));
                                            defModel.setDOB(defObject.getString("DOB"));
                                            defModel.setSSN(defObject.getString("SSN"));
                                            defModel.setPOB(defObject.getString("POB"));
                                            defModel.setHomeTele(defObject.getString("HomeTele"));
                                            defModel.setCellTele(defObject.getString("CellTele"));
                                            defModel.setHeight(defObject.getString("Height"));
                                            defModel.setWeight(defObject.getString("Weight"));
                                            defModel.setHairColor(defObject.getString("HairColor"));
                                            defModel.setEyeColor(defObject.getString("EyeColor"));
                                            defModel.setTattoos(defObject.getString("Tattoos"));
                                            defModel.setMaritalStatus(defObject.getString("MaritalStatus"));
                                            defModel.setPhoto(defObject.getString("Photo"));
                                            defModel.setFacebookURL(defObject.getString("FacebookURL"));
                                            defModel.setTwitterURL(defObject.getString("TwitterURL"));
                                            defModel.setGoogleURL(defObject.getString("GoogleURL"));
                                            defModel.setStatus(defObject.getString("Status"));
                                            defModel.setModifyOn(defObject.getString("ModifyOn"));
                                            defModel.setStateName(defObject.getString("StateName"));

                                            JSONArray defEmploymentDtl = resObj.getJSONArray("employementdetails");
                                            JSONArray defVehicleDtl = resObj.getJSONArray("vehicledetails");
                                            JSONArray defNoteDtl = resObj.getJSONArray("notes");

                                            ArrayList<DefendantEmploymentModel> employmentDtl = new ArrayList<DefendantEmploymentModel>();
                                            if (defEmploymentDtl != null && defEmploymentDtl.length() > 0) {
                                                for (int i = 0; i < defEmploymentDtl.length(); i++) {
                                                    DefendantEmploymentModel empModel = new DefendantEmploymentModel();
                                                    JSONObject empObj = defEmploymentDtl.getJSONObject(i);
                                                    empModel.setId(empObj.getString("Id"));
                                                    empModel.setDefId(empObj.getString("DefendantId"));
                                                    empModel.setEmployer(empObj.getString("Employer"));
                                                    empModel.setOccupation(empObj.getString("Occupation"));
                                                    empModel.setAddress(empObj.getString("Address"));
                                                    empModel.setCity(empObj.getString("City"));
                                                    empModel.setState(empObj.getString("State"));
                                                    empModel.setZip(empObj.getString("Zip"));
                                                    empModel.setTelephone(empObj.getString("Telephone"));
                                                    empModel.setSupervisor(empObj.getString("Supervisor"));
                                                    empModel.setDuration(empObj.getString("Duration"));
                                                    empModel.setStatus(empObj.getString("Status"));
                                                    empModel.setModifyOn(empObj.getString("ModifyOn"));
                                                    employmentDtl.add(empModel);

                                                }
                                            }
                                            defModel.setEmploymentDtl(employmentDtl);

                                            ArrayList<DefendantVehicleModel> vehicleDtl = new ArrayList<>();
                                            if (defVehicleDtl != null && defVehicleDtl.length() > 0) {
                                                for (int i = 0; i < defVehicleDtl.length(); i++) {
                                                    DefendantVehicleModel vehiModel = new DefendantVehicleModel();
                                                    JSONObject empObj = defVehicleDtl.getJSONObject(i);
                                                    vehiModel.setId(empObj.getString("Id"));
                                                    vehiModel.setDefId(empObj.getString("DefendantId"));
                                                    vehiModel.setYear(empObj.getString("Year"));
                                                    vehiModel.setMake(empObj.getString("Make"));
                                                    vehiModel.setModel(empObj.getString("Model"));
                                                    vehiModel.setColor(empObj.getString("Color"));
                                                    vehiModel.setState(empObj.getString("State"));
                                                    vehiModel.setRegistration(empObj.getString("Registration"));
                                                    vehiModel.setStatus(empObj.getString("Status"));
                                                    vehiModel.setModifyOn(empObj.getString("ModifyOn"));
                                                    vehicleDtl.add(vehiModel);

                                                }
                                            }
                                            defModel.setVehicleDtl(vehicleDtl);
                                            ArrayList<DefendantNotesModel> nodeDtl = new ArrayList<>();
                                            if (defNoteDtl != null && defNoteDtl.length() > 0) {
                                                for (int i = 0; i < defNoteDtl.length(); i++) {
                                                    DefendantNotesModel noteModel = new DefendantNotesModel();
                                                    JSONObject noteObj = defNoteDtl.getJSONObject(i);
                                                    noteModel.setId(noteObj.getString("Id"));
                                                    noteModel.setDefId(noteObj.getString("DefId"));
                                                    noteModel.setNote(noteObj.getString("Note"));
                                                    noteModel.setStatus(noteObj.getString("Status"));
                                                    noteModel.setModifyOn(noteObj.getString("ModifyOn"));
                                                    noteModel.setImage(noteObj.getString("Image"));

                                                    nodeDtl.add(noteModel);

                                                }
                                            }
                                            defModel.setNotesDtl(nodeDtl);

                                            Glide.with(THIS)
                                                    .load(WebAccess.PHOTO + defModel.getPhoto())
                                                    .asBitmap()
                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                    .skipMemoryCache(true)
                                                    .into(new SimpleTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super
                                                                Bitmap> glideAnimation) {
                                                            bmDefendant = resource;
                                                            defProfile.setImageBitmap(resource);
                                                        }
                                                    });

                                            String fullAddress = "";
                                            if (defModel.getAddress() != "") {
                                                fullAddress = defModel.getAddress() + ", ";
                                            }
                                            if (defModel.getTown() != "") {
                                                fullAddress += defModel.getTown() + ", ";
                                            }
                                            if (defModel.getState() != "") {
                                                fullAddress += defModel.getState() + ", ";
                                            }
                                            if (defModel.getZipcode() != "") {
                                                fullAddress += defModel.getZipcode();
                                            }
                                            tvDefLocation.setText(fullAddress);
                                            tvDefName.setText(defModel.getFirstName() + " " + defModel.getLastName());

                                            MenuItem menu_action = menu.findItem(R.id.action_delete);
                                            if (defModel.getStatus().equalsIgnoreCase("D")) {
                                                menu_action.setTitle("Activate");

                                            } else {
                                                menu_action.setTitle("Deactivate");

                                            }

                                        }


                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                _activity,
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(_activity,
                                                Launcher.class));
                                    } else {

                                        if (page == 0) {
                                            Utils.showDialog(THIS,
                                                    "Details not avaialble")
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            if (page > 0)
                                                page--;
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    });

        } else
            Utils.noInternetDialog(_activity);
    }

    void getBondDetails() {
        if (Utils.isOnline(_activity)) {
            if (showProgressDialog) {
                showProgressDialog("");
            }
            RequestParams param = new RequestParams();
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("Page", page);
            param.put("Id", defId);

            String url = WebAccess.MAIN_URL + defendantBondUrl;
            client.setTimeout(getCallTimeout);
            client.post(_activity, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            if (showProgressDialog)
                                dismissProgressDialog();
                            Utils.showDialog(_activity,
                                    R.string.err_unexpect);

                            if (page > 0)
                                page--;
                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                            if (showProgressDialog) {
                                dismissProgressDialog();
                                WebAccess.AllBidListCompany.clear();
                            }
                            try {
                                String response2;
                                loadingMore = false;
                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {
                                        bailReqList.clear();
                                        WebAccess.getDefendantBonds(response2);
                                        if (bailReqList != null
                                                && bailReqList.size() > 0) {
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Utils.showDialog(_activity,
                                                    "No Bail Request Available")
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        }


                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                _activity,
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(_activity,
                                                Launcher.class));
                                    } else {

                                        if (page == 0) {
                                            Utils.showDialog(THIS,
                                                    "Details not avaialble")
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            if (page > 0)
                                                page--;
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    });

        } else
            Utils.noInternetDialog(_activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        incomingRequestList.setAdapter(adapter);
        if (resultCode == Activity.RESULT_OK) {
            String key = data.getStringExtra(Const.RETURN_FLAG);
            if (key.equalsIgnoreCase(Const.BOND_DETAILS_UPDATED) || key.equalsIgnoreCase(Const.BOND_DOCUMENT_UPLOADED)) {
                showProgressDialog = false;
                // getBondDetails();
            } else if (key.equalsIgnoreCase(Const.DEFENDANT_BASIC_DETAILS_UPDATED)) {
                showProgressDialog = false;
                geDefendantProfile();
            }


        }
    }


    public void sendNotificationToDefendant(String messsage, int type) {
        if (Utils.isOnline(_activity)) {
            if (showProgressDialog) {
                showProgressDialog("");
            }
            RequestParams param = new RequestParams();
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("DefId", defId);
            param.put("Message", messsage);
            param.put("Type", type);


            String url = WebAccess.MAIN_URL + WebAccess.SEND_NOTIFICATION_TO_DEFENDANT;
            client.setTimeout(getCallTimeout);
            client.post(_activity, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            if (showProgressDialog)
                                dismissProgressDialog();
                            Utils.showDialog(_activity,
                                    R.string.err_unexpect);


                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                            if (showProgressDialog) {
                                dismissProgressDialog();

                            }
                            try {
                                String response2;

                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {
                                        Toast.makeText(
                                                _activity,
                                                resObj.optString("message"),
                                                Toast.LENGTH_LONG).show();

                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                _activity,
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(_activity,
                                                Launcher.class));
                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("0")) {
                                        Toast.makeText(
                                                _activity,
                                                resObj.optString("message"),
                                                Toast.LENGTH_LONG).show();

                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    });

        } else
            Utils.noInternetDialog(_activity);

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

            if (bmDefendant != null) {

                bm.setImageBitmap(bmDefendant);
            } else {

            }


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

    public static class NotificationDialog extends DialogFragment {

        public static NotificationDialog newInstance() {
            NotificationDialog f1 = new NotificationDialog();
         /*   f1.setStyle(DialogFragment.STYLE_NO_FRAME,
                    android.R.style.Theme_DeviceDefault_Dialog);
*/

            return f1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Remove the default background
            getDialog().getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.WHITE));
            getDialog().getWindow().setTitle("Send Notification to " + defModel.getFirstName() + " " + defModel.getLastName());

            // Inflate the new view with margins and background
            View v = inflater.inflate(R.layout.dialog_send_notification, container, true);

            final EditText edtNotification = v.findViewById(R.id.edtNotification);
            final CheckBox cbBatteryStatus = v.findViewById(R.id.cbBatteryStatus);
            final CheckBox cbLocationStatus = v.findViewById(R.id.cbLocationStatus);
            Button btnSendNotification = v.findViewById(R.id.btnSendNotification);
            btnSendNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edtNotification.getText().toString().trim().equalsIgnoreCase("") && !cbBatteryStatus.isChecked() && !cbLocationStatus.isChecked()) {
                        return;
                    }
                    int type = 104;  //normal message
                    if (cbBatteryStatus.isChecked() && cbLocationStatus.isChecked()) {
                        type = 103;
                    } else if (cbBatteryStatus.isChecked()) {
                        type = 101;
                    } else if (cbLocationStatus.isChecked()) {
                        type = 102;
                    }

                    ((Defendant) getActivity()).sendNotificationToDefendant(edtNotification.getText().toString().trim(), type);
                    dismiss();
                }
            });

            return v;
        }
    }

    @SuppressLint("InflateParams")
    private class DefendanrBondAdapter extends BaseAdapter {
        public DefendanrBondAdapter(Context context) {

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return bailReqList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return bailReqList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //  if (convertView == null)
            convertView = getLayoutInflater()
                    .inflate(R.layout.defendant_bond_item, null);
            LinearLayout lp = (LinearLayout) convertView.findViewById(R.id.lp);
            LinearLayout llWarrantDetails = (LinearLayout) convertView.findViewById(R.id.llWarrantDetails);

          /*  if (position % 2 == 0) {
                lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
                llWarrantDetails.setBackgroundColor(Color.parseColor("#F1EFEF"));
            } else {
                lp.setBackgroundColor(Color.WHITE);
                llWarrantDetails.setBackgroundColor(Color.WHITE);
            }
*/

            for (int i = 0; i < bailReqList.get(position).getWarrantList().size(); i++) {
                WarrantModel wMod = bailReqList.get(position).getWarrantList().get(i);
                if (wMod != null) {
                    View v = getLayoutInflater()
                            .inflate(R.layout.row_defendant_warrant, null);

                    ((TextView) v.findViewById(R.id.wrntAmount))
                            .setText("Amount:   $" + wMod.getAmount());
                    ((TextView) v.findViewById(R.id.wrntTownship))
                            .setText("Township:   " + wMod.getTownship());
                    ((TextView) v.findViewById(R.id.wrntCaseNum))
                            .setText("CaseNo:   " + wMod.getCase_no());
                    ((TextView) v.findViewById(R.id.wrntPowerNum))
                            .setText("PowerNo:    " + wMod.getPowerNo());

                  /*  if (!wMod.getCourtDate().equalsIgnoreCase("")) {
                        ((LinearLayout) v.findViewById(R.id.llCourtDate)).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.wrntCourtDate))
                                .setText("COURT DATE:    " + Utils.getRequiredDateFormat("yyyy-MM-dd", "MM/dd/yyyy", wMod.getCourtDate()));

                      *//*  ((TextView) v.findViewById(R.id.wrntCourtDate))
                                .setText("COURT DATE:    " + wMod.getCourtDate());*//*

                    }
*/
                    if (!wMod.getLatestCourtDate().equalsIgnoreCase("")) {
                        ((LinearLayout) v.findViewById(R.id.llCourtDate)).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.wrntCourtDate))
                                .setText("Court Date:    " + Utils.getRequiredDateFormat("yyyy-MM-dd hh:mm:ss", "MM/dd/yyyy hh:mm", wMod.getLatestCourtDate()));


                    }

                    if (wMod.getStatus() != null && !wMod.getStatus().equalsIgnoreCase("")) {
                        ((LinearLayout) v.findViewById(R.id.llBondStatus)).setVisibility(View.VISIBLE);
                        String status = wMod.getStatus();

                        if (status.equalsIgnoreCase("A")) {
                            status = "ACTIVE";
                            ((TextView) v.findViewById(R.id.tvBondStatus)).setBackgroundResource(R.drawable.round_text_active);
                        } else if (status.equalsIgnoreCase("D")) {
                            status = "DISCHARGED";
                            ((TextView) v.findViewById(R.id.tvBondStatus)).setBackgroundResource(R.drawable.round_text_active);
                        } else if (status.equalsIgnoreCase("F")) {
                            status = "FORFEITURE";
                            ((TextView) v.findViewById(R.id.tvBondStatus)).setBackgroundResource(R.drawable.round_text_active);

                        } else {
                            ((LinearLayout) v.findViewById(R.id.llBondStatus)).setVisibility(View.GONE);
                        }

                        ((TextView) v.findViewById(R.id.tvBondStatus))
                                .setText(status);

                    } else {
                        ((TextView) v.findViewById(R.id.tvBondStatus)).setVisibility(View.GONE);
                    }

                    llWarrantDetails.addView(v);

                }

            }

            ((TextView) convertView.findViewById(R.id.date)).setText("Posted Date: " + Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                    "MM/dd/yyyy", bailReqList.get(position).getCreatedDate()));

            TextView tvStatus = convertView.findViewById(R.id.tvstatus);

            if (bailReqList.get(position).isIsAccept().equals("1")) {

                if (bailReqList.get(position).getAgentId().equals(bailReqList.get(position).getSenderCompanyId())) {
                    tvStatus.setText("Accepted(Self)");
                } else
                    tvStatus.setText("Agent Accepted");
            }

            if (bailReqList.get(position).getAgentArrivedTime() != null && !bailReqList.get(position).getAgentArrivedTime().equals("")) {

                if (bailReqList.get(position).getAgentId().equals(bailReqList.get(position).getSenderCompanyId())) {
                    tvStatus.setText("Arrived(Self)");
                } else
                    tvStatus.setText("Agent Arrived");
            }


            if (bailReqList.get(position).getIsComplete().equals("1")) {
                tvStatus.setText("Completed");
            }
            if (bailReqList.get(position).getIsAbort().equals("1")) {
                if (bailReqList.get(position).getIsCancel().equals("1")) {
                    tvStatus.setText("Cancelled");
                } else {
                    tvStatus.setText("Aborted");
                }
            }


            int i = incomingRequestList.getLastVisiblePosition();
            int j = incomingRequestList.getAdapter().getCount();
            i++;

            return convertView;
        }
    }


}
