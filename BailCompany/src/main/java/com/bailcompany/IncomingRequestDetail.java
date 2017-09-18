package com.bailcompany;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.custom.LocationImpl;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.CompanyBidingModel;
import com.bailcompany.model.IndemnitorModel;
import com.bailcompany.model.User;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.ui.MainFragment;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class IncomingRequestDetail extends CustomActivity {
    private BailRequestModel bm;
    String comp;
    String reqId;

    TextView submit, status, quotesText, amountReqText;
    ;
    boolean fromNotification = false;
    MainFragment m = new MainFragment();

    String currentDateTimeString, reqDateTimeString;
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    String message;
    JSONObject jsonObj;
    String key;
    static int getCallTimeout = 50000;
    int crnthour, reqthour;
    int crntminute, reqtminute;
    BailRequestModel mod = new BailRequestModel();
    static Bitmap bmCompany;
    ImageView imgCompany;
    LinearLayout layoutCompany;
    User user;
    LinearLayout payment;
    EditText quotes, amountReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bail_request_detail);

        submit = (TextView) findViewById(R.id.btn_accept);
        layoutCompany = (LinearLayout) findViewById(R.id.bail_container);
        amountReq = (EditText) findViewById(R.id.bid_decimal);
        quotes = (EditText) findViewById(R.id.quote_inst);
        status = (TextView) findViewById(R.id.acceptedtext);
        quotesText = (TextView) findViewById(R.id.quote_inst2);
        amountReqText = (TextView) findViewById(R.id.bid_decimal2);
        // loadContent();
        setActionBar();
        setTouchNClick(R.id.btn_accept);
        getDetail();
        imgCompany.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideKeyboard();
                F1.newInstance().show(getFragmentManager(), null);

            }
        });
        layoutCompany.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideKeyboard();
                getCompanyDetail();
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

    private void showDetail() {
        TextView t = (TextView) findViewById(R.id.temp);
        t.setText(bm.getSenderCompanyName() + "");
        ((TextView) findViewById(R.id.defName)).setText(bm.getDefendantName());

        ((TextView) findViewById(R.id.defDob)).setText("Date Of Birth : " + (bm.getDefDOB() != null ? Commons.changeDateFormat(bm.getDefDOB()) : "")
                + "");
        ((TextView) findViewById(R.id.defSSN)).setText("Social Security Number : " + (bm.getDefSSN() != null ? bm.getDefSSN() : "")
                + "");
        ((TextView) findViewById(R.id.defBookingNumber)).setText("Booking Number : " + (bm.getDefBookingNumber() != null ? bm.getDefBookingNumber() : "")
                + "");

        if (bm.getCompanyOfferToPay() != null)
            ((TextView) findViewById(R.id.payment_offer)).setText("$"
                    + bm.getCompanyOfferToPay() + "");
        ((TextView) findViewById(R.id.defAddr)).setText(Utils
                .getFormattedText(bm.getLocation()));
        ((TextView) findViewById(R.id.wrntReq)).setText(getWarrantReq());
        TextView defAddr = (TextView) findViewById(R.id.defAddr);
        defAddr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = Double.parseDouble(bm.getLocationLatitude());
                double longitude = Double.parseDouble(bm.getLocationLongitude());
                String label = Utils.getFormattedText(bm.getLocation());
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        LinearLayout llIndemn = (LinearLayout) findViewById(R.id.llIndemnitor);
        LinearLayout llWarrant = (LinearLayout) findViewById(R.id.llWarrant);

        ArrayList<WarrantModel> wList = bm.getWarrantList();
        if (wList != null && wList.size() > 0) {
            int count = 0;
            for (WarrantModel wMod : wList) {
                View v = getLayoutInflater()
                        .inflate(R.layout.row_warrant, null);
                if (count == 0)
                    v.findViewById(R.id.divider).setVisibility(View.GONE);
                if (wMod != null) {
                    ((TextView) v.findViewById(R.id.wrntAmount))
                            .setText("Amount:   $" + wMod.getAmount());
                    ((TextView) v.findViewById(R.id.wrntTownship))
                            .setText("Township:   " + wMod.getTownship() + "");

                    Log.e("wMod.getAmount()", "" + wMod.getAmount());
                    Log.e("wMod.getTownship()", "" + wMod.getTownship());

                    llWarrant.addView(v);
                }
                count++;
            }
        }

        ArrayList<IndemnitorModel> iList = bm.getIndemnitorsList();
        if (iList != null && iList.size() > 0) {
            int count = 0;
            for (IndemnitorModel iMod : iList) {
                View v = getLayoutInflater().inflate(R.layout.row_indemnitor,
                        null);
                if (count == 0)
                    v.findViewById(R.id.divider).setVisibility(View.GONE);
                if (iMod != null) {
                    ((TextView) v.findViewById(R.id.indemnName))
                            .setText("Name:   " + iMod.getName() + "");
                    TextView phoneTV = (TextView) v.findViewById(R.id.indemnPhone);
                    phoneTV.setText(iMod.getPhoneNumber());
                    phoneTV.setTag(iMod);

                    phoneTV.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IndemnitorModel iMod = (IndemnitorModel) v.getTag();

                            makeCallDialog(iMod.getPhoneNumber());
                        }
                    });

                    llIndemn.addView(v);
                }
                count++;
            }
        }

        // ArrayList<InsuranceModel> insList = bm.getInsuranceList();
        // String insur = "";
        // if (insList != null && insList.size() > 0) {
        // for (int i = 0; i < insList.size(); i++) {
        // if (i != 0)
        // insur += "\n";
        // insur += insList.get(i).getName();
        // }
        // }

        ((TextView) findViewById(R.id.insurance)).setText(bm
                .getBondInsuranceName() + "");

        ((TextView) findViewById(R.id.indemnNum)).setText(bm
                .getNumberIndemnitors() + "");
        ((TextView) findViewById(R.id.paperwork)).setText(getPaperworkReq());
        ((TextView) findViewById(R.id.paymentStatus))
                .setText(bm.isPaymentAlreadyReceived() ? "Payment Already Received by Company"
                        : "Payment to be collected from Indemnitor\nAmount:  $"
                        + bm.getAmountToCollect() + "\nPayment Plan:  "
                        + bm.getPaymentPlan());

        // @@@@@@@

        // String str = ((TextView) findViewById(R.id.paymentStatus)).getText()
        // .toString();

        ((TextView) findViewById(R.id.splInstruction)).setText(bm
                .getInstructionForAgent() + "");

        imgCompany = (ImageView) findViewById(R.id.img_company);
        Log.d("Company", bm.getCompanyImage() + "");
        Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
                StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
                bm.getSenderCompanyImage(), new ImageLoadedListener() {

                    @Override
                    public void imageLoaded(Bitmap bm) {
                        Log.d("Bitmap", bm == null ? "Null Bitmap"
                                : "Valid Bitmap");
                        if (bm != null) {
                            bmCompany = ImageUtils.getCircularBitmap(bm);
                            imgCompany.setImageBitmap(ImageUtils
                                    .getCircularBitmap(bm));
                        }
                    }
                });
        if (bmp != null)
            imgCompany.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
        else
            imgCompany.setImageBitmap(null);
        RelativeLayout enter = (RelativeLayout) findViewById(R.id.enter);
        RelativeLayout view = (RelativeLayout) findViewById(R.id.view);

        if (WebAccess.AllBidListCompany != null
                && !WebAccess.AllBidListCompany.isEmpty()) {
            if (getIntent().hasExtra("position")) {
                int i = getIntent().getIntExtra("position", 0);
                ArrayList<CompanyBidingModel> agentBidList = WebAccess.AllBidListCompany
                        .get(i);
                if (agentBidList != null && !agentBidList.isEmpty()) {
                    view.setVisibility(View.VISIBLE);
                    enter.setVisibility(View.GONE);
                    CompanyBidingModel companyBid = agentBidList.get(0);
                    amountReqText.setText(companyBid.getAmount());
                    quotesText.setText(companyBid.getDescription());
                    if (companyBid.getAcceptedStatus().equalsIgnoreCase("1")) {
                        status.setText("Your bid is accepted against this request");
                        submit.setText("Submitted");
                    } else {
                        status.setText("Your bid is not accepted yet against this request");
                        submit.setText("Submitted");
                    }

                } else {
                    enter.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    submit.setText("Submit");
                }

            } else {
                enter.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                submit.setText("Submit");
            }

        } else {
            enter.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            submit.setText("Submit");
        }

    }

    private void makeCallDialog(final String number) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("DO YOU WISH TO CALL? \n" + number);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(IncomingRequestDetail.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }

        });

        AlertDialog alertDialog = alert.show();
        TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        messageText.setTextColor(Color.BLACK);
    }

    private String getWarrantReq() {
        String req = "";

        if (bm.isNeedCourtFee())
            req += "Court Filing Fee Needed\n";

        if (bm.isNeedBailSource())
            req += "Bail Source Needed\n";

        if (bm.isIsCallAgency())
            req += "Call Agency";

        return req;
    }

    private String getPaperworkReq() {
        String req = "";

        if (!bm.isNeedPaperWork())
            req += "No Paperwork - Post & Go\n";
        // else
        // req+="Paperwork - Post & Go, ";

        if (bm.isNeedIndemnitorPaperwork())
            req += "Indemnitor Paperwork\n";

        if (bm.isNeedDefendantPaperwork())
            req += "Defendant Paperwork";

        return req;
    }

    protected void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.bail_req_detail));
    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        if (v.getId() == R.id.btn_accept) {
            if (submit.getText().equals("Submit")) {
                if (amountReq.getText().toString().equalsIgnoreCase("")) {
                    Utils.showDialog(THIS, "Please Enter Bid Amount");
                } else {
                    sentRequest();
                }
            }
            // if (!bm.isIsAccept()) {
            // sendAcceptanceToCompany(true);
            // }

        }

    }

    private void getDetail() {
        if (Utils.isOnline(IncomingRequestDetail.this)) {

            if (getIntent().hasExtra("bail")) {
                bm = (BailRequestModel) getIntent()
                        .getSerializableExtra("bail");
                if (bm != null) {
                    reqId = "" + bm.getAgentRequestId();
                    // showDetail();
                } else {
                    Utils.showDialog(IncomingRequestDetail.this,
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

            IncomingRequestDetail.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (bm != null) {
                        showDetail();
                    } else {
                        Utils.showDialog(IncomingRequestDetail.this,
                                "No detail found").show();
                    }
                }
            });

        } else
            Utils.noInternetDialog(IncomingRequestDetail.this);
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
                    Utils.showDialog(IncomingRequestDetail.this,
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
                                Toast.makeText(IncomingRequestDetail.this,
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
                                        IncomingRequestDetail.this, Launcher.class));
                            } else
                                Utils.showDialog(IncomingRequestDetail.this,
                                        json.optString("message"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else
                        Utils.showDialog(IncomingRequestDetail.this,
                                R.string.err_unexpect);

                }

            });

        } else
            Utils.noInternetDialog(this);
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
                    Utils.showDialog(IncomingRequestDetail.this,
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
                                        IncomingRequestDetail.this, Launcher.class));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(IncomingRequestDetail.this,
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
                    Utils.showDialog(IncomingRequestDetail.this,
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
                                showDialog(IncomingRequestDetail.this, message);

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
                                        IncomingRequestDetail.this, Launcher.class));
                            } else {
                                dismissProgressDialog();
                                Utils.showDialog(IncomingRequestDetail.this,
                                        message);
                            }
                        }
                    } catch (JSONException e) {
                        dismissProgressDialog();
                        Utils.showDialog(IncomingRequestDetail.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Utils.noInternetDialog(THIS);
        }
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

    void getAllAgents(final boolean accept) {
        if (Utils.isOnline()) {

            RequestParams param = new RequestParams();
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
            String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_AGENT;
            param.put("Page", "0");
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                    dismissProgressDialog();
                    Utils.showDialog(IncomingRequestDetail.this,
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

                                JSONArray jArray = resObj
                                        .getJSONArray("list_of_agents");
                                MainActivity.agentList = WebAccess
                                        .parseAgentList(jArray);
                                if (fromNotification) {
                                    Intent i = new Intent(
                                            IncomingRequestDetail.this,
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
                                        IncomingRequestDetail.this, Launcher.class));
                            } else {
                                Utils.showDialog(IncomingRequestDetail.this,
                                        message);
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(IncomingRequestDetail.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Utils.noInternetDialog(THIS);
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
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });

            return v;
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
                Utils.showDialog(IncomingRequestDetail.this,
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
                                    IncomingRequestDetail.this, Login.class));
                        } else {
                            Utils.showDialog(IncomingRequestDetail.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(IncomingRequestDetail.this,
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

}
