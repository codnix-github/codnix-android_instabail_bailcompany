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
import com.bailcompany.model.DefendantVehicleModel;
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
public class DefendantVehicleDetails extends CustomActivity {


    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;

    String message;
    JSONObject jsonObj;

    DefendantModel defModel;
    String defId;
    ArrayList<DefendantVehicleModel> arrDefVehicleDtl;
    LinearLayout llVehicleDetails;
    ArrayList<StateModel> allState = null;
    boolean isEdit = false;

    private Context _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendant_vehicle);
        setActionBar(getString(R.string.title_activity_defendant_vehicle_detail));
        _activity = DefendantVehicleDetails.this;
        llVehicleDetails = (LinearLayout) findViewById(R.id.llVehicleDetails);
        ((Button) findViewById(R.id.btnAddVehicletDtl)).setOnClickListener(new View.OnClickListener() {
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
                arrDefVehicleDtl = defModel.getVehicleDtl();
                showDetails();
            }
        }
        getAllStates();
    }

    public void showDetails() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final DefendantVehicleModel eMod : arrDefVehicleDtl) {
            View v = inflater.inflate(R.layout.row_defendant_vehicle_details, null, false);
            ((TextView) v.findViewById(R.id.tvMake)).setText(eMod.getMake());
            ((TextView) v.findViewById(R.id.tvModel)).setText(eMod.getModel());
            ((TextView) v.findViewById(R.id.tvYear)).setText(eMod.getYear());
            ((TextView) v.findViewById(R.id.tvColor)).setText(eMod.getColor());
            ((TextView) v.findViewById(R.id.tvState)).setText(eMod.getState());
            ((TextView) v.findViewById(R.id.tvRegistration)).setText(eMod.getRegistration());

            ((ImageView) v.findViewById(R.id.edtVehicleDetails)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEditDialog(eMod);
                }
            });

            llVehicleDetails.addView(v);
        }

    }

    public void openEditDialog(final DefendantVehicleModel eMod) {
        AlertDialog dialog;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogForm = inflater.inflate(R.layout.dialog_edit_defendant_vehicle_details, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(DefendantVehicleDetails.this);
        builder.setView(dialogForm);
        builder.create();

        final Spinner spState = (Spinner) dialogForm.findViewById(R.id.spState);
        final EditText edtYear = (EditText) dialogForm.findViewById(R.id.edtYear);
        final EditText edtMake = (EditText) dialogForm.findViewById(R.id.edtMake);
        final EditText edtModel = (EditText) dialogForm.findViewById(R.id.edtModel);
        final EditText edtColor = (EditText) dialogForm.findViewById(R.id.edtColor);
        final EditText edtRegistration = (EditText) dialogForm.findViewById(R.id.edtRegistration);

        isEdit = false;
        if (eMod != null) {
            isEdit = true;
            edtYear.setText(eMod.getYear());
            edtMake.setText(eMod.getMake());
            edtModel.setText(eMod.getModel());
            edtColor.setText(eMod.getColor());
            edtRegistration.setText(eMod.getRegistration());

        }

        ((Button) dialogForm.findViewById(R.id.btnUpdateVehicleDetails)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams param = new RequestParams();
                param.put("TemporaryAccessCode",
                        MainActivity.user.getTempAccessCode());
                param.put("UserName", MainActivity.user.getUsername());
                param.put("DefId", defId);
                param.put("VehicleYear", edtYear.getText().toString());
                param.put("VehicleMake", edtMake.getText().toString());
                param.put("VehicleModel", edtModel.getText().toString());
                param.put("VehicleColor", edtColor.getText().toString());
                param.put("VehicleRegistration", edtRegistration.getText().toString());

                if (spState.getSelectedItemPosition() == 0)
                    param.put("VehicleState", "");
                else
                    param.put("VehicleState", spState.getSelectedItem().toString());

                if (isEdit) {
                    param.put("VehicleId", eMod.getId());
                } else {
                    param.put("VehicleId", "");
                }
                updateDefendantVehicleDetails(param);
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
            }else {
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


    public void updateDefendantVehicleDetails(RequestParams param) {
        try {
            showProgressDialog("");
            String url = WebAccess.MAIN_URL + WebAccess.ADD_UPDATE_DEFENDENT_VEHICLE_DETAILS;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    dismissProgressDialog();

                    Utils.showDialog(THIS,
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
