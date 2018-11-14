package com.bailcompany;

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
import android.graphics.PorterDuff;
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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.AgentModel;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.IndemnitorModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.User;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.ui.Defendant;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class HistoryRequestDetail extends CustomActivity {
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;
    static Bitmap bmCompany;
    String comp;
    String reqId;
    TextView submit, bail_request, defName;
    boolean fromNotification = false;
    MainFragment m = new MainFragment();
    String currentDateTimeString, reqDateTimeString;
    String message, header = "Bail Company Detail", status = "Accepted",
            titleDetail = "Bail Request Detail";
    JSONObject jsonObj;
    String key;
    int crnthour, reqthour;
    int crntminute, reqtminute;
    BailRequestModel mod = new BailRequestModel();
    ImageView imgCompany;
    LinearLayout layoutCompany;
    User user;
    LinearLayout payment, powerStParent;
    Button hireAgent, selfAssign;
    boolean isAccepted;
    boolean isCompanyRequest;
    RelativeLayout rlStep1, rlStep2, rlStep3, rlStep4;
    ImageView image_dispatch, image_accepted, image_arrival, image_completion;
    TextView tv_completion, tv_dispatch, tv_accepted, tv_arrival;
    LinearLayout requestProgress;
    private BailRequestModel bm;
    private ArrayList<AgentModel> agentList;
    private ArrayList<String> selectedItems = new ArrayList<String>();

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
        setContentView(R.layout.activity_history_request_detail);
        payment = (LinearLayout) findViewById(R.id.payment);
        hireAgent = (Button) findViewById(R.id.hire);
        selfAssign = (Button) findViewById(R.id.self_assign);
        String title = getIntent().getStringExtra("title");
        layoutCompany = (LinearLayout) findViewById(R.id.sender_company);
        powerStParent = (LinearLayout) findViewById(R.id.power_status);
        if (title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
            header = "Sender Bail Company Detail";
            titleDetail = "My Sent Transfer Bond Request Detail";
            status = "Pending";

        } else if (title.equalsIgnoreCase("My Accepted Transfer Bond Requests")) {
            header = "Accepter Bail Company Detail";
            titleDetail = "My Accepted Transfer Bond Request Detail";
            isAccepted = true;
        } else if (title.equalsIgnoreCase("All Posting Agent Transactions")) {
            header = "Sender Bail Company Detail";
            titleDetail = "Posting Agent Transactions Detail";
            status = "Pending";
            payment.setVisibility(View.GONE);
            isAccepted = true;
            isCompanyRequest = true;


        }
        initStepLayout();
        setActionBar(titleDetail);
        getDetail();
        defName = (TextView) findViewById(R.id.defName);
        defName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (bm.getDefId() == null || bm.getDefId().equals(""))
                    return;
                Intent intent = new Intent(HistoryRequestDetail.this, Defendant.class);
                intent.putExtra("defId", bm.getDefId());
                startActivity(intent);

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
                if (header.equalsIgnoreCase("Agent Detail"))
                    getAgentDetail();
                else
                    getCompanyDetail();
            }
        });
        hireAgent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideKeyboard();
                WebAccess.hireBailAgent = true;
                getAgentList();

                // getAllAgents();
            }
        });
        selfAssign.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideKeyboard();
                WebAccess.selfAssignAgent = true;
                submitSelfRequest();

            }
        });
    }

    void initStepLayout() {

        rlStep1 = (RelativeLayout) findViewById(R.id.rlStep1);
        rlStep2 = (RelativeLayout) findViewById(R.id.rlStep2);
        rlStep3 = (RelativeLayout) findViewById(R.id.rlStep3);
        rlStep4 = (RelativeLayout) findViewById(R.id.rlStep4);
        requestProgress = (LinearLayout) findViewById(R.id.requestProgress);


        image_dispatch = (ImageView) findViewById(R.id.image_dispatch);
        image_accepted = (ImageView) findViewById(R.id.image_accepted);
        image_arrival = (ImageView) findViewById(R.id.image_arrival);
        image_completion = (ImageView) findViewById(R.id.image_completion);

        tv_completion = (TextView) findViewById(R.id.tv_completion);
        tv_dispatch = (TextView) findViewById(R.id.tv_dispatch);
        tv_accepted = (TextView) findViewById(R.id.tv_accepted);
        tv_arrival = (TextView) findViewById(R.id.tv_arrival);


    }

    void setupSteps(BailRequestModel requestdetail) {
        try {
            if (requestdetail.getIsComplete().equals("1")) {
                if (requestdetail.getRequestCompletionTime().equals("")) {
                    requestProgress.setVisibility(View.GONE);
                    return;
                }
            }

            tv_dispatch.setTextColor(getResources().getColor(R.color.grey_70));
            tv_accepted.setTextColor(getResources().getColor(R.color.grey_10));
            tv_arrival.setTextColor(getResources().getColor(R.color.grey_10));
            tv_completion.setTextColor(getResources().getColor(R.color.grey_10));
            image_accepted.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
            image_dispatch.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            image_arrival.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
            image_completion.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
            requestProgress.setVisibility(View.VISIBLE);
            tv_accepted.setText("Accepted");
            tv_completion.setText("Completed");


            rlStep2.setVisibility(View.VISIBLE);
            rlStep3.setVisibility(View.VISIBLE);
            rlStep4.setVisibility(View.VISIBLE);

            if (!requestdetail.getCreatedDate().equals("")) {

                tv_dispatch.setText(tv_dispatch.getText().toString() + "\n" + Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                        "MM/dd/yyyy hh:mm", requestdetail.getCreatedDate()));
            }


            if (requestdetail.isIsAccept().equals("1")) {
                image_accepted.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                tv_accepted.setTextColor(getResources().getColor(R.color.grey_70));
                tv_accepted.setText(tv_accepted.getText().toString() + "\n" + Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                        "MM/dd/yyyy hh:mm", requestdetail.getAgentAcceptedTime()));
            }
            if (!requestdetail.getAgentArrivedTime().equals("")) {

                if (requestdetail.getAgentAcceptedTime().equals("")) {
                    requestProgress.setVisibility(View.GONE);
                    return;
                }
                image_arrival.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                tv_arrival.setTextColor(getResources().getColor(R.color.grey_70));
                tv_arrival.setText(tv_arrival.getText().toString() + "\n" + Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                        "MM/dd/yyyy hh:mm", requestdetail.getAgentArrivedTime()));


            }
            if (requestdetail.getIsComplete().equals("1")) {
                if (requestdetail.getRequestCompletionTime().equals("")) {
                    requestProgress.setVisibility(View.GONE);
                    return;
                }


                if (requestdetail.getAgentAcceptedTime().equals("")) {
                    requestProgress.setVisibility(View.GONE);
                    return;

                }
                if (requestdetail.getAgentArrivedTime().equals("")) {
                    requestProgress.setVisibility(View.GONE);
                    return;

                }
                image_completion.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                tv_completion.setTextColor(getResources().getColor(R.color.grey_70));
                tv_completion.setText(tv_completion.getText().toString() + "\n" + Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                        "MM/dd/yyyy hh:mm", requestdetail.getRequestCompletionTime()));


            }


            if (requestdetail.getAgentId().equals(requestdetail.getSenderCompanyId())) {
                image_accepted.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                tv_accepted.setTextColor(getResources().getColor(R.color.grey_70));
                tv_accepted.setText("Self Assigned");

            }

            if (requestdetail.getIsAbort().equals("1")) {
                // aborted
                if (requestdetail.getAgentArrivedTime().equals("")) {
                    requestProgress.setVisibility(View.GONE);

                    return;
                }
                if (requestdetail.getIsCancel().equals("1")) {
                    //cancelled
                    image_completion.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    tv_completion.setTextColor(getResources().getColor(R.color.grey_70));
                    tv_completion.setText("Cancelled");

                } else {
                    image_completion.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    tv_completion.setTextColor(getResources().getColor(R.color.grey_70));
                    tv_completion.setText("Aborted");

                }

                tv_completion.setText(tv_completion.getText().toString() + "\n" + Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                        "MM/dd/yyyy hh:mm", requestdetail.getRequestAbortedTime()));

            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        hideKeyboard();
    }

    private void showDetail() {

        if (isAccepted) {
            hireAgent.setVisibility(View.VISIBLE);
            selfAssign.setVisibility(View.VISIBLE);
            if (bm.getIsComplete() != null
                    && bm.getIsComplete().equalsIgnoreCase("1")) {
                status = "Completed";
                hireAgent.setVisibility(View.GONE);
                selfAssign.setVisibility(View.GONE);
                powerStParent.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.power_no)).setText(bm
                        .getPowerNo() + "");
                ((TextView) findViewById(R.id.amount_recieved)).setText("$"
                        + bm.getAmountReceived() + "");
                ((TextView) findViewById(R.id.comments)).setText(bm
                        .getComments() + "");
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
                    hireAgent.setVisibility(View.GONE);
                    selfAssign.setVisibility(View.GONE);
                } else if (bm.isIsAccept().equalsIgnoreCase("0")) {
                    status = "Declined";
                    hireAgent.setVisibility(View.VISIBLE);
                    selfAssign.setVisibility(View.VISIBLE);
                } else {
                    status = "Pending";
                }
            }

            if (!bm.getSelfAssigned().equalsIgnoreCase("")) {
                if (status.equalsIgnoreCase("Pending"))
                    status = "Self Assigned";
                titleDetail = "Company Transactions Detail";
                setActionBar(titleDetail);
                ((TextView) findViewById(R.id.btn_accept))
                        .setBackgroundColor(Color.parseColor("#fa9523"));

            }
        }

        if (status.equalsIgnoreCase("Pending")) {

            if (!titleDetail
                    .equalsIgnoreCase("Posting Agent Transactions Detail")) {
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
                header = "Agent Detail";
                ((TextView) findViewById(R.id.temp)).setText(bm.getAgentName()
                        + "");
            }
        } else if (!bm.getSelfAssigned().equalsIgnoreCase("")) {
            if (titleDetail.equalsIgnoreCase("Company Transactions Detail")) {
                header = "Company Detail";
                ((TextView) findViewById(R.id.temp)).setText(bm
                        .getSenderCompanyName() + "");
            }
        } else {
            if (titleDetail
                    .equalsIgnoreCase("Posting Agent Transactions Detail")) {
                header = "Agent Detail";
                ((TextView) findViewById(R.id.temp)).setText(bm.getAgentName()
                        + "");
            }
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
        ((TextView) findViewById(R.id.btn_accept)).setText(status);
        ((TextView) findViewById(R.id.defAddr)).setText(Utils
                .getFormattedText(bm.getLocation()));
        ((TextView) findViewById(R.id.wrntReq2)).setText(getWarrantReq());

        LinearLayout llIndemn = (LinearLayout) findViewById(R.id.llIndemnitor);
        LinearLayout llWarrant = (LinearLayout) findViewById(R.id.llWarrant);
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
                    if (wMod.getAmountReceived() != null && !wMod.getAmountReceived().equalsIgnoreCase("")) {
                        ((TextView) v.findViewById(R.id.wrntAmountReceived))
                                .setText("Amount Received:   $" + wMod.getAmountReceived());
                    }


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

        ArrayList<InsuranceModel> insList = bm.getInsuranceList();
        String insur = "";
        if (insList != null && insList.size() > 0) {
            for (int i = 0; i < insList.size(); i++) {
                if (i != 0)
                    insur += "\n";
                insur += insList.get(i).getName();
                selectedItems.add(insList.get(i).getId() + "");
            }
        }

        ((TextView) findViewById(R.id.insurance)).setText(insur + "");

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
        if (!status.equalsIgnoreCase("Pending")
                || hireAgent.getVisibility() == View.GONE) {
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
                if (ActivityCompat.checkSelfPermission(HistoryRequestDetail.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        if (Utils.isOnline(HistoryRequestDetail.this)) {

            if (getIntent().hasExtra("bail")) {
                bm = (BailRequestModel) getIntent()
                        .getSerializableExtra("bail");
                if (bm != null) {
                    reqId = "" + bm.getAgentRequestId();
                    // showDetail();
                } else {
                    Utils.showDialog(HistoryRequestDetail.this,
                            "No detail found").show();
                }
            }

            HistoryRequestDetail.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (bm != null) {
                        showDetail();
                        setupSteps(bm);
                    } else {
                        Utils.showDialog(HistoryRequestDetail.this,
                                "No detail found").show();
                    }
                }
            });

        } else
            Utils.noInternetDialog(HistoryRequestDetail.this);
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
                Utils.showDialog(HistoryRequestDetail.this,
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
                            startActivity(new Intent(HistoryRequestDetail.this,
                                    Login.class));
                        } else {
                            Utils.showDialog(HistoryRequestDetail.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(HistoryRequestDetail.this,
                            R.string.err_unexpect);
                    e.printStackTrace();
                }

            }

        });

    }

    private void getAgentDetail() {
        showProgressDialog("");
        RequestParams param = new RequestParams();

        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("agentid", bm.getAgentId());
        String url = WebAccess.MAIN_URL + WebAccess.GET_AGENT_DETAIL;
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
                Utils.showDialog(HistoryRequestDetail.this,
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
                            AgentModel model = WebAccess.getAgent(response2);
                            if (model != null)
                                startActivity(new Intent(THIS,
                                        AgentProfile.class).putExtra("agent",
                                        model));
                        } else if (resObj.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(THIS,
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            MainActivity.sp.edit().putBoolean("isFbLogin",
                                    false);
                            MainActivity.sp.edit().putString("user", null)
                                    .commit();
                            startActivity(new Intent(HistoryRequestDetail.this,
                                    Login.class));
                        } else {
                            Utils.showDialog(HistoryRequestDetail.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(HistoryRequestDetail.this,
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
                    Utils.showDialog(HistoryRequestDetail.this,
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
                                            HistoryRequestDetail.this,
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
                                    Utils.showDialog(HistoryRequestDetail.this,
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
                                        HistoryRequestDetail.this,
                                        Launcher.class));
                            } else {
                                Utils.showDialog(HistoryRequestDetail.this,
                                        message);
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(HistoryRequestDetail.this,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });
        } else {
            Utils.noInternetDialog(THIS);
        }
    }

    private void getAgentList() {

        RequestParams param = new RequestParams();
        showProgressDialog("");
        param.put("CompanyId", MainActivity.user.getCompanyId());
        param.put("Page", "0");
        param.put("LocationLongitude", "" + bm.getLocationLongitude() + "");
        param.put("LocationLatitude", "" + bm.getLocationLatitude() + "");
        param.put("UserName", MainActivity.user.getUsername());
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        for (int i = 0; i < bm.getInsuranceList().size(); i++) {
            for (InsuranceModel mod : bm.getInsuranceList()) {
                if (selectedItems.get(i).equals(mod.getId() + "")) {
                    param.put("InsuranceId[" + i + "]", mod.getId() + "");
                    break;
                }
            }

        }
        String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_AGENT;
        client.setTimeout(getCallTimeout);
        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                dismissProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                dismissProgressDialog();
                try {
                    String response2 = new String(responseBody);
                    if (response2 != null) {
                        JSONObject json = new JSONObject(response2);
                        if (json.optString("status").equalsIgnoreCase("1")) {
                            WebAccess.agentRequestId = bm.getAgentRequestId()
                                    + "";
                            JSONArray jArray = json
                                    .getJSONArray("list_of_agents");
                            agentList = WebAccess.parseAgentList(jArray);
                            if (agentList != null && agentList.size() > 0) {
                                startActivity(new Intent(
                                        HistoryRequestDetail.this,
                                        FindBestAgent.class)
                                        .putExtra("agents", agentList)
                                        .putExtra("reqid",
                                                WebAccess.agentRequestId)
                                        .putExtra(
                                                "locLatt",
                                                "" + bm.getLocationLatitude()
                                                        + "")
                                        .putExtra(
                                                "locLng",
                                                "" + bm.getLocationLongitude()
                                                        + ""));
                                finish();
                            } else {
                                Utils.showDialog(HistoryRequestDetail.this,
                                        "No Agent Found Near this location");
                                dismissProgressDialog();
                            }
                        } else if (json.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(THIS,
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            MainActivity.sp.edit().putBoolean("isFbLogin",
                                    false);
                            MainActivity.sp.edit().putString("user", null)
                                    .commit();
                            startActivity(new Intent(HistoryRequestDetail.this,
                                    Login.class));
                        } else {
                            Utils.showDialog(HistoryRequestDetail.this,
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

    void submitSelfRequest() {

        showProgressDialog("");
        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("RequestId", bm.getAgentRequestId());
        String url = WebAccess.MAIN_URL + WebAccess.SELF_REQUEST;
        client.setTimeout(getCallTimeout);
        client.post(HistoryRequestDetail.THIS, url, param,
                new AsyncHttpResponseHandler() {
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
                                if (json.optString("status").equalsIgnoreCase(
                                        "1")) {
                                    message = json.optString("message");

                                    showDialog(HistoryRequestDetail.THIS,
                                            json.optString("message"));

                                } else if (json.optString("status")
                                        .equalsIgnoreCase("3")) {
                                    Toast.makeText(
                                            HistoryRequestDetail.THIS,
                                            "Session was closed please login again",
                                            Toast.LENGTH_LONG).show();
                                    MainActivity.sp.edit().putBoolean(
                                            "isFbLogin", false);
                                    MainActivity.sp.edit()
                                            .putString("user", null).commit();
                                    startActivity(new Intent(
                                            HistoryRequestDetail.THIS,
                                            Login.class));
                                } else {
                                    Utils.showDialog(HistoryRequestDetail.THIS,
                                            json.optString("message"));
                                }
                            }
                        } catch (JSONException e) {

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
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
}
