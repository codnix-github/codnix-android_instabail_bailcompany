package com.bailcompany;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.DefendantEmploymentModel;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.StateModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.FilePath;
import com.bailcompany.utils.ImageSelector;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class DefendantEmploymentDetails extends CustomActivity {

    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;

    String message;
    JSONObject jsonObj;

    DefendantModel defModel;
    String defId;
    ArrayList<DefendantEmploymentModel> arrDefEmpDtl;
    LinearLayout llEmpDetails;
    ArrayList<StateModel> allState = null;
    boolean isEdit = false;

    private Context _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendant_employment);
        setActionBar(getString(R.string.title_activity_defendant_employment_detail));
        _activity = DefendantEmploymentDetails.this;
        llEmpDetails = (LinearLayout) findViewById(R.id.llEmpDetails);
        ((Button) findViewById(R.id.btnAddEmploymentDtl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialog(null);
            }
        });

        if (getIntent().hasExtra("defendant")) {
            defModel = (DefendantModel) getIntent()
                    .getSerializableExtra("defendant");
            if (defModel != null) {
                defId = defModel.getId();
                arrDefEmpDtl = defModel.getEmploymentDtl();
                showDetails();
            }
        }
        getAllStates();
    }

    public void showDetails() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final DefendantEmploymentModel eMod : arrDefEmpDtl) {
            View v = inflater.inflate(R.layout.row_defendant_employment_details, null, false);
            ((TextView) v.findViewById(R.id.tvEmployer)).setText(eMod.getEmployer());
            ((TextView) v.findViewById(R.id.tvOccupation)).setText(eMod.getOccupation());
            ((TextView) v.findViewById(R.id.tvAddress)).setText(eMod.getAddress());
            ((TextView) v.findViewById(R.id.tvCity)).setText(eMod.getCity());
            ((TextView) v.findViewById(R.id.tvState)).setText(eMod.getState());
            ((TextView) v.findViewById(R.id.tvZip)).setText(eMod.getZip());
            ((TextView) v.findViewById(R.id.tvTelephone)).setText(eMod.getTelephone());
            ((TextView) v.findViewById(R.id.tvSupervisor)).setText(eMod.getSupervisor());
            ((TextView) v.findViewById(R.id.tvDuration)).setText(eMod.getDuration());

            ((ImageView) v.findViewById(R.id.edtEmpDetails)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEditDialog(eMod);
                }
            });

            llEmpDetails.addView(v);
        }

    }

    public void openEditDialog(final DefendantEmploymentModel eMod) {
        AlertDialog dialog;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogForm = inflater.inflate(R.layout.dialog_edit_defendant_emp_details, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(DefendantEmploymentDetails.this);
        builder.setView(dialogForm);
        builder.create();

        final Spinner spState = (Spinner) dialogForm.findViewById(R.id.spState);
        final EditText edtEmployer = (EditText) dialogForm.findViewById(R.id.edtEmployer);
        final EditText edtOccupation = (EditText) dialogForm.findViewById(R.id.edtOccupation);
        final EditText edtAddress = (EditText) dialogForm.findViewById(R.id.edtAddress);
        final EditText edtCity = (EditText) dialogForm.findViewById(R.id.edtCity);
        final EditText edtZipCode = (EditText) dialogForm.findViewById(R.id.edtZipCode);
        final EditText edtTelephone = (EditText) dialogForm.findViewById(R.id.edtTelephone);
        final EditText edtSupervisor = (EditText) dialogForm.findViewById(R.id.edtSupervisor);
        final EditText edtDuration = (EditText) dialogForm.findViewById(R.id.edtDuration);
        isEdit = false;
        if (eMod != null) {
            isEdit = true;
            edtEmployer.setText(eMod.getEmployer());
            edtOccupation.setText(eMod.getOccupation());
            edtAddress.setText(eMod.getAddress());
            edtCity.setText(eMod.getCity());
            edtZipCode.setText(eMod.getZip());
            edtTelephone.setText(eMod.getTelephone());
            edtSupervisor.setText(eMod.getSupervisor());
            edtDuration.setText(eMod.getDuration());
        }

        ((Button) dialogForm.findViewById(R.id.btnUpdateEmpDetails)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams param = new RequestParams();
                param.put("TemporaryAccessCode",
                        MainActivity.user.getTempAccessCode());
                param.put("UserName", MainActivity.user.getUsername());
                param.put("DefId", defId);
                param.put("Employer", edtEmployer.getText().toString());
                param.put("EmployerOccupation", edtOccupation.getText().toString());
                param.put("EmployerAddress", edtAddress.getText().toString());
                param.put("EmployerCity", edtCity.getText().toString());

                if (spState.getSelectedItemPosition() == 0)
                    param.put("EmployerState", "");
                else
                    param.put("EmployerState", spState.getSelectedItem().toString());


                param.put("EmployerZipcode", edtZipCode.getText().toString());
                param.put("EmployerTelephone", edtTelephone.getText().toString());

                param.put("EmployerSupervisor", edtSupervisor.getText().toString());
                param.put("EmployerDuration", edtDuration.getText().toString());

                if (isEdit) {
                    param.put("EmploymentId", eMod.getId());
                } else {
                    param.put("EmploymentId", "");
                }
                updateDefendantEmpDetails(param);
            }
        });
        ArrayList<String> arrState = new ArrayList<>();
        int curr_pos = 0;
        arrState.add("Select State");
        for (int i = 0; i < allState.size(); i++) {
            arrState.add(allState.get(i).getName());
            if (eMod != null) {
                if (allState.get(i).getName().equalsIgnoreCase(eMod.getState())) {
                    curr_pos = i + 1;
                }
            }
        }
        ArrayAdapter<String> adapterHairColor = new ArrayAdapter<String>(_activity, android.R.layout.simple_spinner_item, arrState);
        spState.setAdapter(adapterHairColor);
        spState.setSelection(curr_pos);


        dialog = builder.create();
        dialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // popop
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (resultCode == RESULT_OK) {
            String path = "";
            if (requestCode == ImageSelector.IMAGE_CAPTURE) {
                uri = data.getData();

                if (uri == null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    uri = ImageUtils.getImageUri(THIS, imageBitmap);

                    path = FilePath.getPath(THIS, uri);
                } else {
                    path = FilePath.getPath(THIS, uri);
                }
            }  else {
                uri = data.getData();

                if (uri == null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    uri = ImageUtils.getImageUri(THIS, imageBitmap);
                }
                path = FilePath.getPath(THIS, uri);
                if (uri.toString().contains("com.dropbox")) {
                    if (path.contains(".pdf") || path.contains(".docx")) {
                        path = null;
                        uri = null;
                    }
                }
            }

        }
    }


    public void updateDefendantEmpDetails(RequestParams param) {
        try {
            showProgressDialog("");
            String url = WebAccess.MAIN_URL + WebAccess.ADD_UPDATE_DEFENDENT_EMPLOYMENT_DETAILS;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissProgressDialog();

                    Utils.showDialog(_activity,
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

                        Log.d("Res=", response2);
                        if (resObj != null) {
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {
                                // setCompleteStatus();
                                message = resObj.optString("message");
                                if (!Commons.isEmpty(message)
                                        || message.equalsIgnoreCase("success")) {

                                    Log.d("Res=", message);

                                    Utils.showDialog(THIS, message);

                                    Intent intent = new Intent();
                                    intent.putExtra(Const.RETURN_FLAG, Const.DEFENDANT_BASIC_DETAILS_UPDATED);
                                    setResult(RESULT_OK, intent);
                                    finish();

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
                                startActivity(new Intent(_activity,
                                        Launcher.class));
                            } else {

                                Utils.showDialog(THIS,
                                        resObj.optString("message"));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(THIS, "Error occurs");
                        e.printStackTrace();
                    }


                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void getAllStates() {

        if (Utils.isOnline()) {
            String url = WebAccess.MAIN_URL + WebAccess.GET_ALL_States;
            client.setTimeout(getCallTimeout);
            showProgressDialog("");
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
                        ArrayList<StateModel> insListState = new ArrayList<StateModel>();
                        insListState = WebAccess.getAllStates(response2);

                        if (insListState != null && insListState.size() > 0) {

                            allState = new ArrayList<StateModel>();
                            for (int i = 0; i < insListState.size(); i++) {
                                allState.add(insListState.get(i));
                            }

                        }
                    } else
                        Utils.showDialog(THIS, "Error occurs");
                }
            });

        } else {
            dismissProgressDialog();
            Utils.noInternetDialog(THIS);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();

    }

    void hideKeyboard() {
        View view = THIS.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) THIS
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setActionBar(String title) {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


}
