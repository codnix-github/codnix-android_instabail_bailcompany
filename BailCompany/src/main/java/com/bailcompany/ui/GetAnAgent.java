package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bailcompany.DefendantSelectionActivity;
import com.bailcompany.FindBestAgent;
import com.bailcompany.Launcher;
import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.AgentModel;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.GetAnAgentModel;
import com.bailcompany.model.IndemnitorModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("InflateParams")
public class GetAnAgent extends CustomFragment implements
        OnCheckedChangeListener {

    public static String location_entered;
    public static String agentRequestId;
    static EditText dateOfBirth;
    static int getCallTimeout = 50000;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    ArrayList<String[]> resList;
    String selectedItem = "";
    ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
    int i;
    String response2;
    String message;
    JSONObject jsonObj;
    String key;
    boolean selfBtnPress;
    int selectedIndex = -1;
    private Spinner selCountryCode;
    private CheckBox chkCourtFill;
    private CheckBox chkBailSource;
    private CheckBox chkCallAgency;
    private CheckBox chkNoPaper;
    private CheckBox chkIndPaper;
    private CheckBox chkDefendantPaper;
    private RadioButton radioPaymentAlr;
    private RadioButton radioPaymentToBe;
    private EditText warAmount;
    private EditText warTown;
    private EditText defFName;
    private EditText defLName;
    private EditText SSN;
    private EditText bookingNumber;
    private EditText editAmountTo;
    private EditText editPaymentPlan;
    private EditText indemFName;
    private EditText indemLName;
    private EditText indemPhone;
    private RadioButton rbNewDefendant, rbExistingDefendant;
    // private EditText location;
    private AutoCompleteTextView location;
    private EditText instruction, selectInsurance;
    private Button addMore, findBestAgent, selfAssign;
    private Button addMoreIndemnitors;
    private LinearLayout warrentLayout;
    private LinearLayout indDetail;
    private ArrayList<GetAnAgentModel> listWarrent;
    private ArrayList<GetAnAgentModel> listIndemnitors;
    private ArrayList<AgentModel> agentList;
    private String companyId;
    private String locLatt = "0.0", locLng = "0.0";
    private ArrayList<String> selectedItems;
    private boolean[] chkItems;
    private String defId = "";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_an_agent, null);
        setHasOptionsMenu(true);
        selectedItems = new ArrayList<String>();

        initValues(v);
        loadValues();
        loadFunction(v);
        setTouchNClick(findBestAgent);
        setTouchNClick(selfAssign);
        setTouchNClick(dateOfBirth);
        setTouchNClick(addMore);
        setTouchNClick(addMoreIndemnitors);
        setTouchNClick(selectInsurance);

        if (WebAccess.hireReferBailAgent) {
            setValuesFromBail();

        }
        if (WebAccess.hireTransferBondAgent) {
            setValuesFromBail();

        }
        getInsurance();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (WebAccess.agentHire) {
            WebAccess.agentHire = false;
            getFragmentManager().popBackStack();
            MainActivity.drawerLayout.openDrawer(MainActivity.drawerLeft);
        }
    }

    void getInsurance() {

        for (int i = 0; i < MainActivity.user.getInsurance().size(); i++) {
            InsuranceModel model = new InsuranceModel();
            model.setId(Integer.parseInt(MainActivity.user.getInsurance()
                    .get(i)));
            model.setName(MainActivity.user.getInsurancesName().get(i));
            insList.add(model);
        }
    }

    void setValuesFromBail() {
        if (WebAccess.agentRecord.getDefDOB() != null
                && !(WebAccess.agentRecord.getDefDOB().startsWith("1969")))
            // dateOfBirth.setText(changeDateFormat(WebAccess.agentRecord
            // .getDefDOB()));
            dateOfBirth.setText(Utils.getRequiredDateFormatGMT(
                    "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy",
                    WebAccess.agentRecord.getDefDOB()));
        if (WebAccess.agentRecord.getInsuranceList() != null) {
            String insurance = "";
            for (int i = 0; i < WebAccess.agentRecord.getInsuranceList().size(); i++) {
                InsuranceModel insur = WebAccess.agentRecord.getInsuranceList()
                        .get(i);
                if (i == 0)
                    insurance += insur;
                else
                    insurance += "," + insur;

            }
            selectInsurance.setText(insurance);
        }
        if (WebAccess.agentRecord.getDefBookingNumber() != null)
            bookingNumber.setText(WebAccess.agentRecord.getDefBookingNumber());
        if (WebAccess.agentRecord.getDefendantFName() != null)
            defFName.setText(WebAccess.agentRecord.getDefendantFName());
        if (WebAccess.agentRecord.getDefendantLName() != null)
            defLName.setText(WebAccess.agentRecord.getDefendantLName());
        if (WebAccess.agentRecord.getDefSSN() != null)
            SSN.setText(WebAccess.agentRecord.getDefSSN());
        if (WebAccess.agentRecord.getLocation() != null)
            location.setText(WebAccess.agentRecord.getLocation());
        if (WebAccess.agentRecord.getLocationLatitude() != null)
            locLatt = WebAccess.agentRecord.getLocationLatitude();
        if (WebAccess.agentRecord.getLocationLongitude() != null)
            locLng = WebAccess.agentRecord.getLocationLongitude();
        if (WebAccess.agentRecord.isIsCallAgency())
            chkCallAgency.setChecked(true);
        if (WebAccess.agentRecord.isNeedBailSource())
            chkBailSource.setChecked(true);
        if (WebAccess.agentRecord.isNeedCourtFee())
            chkCourtFill.setChecked(true);
        if (WebAccess.agentRecord.getWarrantList() != null) {

            for (int i = 0; i < WebAccess.agentRecord.getWarrantList().size(); i++) {
                WarrantModel war = WebAccess.agentRecord.getWarrantList()
                        .get(i);
                if (i == 0)
                    addWarrant2(false, war);

                else
                    addWarrant2(true, war);

            }

        }
        if (WebAccess.agentRecord.getIndemnitorsList() != null) {

            for (int i = 0; i < WebAccess.agentRecord.getIndemnitorsList()
                    .size(); i++) {
                IndemnitorModel indeminator = WebAccess.agentRecord
                        .getIndemnitorsList().get(i);
                String phNo[] = indeminator.getPhoneNumber().split(" ");
                if (i == 0) {
                    indemFName.setText(indeminator.getFName());
                    indemLName.setText(indeminator.getLName());
                    if (phNo.length > 2)
                        indemPhone.setText(phNo[phNo.length - 1]);
                } else
                    addIndeminator(indeminator);

            }

        }
        if (WebAccess.agentRecord.getInstructionForAgent() != null)
            instruction.setText(WebAccess.agentRecord.getInstructionForAgent());
        if (WebAccess.agentRecord.isNeedPaperWork()) {
            if (WebAccess.agentRecord.isNeedDefendantPaperwork())
                chkDefendantPaper.setChecked(true);
            if (WebAccess.agentRecord.isNeedIndemnitorPaperwork())
                chkIndPaper.setChecked(true);
        } else {
            chkNoPaper.setChecked(false);

        }
        if (WebAccess.agentRecord.isPaymentAlreadyReceived())
            radioPaymentAlr.setChecked(true);
        else {
            radioPaymentToBe.setChecked(true);
            editAmountTo.setText(WebAccess.agentRecord.getAmountToCollect());
            if (!WebAccess.hireReferBailAgent)
                editPaymentPlan.setText(WebAccess.agentRecord.getPaymentPlan());
        }
        WebAccess.hireReferBailAgent = false;
        WebAccess.hireTransferBondAgent = false;
    }

    private void initValues(View v) {
        companyId = getArguments().getString("companyId");
        selCountryCode = (Spinner) v.findViewById(R.id.sel_country_code);
        dateOfBirth = (EditText) v.findViewById(R.id.dob);
        selectInsurance = (EditText) v.findViewById(R.id.sel_insur_get_agent);
        location = (AutoCompleteTextView) v.findViewById(R.id.location_def);
        location.setAdapter(new PlacesAdaper(getActivity(),
                android.R.layout.simple_list_item_1));
        indemFName = (EditText) v.findViewById(R.id.ind_fname);
        indemLName = (EditText) v.findViewById(R.id.ind_lname);
        rbNewDefendant = (RadioButton) v.findViewById(R.id.rbNewDefendant);
        rbExistingDefendant = (RadioButton) v.findViewById(R.id.rbExistingDefendant);

        rbExistingDefendant.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startActivityForResult(
                            new Intent(getActivity(),
                                    DefendantSelectionActivity.class), 5555);
                }
            }
        });
        rbNewDefendant.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    defId = "";
                }
            }
        });

        indemFName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String text = indemFName.getText().toString().trim();
                    if (text.length() > 0)
                        text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                    indemFName.setText(text);

                }

            }
        });
        indemLName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String text = indemLName.getText().toString().trim();
                    if (text.length() > 0)
                        text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                    indemLName.setText(text);

                }

            }
        });

        indemPhone = (EditText) v.findViewById(R.id.ind_phone);
        location.setOnItemClickListener(new OnItemClickListener() {

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
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                dismissProgressDialog();
                                locLatt = latLng[0];
                                locLng = latLng[1];
                                Log.e("*****locLng", "locLatt=" + locLatt
                                        + " locLng=" + locLng);
                            }
                        });
                    }
                }).start();
            }
        });

		/*
         * Fragment fragment = new Fragment(); Bundle bundle = new Bundle();
		 * bundle.putString("locLatt", locLatt); bundle.putString("locLng",
		 * locLng); fragment.setArguments(bundle);
		 */

        chkCourtFill = (CheckBox) v.findViewById(R.id.chk1);
        chkBailSource = (CheckBox) v.findViewById(R.id.chk2);
        chkCallAgency = (CheckBox) v.findViewById(R.id.chk3);
        chkNoPaper = (CheckBox) v.findViewById(R.id.txt5);//
        chkIndPaper = (CheckBox) v.findViewById(R.id.txt6);//
        chkDefendantPaper = (CheckBox) v.findViewById(R.id.txt7);//

        radioPaymentAlr = (RadioButton) v.findViewById(R.id.radio1);
        radioPaymentToBe = (RadioButton) v.findViewById(R.id.radio2);

        defFName = (EditText) v.findViewById(R.id.def_fname);
        defLName = (EditText) v.findViewById(R.id.def_lname);
        SSN = (EditText) v.findViewById(R.id.ssn);
        bookingNumber = (EditText) v.findViewById(R.id.bkng_num);
        instruction = (EditText) v.findViewById(R.id.instructions);

        editAmountTo = (EditText) v.findViewById(R.id.edit_amount_to);

        editAmountTo.setHint(Html.fromHtml("<small>" + "Amount to collect"
                + "</small>"));

        editAmountTo.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                if (!hasFocus)
                    editAmountTo.setText(nf.format(Commons
                            .strToDouble(editAmountTo.getText().toString())));

                if (hasFocus
                        && editAmountTo.getText().toString().startsWith("$")) {
                    editAmountTo.setText(editAmountTo.getText().toString()
                            .replace("$", ""));
                } else if (!hasFocus
                        && !editAmountTo.getText().toString().startsWith("$")) {
                    editAmountTo.setText("$"
                            + editAmountTo.getText().toString());
                }
            }
        });
        editPaymentPlan = (EditText) v.findViewById(R.id.edit_payment_plan);

        editPaymentPlan.setHint(Html.fromHtml("<small>"
                + "Payment Plan (Enter Amount and Frequency)" + "</small>"));

        addMore = (Button) v.findViewById(R.id.add_more);
        findBestAgent = (Button) v.findViewById(R.id.find_best_agent);
        selfAssign = (Button) v.findViewById(R.id.self_assigned);
        addMoreIndemnitors = (Button) v.findViewById(R.id.add_more_ind);
        warrentLayout = (LinearLayout) v.findViewById(R.id.warrent_layout);
        indDetail = (LinearLayout) v.findViewById(R.id.ind_details);

        radioPaymentAlr.setOnCheckedChangeListener(this);
        radioPaymentToBe.setOnCheckedChangeListener(this);

        editAmountTo.setEnabled(false);
        editPaymentPlan.setEnabled(false);
        phoneFormat((EditText) v.findViewById(R.id.ind_phone));
        if (!(WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent))
            addWarrant(false);

    }

    private void phoneFormat(final EditText ph) {
        ph.setOnFocusChangeListener(new OnFocusChangeListener() {

            @SuppressWarnings("deprecation")
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

    private void loadValues() {
        selCountryCode
                .setAdapter(((MainActivity) getActivity())
                        .getAdapter(((MainActivity) getActivity())
                                .getCountryCodeList()));
        // selCountryCode.setEnabled(false);
    }

    private void loadFunction(View v) {
        setClick(v.findViewById(R.id.txt1));
        setClick(v.findViewById(R.id.txt2));
        setClick(v.findViewById(R.id.txt3));
        // CheckBox cb=(CheckBox)v.findViewById(R.id.txt5);
        setClick(chkNoPaper);
        // setClick(v.findViewById(R.id.txt5));
        // setClick(v.findViewById(R.id.txt6));
        // setClick(v.findViewById(R.id.txt7));
        setClick(v.findViewById(R.id.radio1));
        setClick(v.findViewById(R.id.radio2));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        hideKeyboard();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (v.getId()) {
            case R.id.txt1:
                if (chkCourtFill.isChecked())
                    chkCourtFill.setChecked(false);
                else
                    chkCourtFill.setChecked(true);
                break;
            case R.id.txt2:
                if (chkBailSource.isChecked())
                    chkBailSource.setChecked(false);
                else
                    chkBailSource.setChecked(true);
                break;
            case R.id.txt3:
                if (chkCallAgency.isChecked())
                    chkCallAgency.setChecked(false);
                else
                    chkCallAgency.setChecked(true);
                break;

            case R.id.txt5:
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    chkIndPaper.setChecked(false);
                    chkDefendantPaper.setChecked(false);
                    // chkIndPaper.setChecked(false);
                    // chkDefendantPaper.setChecked(false);
                    chkIndPaper.setEnabled(false);
                    chkDefendantPaper.setEnabled(false);

                } else {
                    // chkNoPaper.setChecked(true);
                    chkIndPaper.setEnabled(true);
                    chkDefendantPaper.setEnabled(true);

                    // chkIndPaper .setEnabled(false);
                    // chkDefendantPaper .setEnabled(false);
                }

                break;
            case R.id.radio1:
                if (radioPaymentAlr.isChecked()) {

                    editAmountTo.setEnabled(false);
                    editPaymentPlan.setEnabled(false);
                }
                break;
            case R.id.radio2:
                if (radioPaymentToBe.isChecked()) {

                    editAmountTo.setEnabled(true);
                    editPaymentPlan.setEnabled(true);
                }
                break;
            case R.id.find_best_agent:

                if (!rbExistingDefendant.isChecked()) {
                    defId = "";
                }

                if (isValid() && getWarrents() && getIndemnitors() && Radio()
                        && Checked() && Insurance()) // $$$$$$$$$$$$$$$$$$$$
                // Validation
                // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                {
                    if (Utils.isOnline(getActivity())) {

                        selfBtnPress = false;
                        showProgressDialog("");
                        WebAccess.params = saveForumData();
                        getAgentList();

                        // getBestAgent();
                    } else
                        Utils.noInternetDialog(getActivity());
                }
                break;
            case R.id.self_assigned:
                if (!rbExistingDefendant.isChecked()) {
                    defId = "";
                }

                if (isValid() && getWarrents() && getIndemnitors() && Radio()
                        && Checked() && Insurance()) // $$$$$$$$$$$$$$$$$$$$
                // Validation
                // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                {
                    if (Utils.isOnline(getActivity())) {
                        selfBtnPress = true;
                        getBestAgent();
                    } else
                        Utils.noInternetDialog(getActivity());
                }
                break;
            case R.id.dob:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.add_more:

                addWarrant(true);
                break;
            case R.id.add_more_ind:
                if (indDetail.getChildCount() <= 5) {
                    final View indView = inflater.inflate(
                            R.layout.indemnitor_layout, null);
                    final EditText ind_fname = (EditText) indView.findViewById(R.id.ind_fname);
                    final EditText ind_lname = (EditText) indView.findViewById(R.id.ind_lname);
                    ind_fname.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (!hasFocus) {
                                String text = ind_fname.getText().toString().trim();
                                if (text.length() > 0)
                                    text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                                ind_fname.setText(text);
                                // Toast.makeText(getActivity(), "Changes" + hasFocus, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    ind_lname.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (!hasFocus) {
                                String text = ind_lname.getText().toString().trim();
                                if (text.length() > 0)
                                    text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                                ind_lname.setText(text);

                            }

                        }
                    });

                    Spinner code = (Spinner) indView
                            .findViewById(R.id.sel_country_code);
                    code.setAdapter(((MainActivity) getActivity())
                            .getAdapter(((MainActivity) getActivity())
                                    .getCountryCodeList()));
                    // code.setEnabled(false);
                    ImageButton removeInd = (ImageButton) indView
                            .findViewById(R.id.remove);
                    removeInd.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) indView.getParent())
                                    .removeView(indView);

                        }
                    });
                    indDetail.addView(indView);
                    phoneFormat((EditText) indView.findViewById(R.id.ind_phone));
                }
                break;

            case R.id.sel_insur_get_agent:
                if (insList != null && insList.size() > 0) {
                    chkItems = new boolean[insList.size()];
                    showInsDialog();
                }
                break;

        }
    }

    void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showInsDialog() {
        //selectedItem = "";
        final CharSequence[] items = new CharSequence[insList.size()];
        final ArrayList<String> selItems = new ArrayList<String>();
        selItems.addAll(selectedItems);
        for (int i = 0; i < insList.size(); i++) {
            items[i] = insList.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Insurance").setSingleChoiceItems(items, selectedIndex, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  Toast.makeText(getActivity(), "Pos-" + which, Toast.LENGTH_SHORT).show();
                selectedItem = items[which].toString();
                selectedIndex = which;

            }
        }).setPositiveButton("Ok", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int index = 0; index < items.length; index++) {
                    if (selectedItem.equals(items[index].toString())) {
                        selectedIndex = index;
                    }
                }
                selectInsurance.setText(selectedItem);
            }
        }).setNegativeButton("Cancel", null).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
    }

    // private JSONObject getJsonObject() {
    // JSONObject obj = new JSONObject();
    //
    // try {
    // String amount = editAmountTo.getText().toString().trim();
    // obj.put("AmountToCollect",
    // Commons.isEmpty(amount) ? "0"
    // : (amount.startsWith("$") ? amount.replace("$", "")
    // : amount));
    // obj.put("CompanyId", companyId);
    // obj.put("UserName ", MainActivity.user.getUsername());
    // obj.put("DefBookingNumber", bookingNumber.getText().toString());
    // obj.put("DefDOB",
    // Commons.timeMillisToJsonDate(
    // Commons.dateTimeToMillis2(dateOfBirth.getText()
    // .toString()), false));
    // obj.put("DefSSN", SSN.getText().toString());
    // obj.put("DefendantName", defName.getText().toString());
    // obj.put("InstructionForAgent", instruction.getText().toString());
    // obj.put("IsCallAgency", chkCallAgency.isChecked() ? true : false);
    // obj.put("LocationLatitude", locLatt);
    // obj.put("LocationLongitude", locLng);
    // obj.put("Location", location_entered);
    // obj.put("NeedBailSource", chkBailSource.isChecked() ? true : false);
    // obj.put("NeedCourtFee", chkCourtFill.isChecked() ? true : false);
    // obj.put("NeedDefendantPaperwork",
    // chkDefendantPaper.isChecked() ? true : false);
    // obj.put("NeedIndemnitorPaperwork", chkIndPaper.isChecked() ? true
    // : false);
    // obj.putOpt("TemporaryAccessCode ",
    // MainActivity.user.getTempAccessCode());
    // obj.put("NeedPaperWork", chkNoPaper.isChecked() ? false : true);
    // Log.e("@@@@NeedPaperWork", "" + chkNoPaper.isChecked());
    // obj.put("NumberIndemnitors", indDetail.getChildCount());
    // obj.put("PaymentAlreadyReceived",
    // radioPaymentAlr.isChecked() ? true : false);
    // obj.put("PaymentPlan", editPaymentPlan.getText().toString());
    // // obj.put("Insurance",selectInsurance.toString());
    //
    // JSONArray warr = new JSONArray();
    // for (int i = 0; i < listWarrent.size(); i++) {
    // JSONObject js = new JSONObject();
    //
    // js.put("AgentRequestId", 0);
    // js.put("Amount", listWarrent.get(i).getWarrentAmount());
    // js.put("Township", listWarrent.get(i).getCharginTown());
    // warr.put(js);
    // Log.e("js", "" + js.toString());
    // }
    // obj.putOpt("WarrantList", warr);
    // JSONArray ind = new JSONArray();
    // for (int i = 0; i < listIndemnitors.size(); i++) {
    // JSONObject js = new JSONObject();
    // js.put("AgentRequestId", 0);
    // js.put("Name", listIndemnitors.get(i).getIndemnitorName());
    // js.put("PhoneNumber", listIndemnitors.get(i)
    // .getIndemnitorPhone());
    // ind.put(js);
    // }
    // obj.putOpt("IndemnitorsList", ind);
    //
    // JSONArray iarr = new JSONArray();
    // for (int i = 0; i < selectedItems.size(); i++) {
    // JSONObject js = new JSONObject();
    // js.put("AgentRequestId", 0);
    // for (InsuranceModel mod : insList) {
    // if (selectedItems.get(i).equals(mod.getId() + "")) {
    // js.put("InsuranceId", mod.getId() + "");
    // js.put("InsuranceName", mod.getName() + "");
    // break;
    // }
    // }
    //
    // iarr.put(js);
    // }
    // obj.putOpt("InsuranceList", iarr);
    //
    // return obj;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    // ********************************//editing1
    private void addWarrant(boolean showCancel) {
        // warrentLayout.removeAllViews();
        final View warrent = getActivity().getLayoutInflater().inflate(
                R.layout.add_warrent_layout, null);
        if (!showCancel)
            warrent.findViewById(R.id.remove).setVisibility(View.GONE);
        final EditText warAmount = (EditText) warrent.findViewById(R.id.amount);
        final EditText township = (EditText) warrent.findViewById(R.id.township);

        warAmount.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                if (!hasFocus)
                    warAmount.setText(Commons.strToDouble(warAmount.getText()
                            .toString()) + "");

                Log.d("Amount1", hasFocus + "==="
                        + warAmount.getText().toString());
                if (hasFocus && warAmount.getText().toString().startsWith("$")) {
                    warAmount.setText(warAmount.getText().toString()
                            .replace("$", ""));
                } else if (!hasFocus
                        && !warAmount.getText().toString().startsWith("$")) {
                    String str = warAmount.getText().toString();

                    warAmount.setText("$" + nf.format(Double.parseDouble(str)));

                }
            }
        });

        township.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {

                    String text = township.getText().toString().trim();

                    if (text.length() > 0)
                        text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");

                    township.setText(text);
                    // Toast.makeText(getActivity(), "Changes" + hasFocus, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton remove = (ImageButton) warrent.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((LinearLayout) warrent.getParent()).removeView(warrent);
            }
        });

        warrentLayout.addView(warrent);
    }

    public void addIndeminator(IndemnitorModel indem) {
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (indDetail.getChildCount() <= 5) {
            final View indView = inflater.inflate(R.layout.indemnitor_layout,
                    null);
            Spinner code = (Spinner) indView
                    .findViewById(R.id.sel_country_code);
            code.setAdapter(((MainActivity) getActivity())
                    .getAdapter(((MainActivity) getActivity())
                            .getCountryCodeList()));
            String phNum[] = indem.getPhoneNumber().split(" ");
            // code.setEnabled(false);

            ImageButton removeInd = (ImageButton) indView
                    .findViewById(R.id.remove);
            removeInd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((LinearLayout) indView.getParent()).removeView(indView);

                }
            });
            indDetail.addView(indView);
            final EditText indemfirstName = (EditText) indView.findViewById(R.id.ind_fname);
            indemfirstName.setText(indem.getFName());
            final EditText indemlastName = (EditText) indView.findViewById(R.id.ind_lname);
            indemlastName.setText(indem.getLName());
            indemfirstName.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        String text = indemfirstName.getText().toString().trim();
                        if (text.length() > 0)
                            text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                        indemfirstName.setText(text);

                    }

                }
            });
            indemlastName.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        String text = indemlastName.getText().toString().trim();
                        if (text.length() > 0)
                            text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                        indemlastName.setText(text);

                    }

                }
            });


            EditText phoneNumer = (EditText) indView
                    .findViewById(R.id.ind_phone);
            if (phNum.length > 2)
                phoneNumer.setText(phNum[3]);
            phoneFormat(phoneNumer);
        }
    }

    private void addWarrant2(boolean showCancel, WarrantModel warr) {
        // warrentLayout.removeAllViews();
        final View warrent = getActivity().getLayoutInflater().inflate(
                R.layout.add_warrent_layout, null);
        if (!showCancel)
            warrent.findViewById(R.id.remove).setVisibility(View.GONE);
        final EditText warAmount = (EditText) warrent.findViewById(R.id.amount);
        final EditText township = (EditText) warrent
                .findViewById(R.id.township);
        warAmount.setText("$" + warr.getAmount());
        township.setText(warr.getTownship());
        warAmount.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                if (!hasFocus)
                    warAmount.setText(Commons.strToDouble(warAmount.getText()
                            .toString()) + "");

                Log.d("Amount1", hasFocus + "==="
                        + warAmount.getText().toString());
                if (hasFocus && warAmount.getText().toString().startsWith("$")) {
                    warAmount.setText(warAmount.getText().toString()
                            .replace("$", ""));
                } else if (!hasFocus
                        && !warAmount.getText().toString().startsWith("$")) {
                    String str = warAmount.getText().toString();

                    warAmount.setText("$" + nf.format(Double.parseDouble(str)));

                }
            }
        });

        township.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String text = township.getText().toString().trim();
                    if (text.length() > 0)
                        text = text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1) : "");
                    township.setText(text);
                    // Toast.makeText(getActivity(), "Changes" + hasFocus, Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageButton remove = (ImageButton) warrent.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((LinearLayout) warrent.getParent()).removeView(warrent);
            }
        });

        warrentLayout.addView(warrent);
    }

    private boolean Insurance() {
        boolean done = true;
        if (selectInsurance.getText().toString().length() == 0) {
            done = false;
            Utils.showDialog(getActivity(), "Please select insurance !");

        }
        return done;
    }

    private boolean Checked() {
        if (chkNoPaper.isChecked() || chkIndPaper.isChecked()
                || chkDefendantPaper.isChecked()) {
            return true;
        } else {
            Utils.showDialog(getActivity(), "Please select paperwork !");
            return false;
        }

    }

    private boolean Radio() {
        if (!radioPaymentAlr.isChecked() && !radioPaymentToBe.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please select payment status !")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.show();
            return false;
        } else if (radioPaymentToBe.isChecked()) {
            // /if(editAmountTo.length()==0)
            if (editAmountTo.getText().toString().length() == 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setMessage(" Enter amount to collect !")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.show();
                return false;
            }

			/*
             * else if(editPaymentPlan.getText().toString().length() == 0) {
			 * 
			 * AlertDialog.Builder builder1 = new
			 * AlertDialog.Builder(getActivity());
			 * builder1.setMessage("Enter payment plan.!") .setCancelable(false)
			 * .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 * public void onClick(DialogInterface dialog, int id) { //
			 * finish(); dialog.cancel(); } }) ;
			 * 
			 * AlertDialog alert1 = builder1.create();
			 * alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 * alert1.show(); return false; }
			 */
            else {
            }

        }
        return true;

    }

    private boolean getWarrents() {
        listWarrent = new ArrayList<GetAnAgentModel>();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        for (int i = 0; i < warrentLayout.getChildCount(); i++) {
            GetAnAgentModel model = new GetAnAgentModel();
            LinearLayout outer = (LinearLayout) warrentLayout.getChildAt(i);

            // final EditText warAmount = (EditText)outer.getChildAt(0);
            warAmount = (EditText) outer.getChildAt(0);
            warTown = (EditText) outer.getChildAt(2);

            String amt = warAmount.getText().toString();
            try {
                amt = nf.parse(amt).toString();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                if (amt.startsWith("$"))
                    amt = amt.replace("$", "");
                amt = amt.replaceAll(",", "");
            }

            if (amt.length() == 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setMessage(" Enter warrant amount !")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.show();
                return false;
            } else if (warTown.length() == 0) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        getActivity());
                builder1.setMessage("Enter charging township!")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert1 = builder1.create();
                alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert1.show();
                // Utils.showDialog(getActivity(), "Please enetr warAmount !");
                return false;
            } else {
                model.setWarrentAmount(Double.parseDouble(amt));
                model.setCharginTown(warTown.getText().toString());
                listWarrent.add(model);
            }

            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

			/*
             * if(amt.length() == 0) {
			 * 
			 * warAmount.setError("Enter amount !");
			 * if(warTown.getText().toString().length() == 0)
			 * warTown.setError("Enter town !"); done = false; break; } else {
			 * done = true; warAmount.setError(null); warTown.setError(null);
			 * model.setWarrentAmount(Double.parseDouble(amt));
			 * model.setCharginTown(warTown.getText().toString());
			 * listWarrent.add(model); }
			 */
        }
        return true;
    }

    private boolean getIndemnitors() {

        listIndemnitors = new ArrayList<GetAnAgentModel>();
        for (int i = 0; i < indDetail.getChildCount(); i++) {

            GetAnAgentModel model = new GetAnAgentModel();
            LinearLayout main1 = (LinearLayout) indDetail.getChildAt(i);
            LinearLayout main = (LinearLayout) main1.getChildAt(0);
            EditText indFName = (EditText) main.getChildAt(0);
            EditText indLName = (EditText) main.getChildAt(1);
            LinearLayout l = (LinearLayout) main.getChildAt(2);
            Spinner code = (Spinner) l.getChildAt(0);
            EditText phone = (EditText) l.getChildAt(2);
            // **********************************************
            // if (indName.getText().toString().length() == 0) {
            //
            // AlertDialog.Builder builder = new AlertDialog.Builder(
            // getActivity());
            // builder.setMessage("Enter indemnitor's name !")
            // .setCancelable(false)
            // .setPositiveButton("OK",
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog,
            // int id) {
            // dialog.cancel();
            // }
            // });
            //
            // AlertDialog alert = builder.create();
            // alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // alert.show();
            // return false;
            // }
            // /*
            // * else if(phone.getText().toString().length() == 0) {
            // *
            // * AlertDialog.Builder builder1 = new
            // * AlertDialog.Builder(getActivity());
            // * builder1.setMessage("Enter indemnitor's phone no.!")
            // * .setCancelable(false) .setPositiveButton("OK", new
            // * DialogInterface.OnClickListener() { public void
            // * onClick(DialogInterface dialog, int id) { // finish();
            // * dialog.cancel(); } }) ;
            // *
            // * AlertDialog alert1 = builder1.create();
            // * alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // * alert1.show(); return false; }
            // */
            // else {
            //
            // model.setIndemnitorName(indName.getText().toString());
            // model.setIndemnitorPhone(code.getSelectedItem().toString()
            // + phone.getText().toString());
            // listIndemnitors.add(model);
            // }

            if (!indFName.getText().toString().trim().equals("") ||
                    !indLName.getText().toString().trim().equals("") ||
                    !phone.getText().toString().trim().equals("")) {
                model.setIndemnitorName(indFName.getText().toString() + " " + indLName.getText().toString());
                model.setIndemnitorFName(indFName.getText().toString());
                model.setIndemnitorLName(indLName.getText().toString());

                if (!phone.getText().toString().trim().equalsIgnoreCase("")) {
                    model.setIndemnitorPhone(code.getSelectedItem().toString()
                            + phone.getText().toString());
                } else {
                    model.setIndemnitorPhone("");
                }

                listIndemnitors.add(model);
            }
        }
        return true;
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%% Alert

    private boolean isValid()

    {
        location_entered = location.getText().toString();
        if (defFName.getText().toString().length() == 0)
            Utils.showDialog(getActivity(), "Please enter defendant first name !");
        else {

            if (location_entered.length() == 0)
                Utils.showDialog(getActivity(),
                        "Please enter location of defendant !");

            else
                return true;

        }
        // }
        return false;
    }

    // private void findBestAgent() {
    // ((MainActivity) getActivity()).showProgressDialog("");
    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // agentList = WebAccess.getFindBestAgent(getJsonObject());
    // getActivity().runOnUiThread(new Runnable() {
    //
    // @Override
    // public void run() {
    // ((MainActivity) getActivity()).dismissProgressDialog();
    // // Log.d("Agents",
    // // "Best Agents: "+agentList==null?" None":agentList.size()+"");
    // if (agentList != null && agentList.size() > 0) {
    // getActivity().startActivity(
    // new Intent(getActivity(),
    // FindBestAgent.class)
    // .putExtra("agents", agentList)
    // .putExtra("reqid",
    // WebAccess.agentRequestId)
    // .putExtra("locLatt", locLatt)
    // .putExtra("locLng", locLng));
    //
    // }
    // }
    // });
    //
    // }
    // }).start();
    // }

    @Override
    public void onCheckedChanged(CompoundButton cb, boolean checked) {
        // TODO Auto-generated method stub

        editAmountTo.setEnabled(radioPaymentToBe.isChecked());
        editPaymentPlan.setEnabled(radioPaymentToBe.isChecked());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String key = data.getStringExtra(Const.RETURN_FLAG);
            if (key.equalsIgnoreCase(Const.RETURN_DEFENDANT_DETAIL)) {

                DefendantModel defModel = (DefendantModel) data.getSerializableExtra(Const.EXTRA_DATA);
                if (defModel != null) {
                    defId = defModel.getId();
                    defFName.setText(defModel.getFirstName());
                    defLName.setText(defModel.getLastName());
                    SSN.setText(defModel.getSSN());
                    dateOfBirth.setText(defModel.getDOB());
                } else {
                    defId = "";
                }

            }
        }
    }

    // public String changeDateFormat(String s) {
    // Date d = null;
    // String date = "";
    // SimpleDateFormat formatter;
    // formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    // try {
    // d = new Date();
    // d = formatter.parse(s);
    // date = new SimpleDateFormat("MM/dd/yyyy").format(d);
    //
    // } catch (ParseException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // return date;
    // }

    void getBestAgent() {


        showProgressDialog("");
        Date d = null;
        String date = "";
        SimpleDateFormat formatter;
        try {
            d = new Date();
            d = new SimpleDateFormat("MM/dd/yyyy").parse(dateOfBirth.getText()
                    .toString());
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = formatter.format(d);

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String amount = editAmountTo.getText().toString().trim();
        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("DefendantName", defFName.getText().toString() + " " + defLName.getText().toString());
        param.put("firstname", defFName.getText().toString());
        param.put("lastname", defLName.getText().toString());

        param.put("DefDOB", date);
        param.put("DefSSN", SSN.getText().toString());
        param.put("DefBookingNumber", bookingNumber.getText().toString());
        param.put("LocationLongitude", locLng);
        param.put("LocationLatitude", locLatt);
        param.put("NeedCourtFee", chkCourtFill.isChecked() ? 1 : 0);
        param.put("NeedBailSource", chkBailSource.isChecked() ? 1 : 0);
        param.put("IsCallAgency", chkCallAgency.isChecked() ? 1 : 0);
        param.put("NumberIndemnitors", indDetail.getChildCount());
        param.put("NeedPaperWork", chkNoPaper.isChecked() ? 0 : 1);
        param.put("NeedIndemnitorPaperwork", chkIndPaper.isChecked() ? 1 : 0);
        param.put("NeedDefendantPaperwork", chkDefendantPaper.isChecked() ? 1
                : 0);
        param.put("PaymentAlreadyReceived", radioPaymentAlr.isChecked() ? 1 : 0);
        param.put("DefId", defId);


        if (!radioPaymentAlr.isChecked()) {
            param.put("AmountToCollect",
                    Commons.isEmpty(amount) ? 0
                            : (amount.startsWith("$") ? amount.replace("$", "")
                            .replace(",", "") : amount));
            param.put("PaymentPlan", editPaymentPlan.getText().toString());
        }
        param.put("InstructionForAgent", instruction.getText().toString());
        param.put("CompanyId", companyId);
        param.put("Location", location_entered);
        for (int i = 0; i < listWarrent.size(); i++) {
            param.put("Amount[" + i + "]", listWarrent.get(i)
                    .getWarrentAmount());
            param.put("Township[" + i + "]", listWarrent.get(i)
                    .getCharginTown());

        }
        for (int i = 0; i < listIndemnitors.size(); i++) {
            param.put("Name[" + i + "]", listIndemnitors.get(i)
                    .getIndemnitorName());
            param.put("ind_firstname[" + i + "]", listIndemnitors.get(i)
                    .getIndemnitorFName());
            param.put("ind_lastname[" + i + "]", listIndemnitors.get(i)
                    .getIndemnitorLName());
            param.put("PhoneNumber[" + i + "]", listIndemnitors.get(i)
                    .getIndemnitorPhone());
        }

        param.put("InsuranceId", insList.get(selectedIndex).getId() + "");

        String url = WebAccess.MAIN_URL + WebAccess.GET_AN_AGENT;
        client.setTimeout(getCallTimeout);
        client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                dismissProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                try {
                    response2 = new String(responseBody);
                    if (response2 != null) {
                        JSONObject json = new JSONObject(response2);
                        if (json.optString("status").equalsIgnoreCase("1")) {
                            agentRequestId = json.optString("agent_request_id");

                            if (selfBtnPress) {
                                submitSelfRequest();
                            } else {
                                getAgentList();
                            }
                        } else if (json.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(getActivity(),
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            MainActivity.sp.edit().putBoolean("isFbLogin",
                                    false);
                            MainActivity.sp.edit().putString("user", null)
                                    .commit();
                            startActivity(new Intent(getActivity(), Login.class));
                        } else {
                            Utils.showDialog(getActivity(),
                                    json.optString("message"));
                            dismissProgressDialog();
                        }
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }

        });

    }

    private void getAgentList() {

        RequestParams param = new RequestParams();

        param.put("CompanyId", MainActivity.user.getCompanyId());
        param.put("Page", "0");
        param.put("LocationLongitude", "" + locLng + "");
        param.put("LocationLatitude", "" + locLatt + "");
        param.put("UserName", MainActivity.user.getUsername());
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("InsuranceId[0]", insList.get(selectedIndex).getId() + "");

        String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_AGENT;
        client.setTimeout(getCallTimeout);
        client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

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
                        JSONObject json = new JSONObject(response2);
                        if (json.optString("status").equalsIgnoreCase("1")) {

                            JSONArray jArray = json
                                    .getJSONArray("list_of_agents");
                            agentList = WebAccess.parseAgentList(jArray);
                            if (agentList != null && agentList.size() > 0) {
                                startActivity(new Intent(getActivity(),
                                        FindBestAgent.class)
                                        .putExtra("agents", agentList)
                                        .putExtra("reqid",
                                                WebAccess.agentRequestId)
                                        .putExtra("locLatt", locLatt)
                                        .putExtra("locLng", locLng));

                            } else if (json.optString("status")
                                    .equalsIgnoreCase("3")) {
                                Toast.makeText(
                                        getActivity(),
                                        "Session was closed please login again",
                                        Toast.LENGTH_LONG).show();
                                MainActivity.sp.edit().putBoolean("isFbLogin",
                                        false);
                                MainActivity.sp.edit().putString("user", null)
                                        .commit();
                                startActivity(new Intent(getActivity(),
                                        Launcher.class));
                            } else {
                                Utils.showDialog(getActivity(),
                                        "No Agent Found Near this location");
                                dismissProgressDialog();
                            }
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

    void submitSelfRequest() {

        showProgressDialog("");
        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("RequestId", agentRequestId);
        String url = WebAccess.MAIN_URL + WebAccess.SELF_REQUEST;
        client.setTimeout(getCallTimeout);
        client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {
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
                        if (json.optString("status").equalsIgnoreCase("1")) {
                            message = json.optString("message");
                            selfBtnPress = false;
                            showDialog(getActivity(), json.optString("message"));

                        } else if (json.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(getActivity(),
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            MainActivity.sp.edit().putBoolean("isFbLogin",
                                    false);
                            MainActivity.sp.edit().putString("user", null)
                                    .commit();
                            startActivity(new Intent(getActivity(), Login.class));
                        } else {
                            Utils.showDialog(getActivity(),
                                    json.optString("message"));
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        });

    }

    public RequestParams saveForumData() {
        Date d = null;
        String date = "";
        SimpleDateFormat formatter;
        try {
            d = new Date();
            d = new SimpleDateFormat("MM/dd/yyyy").parse(dateOfBirth.getText()
                    .toString());
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = formatter.format(d);

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String amount = editAmountTo.getText().toString().trim();
        RequestParams param = new RequestParams();


        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("DefendantName", defFName.getText().toString() + " " + defLName.getText().toString());
        param.put("firstname", defFName.getText().toString());
        param.put("lastname", defLName.getText().toString());
        param.put("DefDOB", date);
        param.put("DefSSN", SSN.getText().toString());
        param.put("DefBookingNumber", bookingNumber.getText().toString());
        param.put("LocationLongitude", locLng);
        param.put("LocationLatitude", locLatt);
        param.put("NeedCourtFee", chkCourtFill.isChecked() ? 1 : 0);
        param.put("NeedBailSource", chkBailSource.isChecked() ? 1 : 0);
        param.put("IsCallAgency", chkCallAgency.isChecked() ? 1 : 0);
        param.put("NeedPaperWork", chkNoPaper.isChecked() ? 0 : 1);
        param.put("NeedIndemnitorPaperwork", chkIndPaper.isChecked() ? 1 : 0);
        param.put("NeedDefendantPaperwork", chkDefendantPaper.isChecked() ? 1
                : 0);
        param.put("PaymentAlreadyReceived", radioPaymentAlr.isChecked() ? 1 : 0);
        param.put("DefId", defId);

        if (!radioPaymentAlr.isChecked()) {
            param.put("AmountToCollect",
                    Commons.isEmpty(amount) ? 0
                            : (amount.startsWith("$") ? amount.replace("$", "")
                            .replace(",", "") : amount));
            param.put("PaymentPlan", editPaymentPlan.getText().toString());
        }
        param.put("InstructionForAgent", instruction.getText().toString());
        param.put("CompanyId", companyId);
        param.put("Location", location_entered);

        for (int i = 0; i < listWarrent.size(); i++) {
            param.put("Amount[" + i + "]", listWarrent.get(i)
                    .getWarrentAmount());
            param.put("Township[" + i + "]", listWarrent.get(i)
                    .getCharginTown());

        }

        if (listIndemnitors.size() > 0) {
            for (int i = 0; i < listIndemnitors.size(); i++) {
                param.put("Name[" + i + "]", listIndemnitors.get(i)
                        .getIndemnitorName());
                param.put("ind_firstname[" + i + "]", listIndemnitors.get(i)
                        .getIndemnitorFName());
                param.put("ind_lastname[" + i + "]", listIndemnitors.get(i)
                        .getIndemnitorLName());
                param.put("PhoneNumber[" + i + "]", listIndemnitors.get(i)
                        .getIndemnitorPhone());
            }

            param.put("NumberIndemnitors", listIndemnitors.size());
        } else {
            param.put("NumberIndemnitors", 0);
        }

        param.put("InsuranceId", insList.get(selectedIndex).getId());

        return param;
    }

    public AlertDialog showDialog(Context ctx, String msg)// ///hello
    {

        return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                getFragmentManager().popBackStack();
                MainActivity.drawerLayout.openDrawer(MainActivity.drawerLeft);
            }
        });

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
            dateOfBirth.setText(date);
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
                            Toast.makeText(getActivity(), "Please wait...",
                                    Toast.LENGTH_SHORT).show();
                        resList = Utils.searchPlaces(constraint.toString());
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
