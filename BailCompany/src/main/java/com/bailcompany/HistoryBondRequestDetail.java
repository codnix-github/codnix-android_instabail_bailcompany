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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BailRequestModel;
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

public class HistoryBondRequestDetail extends CustomActivity {
    private BailRequestModel bm;
    String comp;
    String reqId;

    TextView submit, bail_request, bid2;
    boolean fromNotification = false;
    MainFragment m = new MainFragment();

    String currentDateTimeString, reqDateTimeString;
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    String message, header = "Bail Company Detail", status = "Accepted",
            titleDetail = "Bail Request Detail";
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

    Button hireAgent, hireAnOtherAgent;
    boolean isAccepted;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_bond_request_detail);

        hireAgent = (Button) findViewById(R.id.hire);
        hireAnOtherAgent = (Button) findViewById(R.id.hireAnOtherAgent);
        String title = getIntent().getStringExtra("title");
        layoutCompany = (LinearLayout) findViewById(R.id.sender_company);
        bid2 = (TextView) findViewById(R.id.btnBiding);
        if (title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
            header = "Sender Bail Company Detail";
            titleDetail = "My Sent Transfer Bond Request Detail";
            status = "Pending";

        } else if (title.equalsIgnoreCase("My Accepted Transfer Bond Requests")) {
            header = "Accepted Bail Company Detail";
            titleDetail = "My Accepted Transfer Bond Request Detail";
            isAccepted = true;
        }
        setActionBar(titleDetail);
        getDetail();
        bid2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideKeyboard();
                if (bm != null) {
                    String AgentId = bm.getAgentId();
                    String accept = "0";
                    if (!AgentId.equalsIgnoreCase("")) {
                        accept = "1";
                    }
                    Intent intent = new Intent(HistoryBondRequestDetail.this,
                            BidingBailActivity.class);
                    intent.putExtra("Accepted", accept);
                    intent.putExtra("req", bm);
                    intent.putExtra("position", pos);
                    startActivity(intent);

                }
            }
        });
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
        hireAgent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideKeyboard();
                WebAccess.hireTransferBondAgent = true;
                WebAccess.agentRecord = bm;
                WebAccess.agentRequestId = bm.getAgentRequestId() + "";
                finish();
                // getAllAgents();
            }
        });
        hireAnOtherAgent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                hideKeyboard();
                WebAccess.hireBailAgent = true;
//				getAgentList();
                getAllAgents();

