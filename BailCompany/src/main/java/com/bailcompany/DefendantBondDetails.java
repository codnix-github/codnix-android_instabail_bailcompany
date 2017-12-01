package com.bailcompany;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.custom.LocationImpl;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.CourtDateModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.User;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.ui.MainFragment;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("InflateParams")
public class DefendantBondDetails extends CustomActivity {
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;
    static Bitmap bmCompany;
    String comp;
    String reqId;
    TextView submit, status, quotesText, amountReqText;
    boolean fromNotification = false;
    MainFragment m = new MainFragment();
    String currentDateTimeString, reqDateTimeString;
    String message;
    JSONObject jsonObj;
    String key;
    int crnthour, reqthour;
    int crntminute, reqtminute;
    BailRequestModel mod = new BailRequestModel();
    User user;
    EditText quotes, amountReq;
    ArrayList<String> dropDownValuesList = new ArrayList<>();
    ArrayList<WarrantStruct> warrantIdList = new ArrayList<>();
    ArrayList<PreFixesHolder> preFixesHolders = new ArrayList<>();
    Context _activity;
    int selectedWarrentId = 0;
    WarrantModel selectedWarrentModel = null;
    JSONArray preFixArr;
    int defId = 1;
    ArrayList<CourtDateModel> warrantCourtDates = new ArrayList<CourtDateModel>();
    ArrayList<WarrantCourtDatesHolder> warrantCourtDatesHolders = new ArrayList<>();
    private BailRequestModel bm;
    private LinearLayout preFixedViewLL;
    private LinearLayout warrantCourtDatesLL;
    private LinearLayout llDefendantDocuments;
    private LinearLayout llCosignerDocuments;
    private LinearLayout llOtherDocuments;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {


        @Override
        public void onDateTimeSet(Date date) {
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendant_bond_details);
        setActionBar();
        _activity = getApplicationContext();
        submit = (TextView) findViewById(R.id.btn_accept);
        amountReq = (EditText) findViewById(R.id.bid_decimal);
        quotes = (EditText) findViewById(R.id.quote_inst);
        status = (TextView) findViewById(R.id.acceptedtext);
        quotesText = (TextView) findViewById(R.id.quote_inst2);
        amountReqText = (TextView) findViewById(R.id.bid_decimal2);
        // loadContent();

        setTouchNClick(R.id.btn_accept);
        getDetail();


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

    private void showDetail() {

        getDropDownValues();


        LinearLayout llWarrant = (LinearLayout) findViewById(R.id.llWarrant);
        llDefendantDocuments = (LinearLayout) findViewById(R.id.llDocumentsDefendant);
        llCosignerDocuments = (LinearLayout) findViewById(R.id.llDocumentsCosigner);
        llOtherDocuments = (LinearLayout) findViewById(R.id.llDocumentsOther);

        ArrayList<WarrantModel> wList = bm.getWarrantList();
        final ArrayList<CourtDateModel> allCourtDates = bm.getCourtDates();

        if (wList != null && wList.size() > 0) {
            int count = 0;
            for (final WarrantModel wMod : wList) {
                View v = getLayoutInflater()
                        .inflate(R.layout.row_warrant, null);
                if (count == 0)
                    v.findViewById(R.id.divider).setVisibility(View.GONE);
                if (wMod != null) {
                    ((TextView) v.findViewById(R.id.wrntAmount))
                            .setText("Amount:   $" + wMod.getAmount());

                    ((TextView) v.findViewById(R.id.wrntAmount)).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            selectedWarrentId = wMod.getId();
                            selectedWarrentModel = wMod;
                            warrantCourtDates.clear();
                            for (final CourtDateModel wCourtDate : allCourtDates) {
                                if (wCourtDate.getWarrantId() == selectedWarrentId) {
                                    warrantCourtDates.add(wCourtDate);
                                }

                            }
                            Log.d("TotalCourtDate=", "" + warrantCourtDates.size());

                            AlertDialog dialog;
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View dialogForm = inflater.inflate(R.layout.dialog_edit_defendant_bond_details, null, false);
                            final LinearLayout loginContainer = (LinearLayout) dialogForm.findViewById(R.id.loginContainer);
                            final LinearLayout signupContainer = (LinearLayout) dialogForm.findViewById(R.id.signupContainer);
                            TextView buttonTabLogin = (TextView) dialogForm.findViewById(R.id.buttonTabLogin);
                            Button buttonAddMoreCourtDate = (Button) dialogForm.findViewById(R.id.add_more_courtdate);
                            buttonTabLogin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loginContainer.setVisibility(View.VISIBLE);
                                    signupContainer.setVisibility(View.GONE);
                                }
                            });
                            TextView buttonTabSignup = (TextView) dialogForm.findViewById(R.id.buttonTabSignup);
                            buttonTabSignup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loginContainer.setVisibility(View.GONE);
                                    signupContainer.setVisibility(View.VISIBLE);
                                }
                            });
                            buttonAddMoreCourtDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addCourtDateView("");
                                }
                            });


                            preFixedViewLL = (LinearLayout) dialogForm.findViewById(R.id.preFixedViewLL);
                            warrantCourtDatesLL = (LinearLayout) dialogForm.findViewById(R.id.warrantCourtDatesLL);

                            warrantCourtDatesHolders.clear();
                            addDropDownViews(wMod);


                            Button buttonLogin = (Button) dialogForm.findViewById(R.id.btnUpdateWarrantDetails);
                            buttonLogin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    updateWarrantDetails();
                                    Toast.makeText(DefendantBondDetails.this, "" + wMod.getId(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog.Builder builder = new AlertDialog.Builder(DefendantBondDetails.this);
                            builder.setView(dialogForm);
                            builder.create();


                            dialog = builder.create();
                            dialog.show();
                        }
                    });
                    ((TextView) v.findViewById(R.id.wrntTownship))
                            .setText("Township:   " + wMod.getTownship() + "");

                    Log.e("wMod.getAmount()", "" + wMod.getAmount());
                    Log.e("wMod.getTownship()", "" + wMod.getTownship());

                    llWarrant.addView(v);
                }
                count++;
            }
        }

        ArrayList<InsuranceModel> insList = bm.getInsuranceList();
        String insur = "";
        if (insList != null && insList.size() > 0) {
            for (int i = 0; i < insList.size(); i++) {
                if (i != 0)
                    insur += "\n";
                insur += insList.get(i).getName();
            }
        }
        ((TextView) findViewById(R.id.insurance)).setText(insur + "");
        ((TextView) findViewById(R.id.indemnNum)).setText(bm
                .getNumberIndemnitors() + "");


        ((TextView) findViewById(R.id.splInstruction)).setText(bm
                .getInstructionForAgent() + "");

        Log.d("BCosigner=", "" + bm.getBondDocuments().getCosignerPhoto().size());
        Log.d("BDef=", "" + bm.getBondDocuments().getDefendantPhoto().size());
        Log.d("BOther=", "" + bm.getBondDocuments().getOtherDocuments().size());

        ArrayList<String> docOtherDoc = bm.getBondDocuments().getOtherDocuments();
        if (docOtherDoc != null && docOtherDoc.size() > 0) {

            for (final String url : docOtherDoc) {
                //tvDocument
                View v = getLayoutInflater()
                        .inflate(R.layout.row_document, null);

                TextView tvDocument = (TextView) v.findViewById(R.id.tvDocument);
                final ImageView ivPic = (ImageView) v.findViewById(R.id.ivPic);
                tvDocument.setText(url);

                Log.d("DocUrlS=",url);

                if(url.toLowerCase().endsWith("jpg") || url.toLowerCase().endsWith("png") || url.toLowerCase().endsWith("jpeg"))
                {
                    Bitmap bm = new ImageLoader(StaticData.getDIP(60),
                            StaticData.getDIP(60), ImageLoader.SCALE_FITXY).loadImage(url, new ImageLoader.ImageLoadedListener() {

                        @Override
                        public void imageLoaded(Bitmap bm) {
                            if (bm != null)
                                ivPic.setImageBitmap(ImageUtils
                                        .getCircularBitmap(bm));

                        }
                    });
                    if (bm != null) {
                        ivPic.setImageBitmap(ImageUtils.getCircularBitmap(bm));
                    }

                }

                llOtherDocuments.addView(v);
            }
        }

        ArrayList<String> docDefenant = bm.getBondDocuments().getDefendantPhoto();
        if (docDefenant != null && docDefenant.size() > 0) {

            for (final String url : docDefenant) {
                //tvDocument
                View v = getLayoutInflater()
                        .inflate(R.layout.row_document, null);

                TextView tvDocument = (TextView) v.findViewById(R.id.tvDocument);
                tvDocument.setText(url);

                llDefendantDocuments.addView(v);
            }
        }

        ArrayList<String> docCosigner = bm.getBondDocuments().getCosignerPhoto();
        if (docCosigner != null && docCosigner.size() > 0) {

            for (final String url : docCosigner) {
                //tvDocument
                View v = getLayoutInflater()
                        .inflate(R.layout.row_document, null);

                TextView tvDocument = (TextView) v.findViewById(R.id.tvDocument);
                tvDocument.setText(url);

                llCosignerDocuments.addView(v);
            }
        }

        RelativeLayout enter = (RelativeLayout) findViewById(R.id.enter);
        RelativeLayout view = (RelativeLayout) findViewById(R.id.view);

    }

    protected void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.bail_req_detail));
    }

    public void updateWarrantDetails() {
        if (Utils.isOnline(_activity)) {
            showProgressDialog("");
            RequestParams param = getWarrantParametersData();
            String url = WebAccess.MAIN_URL + WebAccess.UPDATE_BOND_DETAILS;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                    dismissProgressDialog();
                    Utils.showDialog(DefendantBondDetails.this,
                            R.string.err_unexpect);
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                    dismissProgressDialog();
                    try {
                        String response2;
                        response2 = new String(responseBody);
                        JSONObject resObj = new JSONObject(response2);
                        if (resObj != null) {
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {

                                Utils.showDialog(DefendantBondDetails.this,
                                        resObj.getString("message"));
                                Intent intent = new Intent();
                                intent.putExtra(Const.RETURN_FLAG, Const.BOND_DETAILS_UPDATED);
                                setResult(RESULT_OK, intent);
                                finish();

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
                                startActivity(new Intent(
                                        DefendantBondDetails.this,
                                        Launcher.class));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(DefendantBondDetails.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });


        } else {
            Utils.noInternetDialog(THIS);

        }
    }

    public RequestParams getWarrantParametersData() {

        PreFixesHolder holder = preFixesHolders.get(0);
        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("DefId", defId);
        param.put("warrentId", selectedWarrentId);
        param.put("BondRequestId", reqId);
        param.put("prefix", holder.spinner.getSelectedItem().toString().trim());
        param.put("serial", holder.serialET.getText().toString().trim());
        param.put("caseno", holder.caseNoET.getText().toString().trim());
        param.put("amount", holder.edtAmout.getText().toString().trim());
        param.put("warrentnote", holder.edtNote.getText().toString().trim());
        param.put("courtdate", "");
        param.put("township", holder.edtTownship.getText().toString().trim());
        param.put("bondstatus", "A");
        if (holder.spinnerStatus.getSelectedItemPosition() != 0)
            param.put("bondstatus", holder.spinnerStatus.getSelectedItem().toString().trim());
        Log.d("CourtDateSize", "" + warrantCourtDatesHolders.size());
        for (int i = 0; i < warrantCourtDatesHolders.size(); i++) {
            param.put("courtdate[" + i + "]", warrantCourtDatesHolders.get(i).edtCourtDate.getText().toString());
        }

        return param;
    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        if (v.getId() == R.id.btn_accept) {
            if (submit.getText().equals("Submit")) {
                if (amountReq.getText().toString().equalsIgnoreCase("")) {
                    Utils.showDialog(THIS, "Please Enter Bid Amount");
                } else {
                    // sentRequest();
                }
            }
            // if (!bm.isIsAccept()) {
            // sendAcceptanceToCompany(true);
            // }

        }

    }

    private void getDetail() {
        if (Utils.isOnline(DefendantBondDetails.this)) {

            if (getIntent().hasExtra("bail")) {
                bm = (BailRequestModel) getIntent()
                        .getSerializableExtra("bail");
                if (bm != null) {
                    reqId = "" + bm.getAgentRequestId();
                    // showDetail();
                } else {
                    Utils.showDialog(DefendantBondDetails.this,
                            "No detail found").show();
                }
            } else {
                reqId = PreferenceManager.getDefaultSharedPreferences(THIS)
                        .getString("id", "");
                fromNotification = getIntent().getBooleanExtra("notification",
                        false);
                Log.d("id", reqId + "");
                // bm = WebAccess.getBailRequestDetail(reqId);
                if (reqId != null && !reqId.equalsIgnoreCase(""))
                    bm = getBailRequestDetail();
            }

            DefendantBondDetails.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (bm != null) {
                        showDetail();
                    } else {
                        Utils.showDialog(DefendantBondDetails.this,
                                "No detail found").show();
                    }
                }
            });

        } else
            Utils.noInternetDialog(DefendantBondDetails.this);
    }

    public BailRequestModel getBailRequestDetail() {
        if (Utils.isOnline()) {
            showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("RequestId", reqId);
            param.put("UserName", MainActivity.user.getUsername());
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            String url = WebAccess.MAIN_URL + WebAccess.GET_REQUEST_DETAIL;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                    dismissProgressDialog();
                    Utils.showDialog(DefendantBondDetails.this,
                            R.string.err_unexpect);
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                    dismissProgressDialog();
                    try {
                        String response2;
                        response2 = new String(responseBody);
                        JSONObject resObj = new JSONObject(response2);
                        if (resObj != null) {
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {
                                JSONObject dObj;

                                dObj = resObj.getJSONObject("request_details");

                                mod = WebAccess.parseBailRequestDetail(dObj);
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
                                startActivity(new Intent(
                                        DefendantBondDetails.this,
                                        Launcher.class));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(DefendantBondDetails.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
            return mod;
        } else {
            Utils.noInternetDialog(THIS);

        }
        return null;
    }

    void sendAcceptanceToCompany(final boolean accept) {
        if (Utils.isOnline()) {
            showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("TransferBondId", reqId);
            param.put("UserName", MainActivity.user.getUsername());
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());

            param.put("LocationLongitude", bm.getLocationLatitude());
            param.put("LocationLatitude", bm.getLocationLongitude());

            String url = WebAccess.MAIN_URL + WebAccess.ACCEPT_BOND_REQUEST;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissProgressDialog();
                    Utils.showDialog(DefendantBondDetails.this,
                            R.string.err_unexpect);
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                    try {
                        String response2;
                        response2 = new String(responseBody);
                        JSONObject resObj = new JSONObject(response2);

                        if (resObj != null) {
                            message = resObj.optString("message");
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {
                                showDialog(DefendantBondDetails.this,
                                        message);
                                // getAllAgents(accept);
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
                                startActivity(new Intent(
                                        DefendantBondDetails.this,
                                        Launcher.class));
                            } else {
                                dismissProgressDialog();
                                Utils.showDialog(
                                        DefendantBondDetails.this,
                                        message);
                            }
                        }
                    } catch (JSONException e) {
                        dismissProgressDialog();
                        Utils.showDialog(DefendantBondDetails.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Utils.noInternetDialog(THIS);
        }
    }

    void sentRequest() {
        if (Utils.isOnline(this)) {

            showProgressDialog("");

            RequestParams param = new RequestParams();
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("RequestId", reqId);
            param.put("Amount", amountReq.getText().toString());
            param.put("Description", quotes.getText().toString());

            String url = WebAccess.MAIN_URL + WebAccess.GET_BID_ON_COMPANY;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissProgressDialog();
                    Utils.showDialog(DefendantBondDetails.this,
                            R.string.err_unexpect);
                }

                @SuppressWarnings("unused")
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    dismissProgressDialog();
                    String response2;
                    response2 = new String(responseBody);
                    if (response2 != null) {
                        JSONObject json;
                        try {
                            json = new JSONObject(response2);
                            if (json.optString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(
                                        DefendantBondDetails.this,
                                        json.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();

                            } else if (json.optString("status")
                                    .equalsIgnoreCase("3")) {
                                Toast.makeText(
                                        THIS,
                                        "Session was closed please login again",
                                        Toast.LENGTH_LONG).show();
                                MainActivity.sp.edit().putBoolean("isFbLogin",
                                        false);
                                MainActivity.sp.edit().putString("user", null)
                                        .commit();
                                startActivity(new Intent(
                                        DefendantBondDetails.this,
                                        Launcher.class));
                            } else
                                Utils.showDialog(
                                        DefendantBondDetails.this,
                                        json.optString("message"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else
                        Utils.showDialog(DefendantBondDetails.this,
                                R.string.err_unexpect);

                }

            });

        } else
            Utils.noInternetDialog(this);
    }

    public void getDropDownValues() {
        pd = ProgressDialog.show(DefendantBondDetails.this, "", "Loading.....");

        RequestParams param = new RequestParams();
        param.put("RequestId", reqId);
        param.put("TemporaryAccessCode",
                MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());


        client.setTimeout(getCallTimeout);
        String url = WebAccess.MAIN_URL + "get-prefixes";
        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Utils.showDialog(_activity, error.toString());
                pd.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                pd.dismiss();

                try {
                    String response2;
                    response2 = new String(responseBody);
                    JSONObject resObj;
                    resObj = new JSONObject(response2);
                    if (resObj != null) {
                        if (resObj.optString("status").equalsIgnoreCase("1")) {
                            dropDownValuesList = new ArrayList<>();
                            warrantIdList = new ArrayList<>();

                            JSONArray warrantArr = resObj.optJSONArray("warrantList");
                            preFixArr = resObj.optJSONArray("pre_fixes");

                            Log.d("PreFix", preFixArr.toString());

                            if (warrantArr != null) {

                                for (int wIndex = 0; wIndex < warrantArr.length(); wIndex++) {
                                    JSONObject warrantObj = warrantArr.getJSONObject(wIndex);

                                    if (selectedWarrentId == warrantObj.optInt("Id")) {
                                        WarrantStruct struct = new WarrantStruct();
                                        struct.id = warrantObj.optInt("Id");
                                        struct.title = warrantObj.optString("Township");
                                        warrantIdList.add(struct);
                                    }
                                }
                            }


                            //addDropDownViews();
                        } else if (resObj.optString("status").equalsIgnoreCase("3")) {
                            Toast.makeText(_activity, "Session was closed please login again", Toast.LENGTH_LONG).show();
                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(_activity);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove("login");// login
                            editor.commit();
                            startActivity(new Intent(_activity, Login.class));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(_activity, "Error occurs");
                    e.printStackTrace();
                }
            }
        });
    }

    public void addDropDownViews(WarrantModel wModel) {

        String prefix = "", serial = "";
        int selectedPreFix = 0;
        if (!selectedWarrentModel.getPowerNo().trim().equals("")) {
            String power[] = selectedWarrentModel.getPowerNo().trim().split("-");
            prefix = power[0];
            serial = power[1];
        }

        try {
            if (preFixArr != null) {
                dropDownValuesList.clear();
                if (dropDownValuesList.size() == 0) {
                    dropDownValuesList.add("Select Pre-fix");
                    for (int pIndex = 0; pIndex < preFixArr.length(); pIndex++) {

                        JSONObject preFixObj = preFixArr.getJSONObject(pIndex);
                        dropDownValuesList.add(preFixObj.optString("pre_fix"));
                        if (prefix.equals(preFixObj.optString("pre_fix"))) {
                            selectedPreFix = pIndex;
                        }
                    }

                }
            }
        } catch (Exception e) {

        }

        preFixesHolders = new ArrayList<>();

        View child = getLayoutInflater().inflate(R.layout.row_pre_fixes_warrant, null);
        PreFixesHolder holder = new PreFixesHolder();
        holder.spinner = (Spinner) child.findViewById(R.id.powerNumberSp);
        holder.spinnerStatus = (Spinner) child.findViewById(R.id.spinerStatus);

        holder.titleTV = (TextView) child.findViewById(R.id.titleTV);
        holder.serialET = (EditText) child.findViewById(R.id.serialET);
        holder.caseNoET = (EditText) child.findViewById(R.id.caseNoET);
        holder.edtAmout = (EditText) child.findViewById(R.id.edtAmout);
        holder.edtTownship = (EditText) child.findViewById(R.id.edtTownship);
        holder.edtNote = (EditText) child.findViewById(R.id.edtNote);

        holder.warrantID = wModel.getId();
        holder.titleTV.setText(wModel.getTownship());


        holder.caseNoET
                .setText(selectedWarrentModel.getCase_no());
        holder.edtTownship
                .setText(selectedWarrentModel.getTownship());
        holder.edtAmout
                .setText(selectedWarrentModel.getAmount());
        holder.edtNote
                .setText(selectedWarrentModel.getNotes());
        holder.serialET
                .setText(serial);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dropDownValuesList);
        holder.spinner.setAdapter(adapter);
        holder.spinner.setSelection(selectedPreFix + 1);

        ArrayList<String> status = new ArrayList<String>();
        status.add("Select Status");
        status.add("Active");
        status.add("Discharge");
        status.add("Forbidden");
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, status);
        holder.spinnerStatus.setAdapter(adapterStatus);
        holder.spinnerStatus.setSelection(1);
        if (selectedWarrentModel.getStatus().equals("D")) {
            holder.spinnerStatus.setSelection(2);
        } else if (selectedWarrentModel.getStatus().equals("F")) {
            holder.spinnerStatus.setSelection(3);
        }

        if (warrantCourtDates.size() > 0) {
            for (int i = 0; i < warrantCourtDates.size(); i++) {
                addCourtDateView(warrantCourtDates.get(i).getCourtDate());
            }
        } else {
            addCourtDateView("");
        }


        preFixedViewLL.addView(child);
        preFixesHolders.add(holder);

    }

    void addCourtDateView(String date) {
        View viewCourtdate = getLayoutInflater().inflate(R.layout.row_courtdates, null);
        final WarrantCourtDatesHolder warrantCourtDates = new WarrantCourtDatesHolder();

        warrantCourtDates.edtCourtDate = (EditText) viewCourtdate.findViewById(R.id.edtCourtDate);


        warrantCourtDates.edtCourtDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date convertedDate = new Date();
                try {
                    convertedDate = dateFormat.parse(warrantCourtDates.edtCourtDate.getText().toString());
                } catch (Exception e) {

                }
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {


                            @Override
                            public void onDateTimeSet(Date date) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String dateString = sdf.format(date);
                                warrantCourtDates.edtCourtDate.setText(dateString);

                            }

                            @Override
                            public void onDateTimeCancel() {
                                // Overriding onDateTimeCancel() is optional.
                            }
                        })
                        .setIs24HourTime(true)
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .setInitialDate(convertedDate)
                        .build()
                        .show();

                //   android.support.v4.app.DialogFragment newFragment = new DatePickerFragment();
                // newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        warrantCourtDates.edtCourtDate.setText(date);
        warrantCourtDatesHolders.add(warrantCourtDates);
        warrantCourtDatesLL.addView(viewCourtdate);

    }

    public AlertDialog showDialog(Context ctx, String msg)// ///hello
    {

        return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    void getAllAgents(final boolean accept) {
        if (Utils.isOnline()) {

            RequestParams param = new RequestParams();

            param.put("TransferBondId", reqId);
            param.put("UserName", MainActivity.user.getUsername());
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            final double[] loc = LocationImpl.getAvailableLocation();
            param.put("LocationLongitude", "" + loc[1] + "");
            param.put("LocationLatitude", "" + loc[0] + "");
            if (!MainActivity.user.getInsurance().isEmpty()) {
                ArrayList<String> insuranceList = MainActivity.user
                        .getInsurance();
                for (int i = 0; i < insuranceList.size(); i++) {
                    param.put("InsuranceId[" + i + "]", insuranceList.get(i)
                            + "");
                }
            }
            param.put("Page", "0");
            String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_AGENT;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissProgressDialog();
                    Utils.showDialog(DefendantBondDetails.this,
                            R.string.err_unexpect);
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                    dismissProgressDialog();
                    try {
                        String response2;
                        response2 = new String(responseBody);
                        JSONObject resObj = new JSONObject(response2);

                        if (resObj != null) {
                            message = resObj.optString("message");
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {

                                Toast.makeText(THIS, message, Toast.LENGTH_LONG)
                                        .show();
                                JSONArray jArray = resObj
                                        .getJSONArray("list_of_agents");
                                MainActivity.agentList = WebAccess
                                        .parseAgentList(jArray);
                                if (fromNotification) {
                                    Intent i = new Intent(
                                            DefendantBondDetails.this,
                                            MainActivity.class);
                                    i.putExtra("not", true);
                                    i.putExtra("bail", bm);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);

                                } else if (accept) {
                                    setResult(
                                            RESULT_OK,
                                            new Intent()
                                                    .putExtra("bail", bm)
                                                    .putExtra("locLatt",
                                                            "" + loc[0] + "")
                                                    .putExtra("locLng",
                                                            "" + loc[1] + ""));

                                } else {
                                    setResult(RESULT_OK);
                                }
                                finish();
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
                                startActivity(new Intent(
                                        DefendantBondDetails.this,
                                        Launcher.class));
                            } else {
                                Utils.showDialog(
                                        DefendantBondDetails.this,
                                        message);
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(DefendantBondDetails.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Utils.noInternetDialog(THIS);
        }
    }

    private void getCompanyDetail() {
        showProgressDialog("");
        RequestParams param = new RequestParams();

        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("companyid", bm.getSenderCompanyId());
        String url = WebAccess.MAIN_URL + WebAccess.GET_COMPANY_DETAIL;
        client.setTimeout(getCallTimeout);

        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
                Utils.showDialog(DefendantBondDetails.this,
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

                    if (resObj != null) {
                        if (resObj.optString("status").equalsIgnoreCase("1")) {
                            user = WebAccess.getCompanyResponse(response2);
                            startActivity(new Intent(THIS, CompanyProfile.class)
                                    .putExtra("user", user));
                        } else if (resObj.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(THIS,
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            MainActivity.sp.edit().putBoolean("isFbLogin",
                                    false);
                            MainActivity.sp.edit().putString("user", null)
                                    .commit();
                            startActivity(new Intent(
                                    DefendantBondDetails.this,
                                    Login.class));
                        } else {
                            Utils.showDialog(
                                    DefendantBondDetails.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(DefendantBondDetails.this,
                            R.string.err_unexpect);
                    e.printStackTrace();
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

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment implements
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
            //dateOfBirth.setText(date);
        }

        private String pad(int c) {
            return c >= 10 ? "" + c : "0" + c;
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

            bm.setImageBitmap(bmCompany);

            v.findViewById(R.id.popup_root).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });

            return v;
        }
    }

    public class WarrantStruct {
        int id;
        String title;
    }

    public class WarrantCourtDatesHolder {
        int id;
        int warrantId;
        EditText edtCourtDate;
    }


    public class PreFixesHolder {
        Spinner spinner;
        TextView titleTV;
        EditText serialET;
        EditText caseNoET;
        EditText edtAmout;
        EditText edtTownship;

        Spinner spinnerStatus;
        EditText edtNote;

        int warrantID;
    }

}
