package com.bailcompany.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bailcompany.AgentProfile;
import com.bailcompany.CompletionForum;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.InsuranceModel;

public class PaymentReceiptPopup extends CustomActivity {

    TextView txtDate, txtTime, txtName, txtLicense, txtServiceType, txtPowerNo,
            txtInsComp, txtCompName, txtReceiptNum, txtInsamt, receiptId,
            postName;
    ImageButton ibCardType;
    TextView pwrNo, officerName;

    BailRequestModel reqDetail;// bm
    AgentProfile ag = new AgentProfile();

    static String temp1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.payment_receipt);
        pwrNo = (TextView) findViewById(R.id.pwr_no);
        officerName = (TextView) findViewById(R.id.office);
        receiptId = (TextView) findViewById(R.id.receipt);

        Log.e("temp1", "temp1=" + temp1);

        Intent i = getIntent();
        reqDetail = (BailRequestModel) i.getSerializableExtra("bail");
        temp1 = i.getStringExtra("pay");
//		String pwrNum = i.getStringExtra("power");

        String str = reqDetail.isPaymentAlreadyReceived() ? "Already Paid "
                : "$" + reqDetail.getAmountToCollect();

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        setTouchNClick(R.id.cancel);
        setTouchNClick(R.id.btnSubmit);


        txtTime = (TextView) findViewById(R.id.txtTime);
        txtName = (TextView) findViewById(R.id.txtName);// ***************
        txtLicense = (TextView) findViewById(R.id.txtLicense);// *************
        txtServiceType = (TextView) findViewById(R.id.txtServiceType);
        txtPowerNo = (TextView) findViewById(R.id.txtPowerNo);
        txtInsComp = (TextView) findViewById(R.id.txtInsComp);
        txtCompName = (TextView) findViewById(R.id.txtCompName);
        txtReceiptNum = (TextView) findViewById(R.id.txtReceiptNum);
        txtInsamt = (TextView) findViewById(R.id.txtInsamt);
        postName = (TextView) findViewById(R.id.post_name);
        receiptId.setText(reqDetail.getAgentRequestId() + "");
        ibCardType = (ImageButton) findViewById(R.id.btnCardType);

        txtServiceType.setText("Posting");
        officerName.setText("Amount Collected ");
        pwrNo.setText("Power Used ");

        if (temp1.contains("$"))
            txtInsamt.setText(temp1);
        else
            txtInsamt.setText("$" + temp1);

//		txtPowerNo.setText(pwrNum);

        ArrayList<InsuranceModel> insList = reqDetail.getInsuranceList();
        String insur = "";
        if (insList != null && insList.size() > 0) {
            for (int ii = 0; ii < insList.size(); ii++) {
                if (ii != 0)
                    insur += "\n";
                insur += insList.get(ii).getName();
                txtInsComp.setText(insur + "");
            }
        }

        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        String[] date = getCurrentDateTime();
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
            String date2 = new SimpleDateFormat("MM/dd/yyyy").format(d);
            // txtDate.setText("Date:   " + date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtTime.setText("Time:   " + date[0] + "\n" + date[1]);
        txtLicense.setText(MainActivity.user.getLicenseno() + "");

        if (reqDetail != null) {

            txtName.setText("Self");
            txtCompName.setText(reqDetail.getSenderCompanyName() + "");
        }

    }

    private String[] getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss aa");
        String[] date = {sdfDate.format(cal.getTime()),
                sdfTime.format(cal.getTime())};

        return date;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.cancel)
            finish();

        else if (v.getId() == R.id.btnSubmit) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