//				finish();

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

    @SuppressLint("InflateParams")
    private void showDetail() {
        WebAccess.agentRequestId = bm.getAgentRequestId() + "";
        if (isAccepted) {

            if (bm.getAgentId().equalsIgnoreCase("") || bm.getAgentId() == null) {
                hireAgent.setVisibility(View.VISIBLE);
                hireAnOtherAgent.setVisibility(View.GONE);
            } else {
                hireAgent.setVisibility(View.GONE);
                hireAnOtherAgent.setVisibility(View.VISIBLE);
                if (bm.getIsComplete() != null
                        && bm.getIsComplete().equalsIgnoreCase("1")) {
                    status = "Completed";
                } else if (bm.getIsAbort() != null
                        && bm.getIsAbort().equalsIgnoreCase("1")) {
                    status = "Aborted";
                    if (bm.getIsCancel() != null
                            && bm.getIsCancel().equalsIgnoreCase("1")) {

                        status = "Canceled";
                    }

                } else {
                    if (bm.isIsAccept().equalsIgnoreCase("1")) {
                        status = "In Progress";
                    } else if (bm.isIsAccept().equalsIgnoreCase("0")) {
                        status = "Declined";
                        hireAgent.setVisibility(View.VISIBLE);
                        hireAnOtherAgent.setVisibility(View.GONE);
                    } else {
                        status = "Pending";
                    }
                }
            }
            ((TextView) findViewById(R.id.btn_accept)).setText(status);
            ((RelativeLayout) findViewById(R.id.parent_biding))
                    .setVisibility(View.GONE);
        } else {
            ((RelativeLayout) findViewById(R.id.parent_biding))
                    .setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
        }
        if (status.equalsIgnoreCase("Pending")) {

            if (!(bm.getSenderCompanyId().equalsIgnoreCase(""))
                    && bm.getSenderCompanyId() != null) {
                status = "Accepted";
                if (isAccepted)
                    header = "Sender Company Detail";
                else
                    header = "Receiver Company Detail";
                ((TextView) findViewById(R.id.temp)).setText(bm
                        .getSenderCompanyName() + "");
            }

        } else {
            ((TextView) findViewById(R.id.temp)).setText(bm
                    .getSenderCompanyName() + "");
        }
        ((TextView) findViewById(R.id.defName)).setText(bm.getDefendantName()
                + "");

        ((TextView) findViewById(R.id.defDob)).setText("Date Of Birth : " + (bm.getDefDOB() != null ? Commons.changeDateFormat(bm.getDefDOB()) : "")
                + "");
        ((TextView) findViewById(R.id.defSSN)).setText("Social Security Number : " + (bm.getDefSSN() != null ? bm.getDefSSN() : "")
                + "");
        ((TextView) findViewById(R.id.defBookingNumber)).setText("Booking Number : " + (bm.getDefBookingNumber() != null ? bm.getDefBookingNumber() : "")
                + "");

        if (bm.getCompanyOfferToPay() != null)
            ((TextView) findViewById(R.id.payment_offer)).setText("$"
                    + bm.getCompanyOfferToPay() + "");

        ((TextView) findViewById(R.id.company_header)).setText(header);

        ((TextView) findViewById(R.id.defAddr)).setText(Utils
                .getFormattedText(bm.getLocation()));
        ((TextView) findViewById(R.id.wrntReq2)).setText(getWarrantReq());
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
                    ((TextView) v.findViewById(R.id.wrntCaseNum))
                            .setText("Case No:   " + wMod.getCase_no() + "");
                    ((TextView) v.findViewById(R.id.wrntPowerNum))
                            .setText("Power No:   " + wMod.getPowerNo() + "");
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
        //
        // ((TextView) findViewById(R.id.insurance)).setText(insur + "");
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

        ((TextView) findViewById(R.id.splInstruction)).setText(bm
                .getInstructionForAgent() + "");

        imgCompany = (ImageView) findViewById(R.id.img_company);
        Log.d("Company", bm.getCompanyImage() + "");
        if (!status.equalsIgnoreCase("Pending")) {
            String image = "";
            if (titleDetail
                    .equalsIgnoreCase("Posting Agent Transactions Detail")) {
                image = bm.getAgentImage();
            } else {
                image = bm.getSenderCompanyImage();
            }
            Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
                    StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
                    image, new ImageLoadedListener() {

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
        } else {
            LinearLayout senderLayout = (LinearLayout) findViewById(R.id.sender_company);
            senderLayout.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.company_header))
                    .setVisibility(View.GONE);
        }
    }

    private void makeCallDialog(final String number) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("DO YOU WISH TO CALL? \n" + number);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(HistoryBondRequestDetail.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

    protected void setActionBar(String title) {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(title);
    }

    private void getDetail() {
        if (Utils.isOnline(HistoryBondRequestDetail.this)) {

            if (getIntent().hasExtra("bail")) {
                bm = (BailRequestModel) getIntent()
                        .getSerializableExtra("bail");
                pos = getIntent().getIntExtra("position", 0);
                if (bm != null) {
                    reqId = "" + bm.getAgentRequestId();
                    // showDetail();
                } else {
                    Utils.showDialog(HistoryBondRequestDetail.this,
                            "No detail found").show();
                }
            }

            HistoryBondRequestDetail.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (bm != null) {
                        showDetail();
                    } else {
                        Utils.showDialog(HistoryBondRequestDetail.this,
                                "No detail found").show();
                    }
                }
            });

        } else
            Utils.noInternetDialog(HistoryBondRequestDetail.this);
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
                Utils.showDialog(HistoryBondRequestDetail.this,
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
                                    HistoryBondRequestDetail.this, Login.class));
                        } else {
                            Utils.showDialog(HistoryBondRequestDetail.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(HistoryBondRequestDetail.this,
                            R.string.err_unexpect);
                    e.printStackTrace();
                }

            }

        });

    }

    void getAllAgents() {
        if (Utils.isOnline()) {
            showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("CompanyId", MainActivity.user.getCompanyId());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("LocationLongitude", bm.getLocationLongitude());
            param.put("LocationLatitude", bm.getLocationLatitude());
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
                    Utils.showDialog(HistoryBondRequestDetail.this,
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
                                WebAccess.agentRequestId = reqId;
                                if (MainActivity.agentList != null
                                        && MainActivity.agentList.size() > 0) {
                                    startActivity(new Intent(
                                            HistoryBondRequestDetail.this,
                                            FindBestAgent.class)
                                            .putExtra("agents",
                                                    MainActivity.agentList)
                                            .putExtra("reqid",
                                                    WebAccess.agentRequestId)
                                            .putExtra("locLatt",
                                                    bm.getLocationLatitude())
                                            .putExtra("locLng",
                                                    bm.getLocationLongitude()));

                                } else {
                                    Utils.showDialog(
                                            HistoryBondRequestDetail.this,
                                            "No Agent Found Near this location");
                                    dismissProgressDialog();
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
                                startActivity(new Intent(
                                        HistoryBondRequestDetail.this,
                                        Launcher.class));
                            } else {
                                Utils.showDialog(HistoryBondRequestDetail.this,
                                        message);
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(HistoryBondRequestDetail.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Utils.noInternetDialog(THIS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
