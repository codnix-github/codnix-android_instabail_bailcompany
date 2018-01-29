package com.bailcompany.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    String defendantUrl, defendantBondUrl;
    boolean isSender, IsBailRequest;
    ListView incomingRequestList;
    int page = 0;
    boolean loadingMore;
    DefendanrBondAdapter adapter;
    CircleImageView defProfile;
    TextView tvDefName, tvDefLocation;
    String defId = "";
    private DefendantModel defModel;
    private boolean showProgressDialog = true;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

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
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.title_activity_defendant_detail));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

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
                                                    JSONObject empObj = defNoteDtl.getJSONObject(i);
                                                    noteModel.setId(empObj.getString("Id"));
                                                    noteModel.setDefId(empObj.getString("DefId"));
                                                    noteModel.setNote(empObj.getString("Note"));
                                                    noteModel.setStatus(empObj.getString("Status"));
                                                    noteModel.setModifyOn(empObj.getString("ModifyOn"));
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
                getBondDetails();
            } else if (key.equalsIgnoreCase(Const.DEFENDANT_BASIC_DETAILS_UPDATED)) {
                showProgressDialog = false;
                geDefendantProfile();
            }


        }
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

            if (position % 2 == 0) {
                lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
                llWarrantDetails.setBackgroundColor(Color.parseColor("#F1EFEF"));
            } else {
                lp.setBackgroundColor(Color.WHITE);
                llWarrantDetails.setBackgroundColor(Color.WHITE);
            }


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

                    if (!wMod.getCourtDate().equalsIgnoreCase("")) {
                        ((LinearLayout) v.findViewById(R.id.llCourtDate)).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.wrntCourtDate))
                                .setText("Court Date:    " + Utils.getRequiredDateFormatGMT("yyyy-MM-dd", "MM/dd/yyyy", wMod.getCourtDate()));

                    }
                    llWarrantDetails.addView(v);

                }

            }

            ((TextView) convertView.findViewById(R.id.date)).setText(Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                    "MM/dd/yyyy", bailReqList.get(position).getCreatedDate()));


            int i = incomingRequestList.getLastVisiblePosition();
            int j = incomingRequestList.getAdapter().getCount();
            i++;

            return convertView;
        }
    }


}
