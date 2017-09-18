package com.bailcompany;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.IndemnitorModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.utils.Utils;

@SuppressLint("InflateParams")
public class SelfAssignedDetailActivity extends CustomActivity {
    private BailRequestModel bm;
    private TextView btnArrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assigned_detail);
        setActionBar("Self Assigned Transaction Detail");
        btnArrive = (TextView) findViewById(R.id.btn_arrive);
        getDetail();
        btnArrive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                startActivity(new Intent(SelfAssignedDetailActivity.this,
                        CompletionForum.class).putExtra("bail", bm));
                finish();
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

    protected void setActionBar(String title) {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(title);
    }

    private void getDetail() {
        if (Utils.isOnline(SelfAssignedDetailActivity.this)) {

            if (getIntent().hasExtra("bail")) {
                bm = (BailRequestModel) getIntent()
                        .getSerializableExtra("bail");
                if (bm != null) {

                    showDetail();
                } else {
                    Utils.showDialog(SelfAssignedDetailActivity.this,
                            "No detail found").show();
                }
            }

        } else
            Utils.noInternetDialog(SelfAssignedDetailActivity.this);
    }

    private void showDetail() {

        ((TextView) findViewById(R.id.defName)).setText(bm.getDefendantName()
                + "");
        if (bm.getCompanyOfferToPay() != null)
            ((TextView) findViewById(R.id.payment_offer)).setText("$"
                    + bm.getCompanyOfferToPay() + "");

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

        defAddr.setText(Utils.getFormattedText(bm.getLocation()));


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
        ((TextView) findViewById(R.id.paperwork)).setText(getPaperworkReq());
        ((TextView) findViewById(R.id.paymentStatus))
                .setText(bm.isPaymentAlreadyReceived() ? "Payment Already Received by Company"
                        : "Payment to be collected from Indemnitor\nAmount:  $"
                        + bm.getAmountToCollect() + "\nPayment Plan:  "
                        + bm.getPaymentPlan());

        ((TextView) findViewById(R.id.splInstruction)).setText(bm
                .getInstructionForAgent() + "");
    }

    private void makeCallDialog(final String number) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("DO YOU WISH TO CALL? \n" + number);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(SelfAssignedDetailActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
