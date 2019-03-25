package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bailcompany.DefendantTracker;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.adapter.DefendantListAdapter;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

@SuppressLint("InflateParams")
public class DefendantList extends CustomFragment implements DefendantListAdapter.DefendantClickListner {


    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;

    int page = 0;
    boolean loadingMore;
    View v;
    RecyclerView mRecyclerView;
    android.widget.SearchView svDefendant;
    boolean returnResult = false;
    String defUserId = "0";
    //   private DefendantListAdapter mAdapter;
    CircularProgressButton btnSaveLoginDetails;
    ImageView btnShareLoginDetail;
    private DefendantListAdapter mAdapter;
    private ArrayList<DefendantModel> defList;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_defendants, null);

        svDefendant = (android.widget.SearchView) v.findViewById(R.id.svDefendant);
        setHasOptionsMenu(true);
        initViews();
        defList = new ArrayList<>();
        mAdapter = new DefendantListAdapter(getActivity(), defList, false, this);
        mRecyclerView.setAdapter(mAdapter);
        geDefendantsList();
        search(svDefendant);

        Button btnTrackDefendant = (Button) v.findViewById(R.id.btnTrackDefendant);
        btnTrackDefendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getActivity(), DefendantTracker.class);
                intent.putExtra("DefList", defList);
                startActivity(intent);
            }
        });

        return v;
    }

    private void initViews() {

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_defendant_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        // mRecyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 36));
    }

    void geDefendantsList() {
        if (Utils.isOnline(getActivity())) {
            if (page == 0)
                showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("Page", page);

            String url = WebAccess.MAIN_URL + WebAccess.GET_DEFENDANT_LIST;
            client.setTimeout(getCallTimeout);
            client.post(getActivity(), url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            if (page == 0)
                                dismissProgressDialog();
                            Utils.showDialog(getActivity(),
                                    R.string.err_unexpect);

                            if (page > 0)
                                page--;
                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                            if (page == 0) {
                                dismissProgressDialog();

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


                                        defList.clear();

                                        JSONArray arrDef = resObj.getJSONArray("defendants");
                                        if (arrDef.length() > 0) {
                                            for (int i = 0; i < arrDef.length(); i++) {
                                                JSONObject defObject = arrDef.getJSONObject(i);
                                                DefendantModel objDef = new DefendantModel();
                                                objDef.setId(defObject.getString("Id"));
                                                objDef.setCompanyId(defObject.getString("CompanyId"));
                                                objDef.setFirstName(defObject.getString("FirstName"));
                                                objDef.setLastName(defObject.getString("LastName"));
                                                objDef.setSSN(defObject.getString("SSN"));
                                                objDef.setDOB(defObject.getString("DOB"));
                                                objDef.setPhoto(defObject.getString("Photo"));
                                                objDef.setUserName(defObject.getString("UserEmail"));
                                                objDef.setPassword(defObject.getString("UserPassword"));
                                                objDef.setDefUserId(defObject.getString("DefUserId"));

                                                defList.add(objDef);
                                            }

                                        }

                                        // refreshing recycler view
                                        mAdapter.notifyDataSetChanged();

                                        //mAdapter = new DefendantListAdapter(defList, getActivity(), false, DefendantList.this);
                                        //  mAdapter = new DefendantListAdapter(defList, getActivity(), false, DefendantList.this);
                                        // mRecyclerView.setAdapter(mAdapter);


                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                getActivity(),
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(getActivity(),
                                                Launcher.class));
                                    } else {

                                        if (page == 0) {
                                            Utils.showDialog(getActivity(),
                                                    "Details not available")
                                                    .show();
                                            mAdapter.notifyDataSetChanged();
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
            Utils.noInternetDialog(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void search(android.widget.SearchView searchView) {
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    public void openDialog(final DefendantModel def) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_defendant_login_details);

        dialog.setTitle("Login Details");


        final EditText edtUserName = (EditText) dialog.findViewById(R.id.edtUserName);
        final EditText edtPassword = (EditText) dialog.findViewById(R.id.edtPassword);
        //  Button btnSaveLogindetail = (Button) dialog.findViewById(R.id.btnSaveLoginDetail);

        btnSaveLoginDetails = (CircularProgressButton) dialog.findViewById(R.id.btnSaveLoginDetail);

        btnShareLoginDetail = (ImageView) dialog.findViewById(R.id.btnShareLoginDetail);

        DefendantModel defModel = getDefendantDetailById(def.getId());

        if (!defModel.getDefUserId().equalsIgnoreCase("")) {

            defUserId = defModel.getDefUserId();

            edtUserName.setText(defModel.getUserName());
            edtPassword.setText(defModel.getPassword());
            btnSaveLoginDetails.setText("Update");
        } else {
            defUserId="0";
            edtUserName.setText(defModel.getFirstName().toLowerCase() + "" + defModel.getId());
            edtPassword.setText("123456");
            btnShareLoginDetail.setVisibility(View.GONE);
        }
        btnSaveLoginDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uploadDocuments();


                addUpdateLoginDetail(edtUserName.getText().toString(), edtUserName.getText().toString(), def.getId(), edtPassword.getText().toString(), defUserId);

            }
        });

        btnShareLoginDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is your Login Details for your Bail Client Mobile Application.\n\nLogin Details: \n";
                shareBody += "UserName : " + edtUserName.getText().toString() + "\n";
                shareBody += "Password : " + edtPassword.getText().toString() + "\n\n\n";
                shareBody += "Download App from the link below, if you dont already have it downloaded.\n\n";
                shareBody += "*you must accept all permissions and for iPhone if prompted accept ALWAYS USE (not just while using app) \n\n\n";
                shareBody += "ANDROID: https://play.google.com/store/apps/details?id=com.baildefendant \n\n";
                shareBody += "IPHONE: https://itunes.apple.com/us/app/Insta-bail-defendant/id1381227797?ls=1&mt=8";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                //https://itunes.apple.com/us/app/Insta-bail-defendant/id1381227797?ls=1&mt=8
            }
        });


        //  TextView textViewUser = (TextView) dialog.findViewById(R.id.textBrand);
        // textViewUser.setText("Hi");
        dialog.show();
    }

    void addUpdateLoginDetail(String username, String email, String defid, String password, String userid) {
        if (Utils.isOnline(getActivity())) {

            btnSaveLoginDetails.startAnimation();

            //  showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("DefId", defid);
            param.put("UserId", userid);
            param.put("DefEmail", email);
            param.put("Password", password);

            String url = WebAccess.MAIN_URL + WebAccess.ADD_UPDATE_DEFENDANT_LOGIN_DETAILS;
            client.setTimeout(getCallTimeout);
            client.post(getActivity(), url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                            // dismissProgressDialog();
                            Utils.showDialog(getActivity(),
                                    R.string.err_unexpect);
                            btnSaveLoginDetails.revertAnimation();

                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                            // dismissProgressDialog();

                            try {
                                String response2;

                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {
                                        // setCompleteStatus();
                                        btnSaveLoginDetails.revertAnimation();
                                        String message = resObj.optString("message");

                                        if (!Commons.isEmpty(message)
                                                || message.equalsIgnoreCase("success")) {

                                            Log.d("Res=", message);
                                            btnShareLoginDetail.setVisibility(View.VISIBLE);
                                            Utils.showDialog(getActivity(), message);
                                            geDefendantsList();
                                            search(svDefendant);

                                        }

                                        //   mAdapter = new DefendantListAdapter(defList, getActivity(), false, DefendantList.this);
                                        //  mRecyclerView.setAdapter(mAdapter);


                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                getActivity(),
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(getActivity(),
                                                Launcher.class));
                                    } else {


                                        Utils.showDialog(getActivity(),
                                                "Details not avaialble")
                                                .show();
                                        mAdapter.notifyDataSetChanged();


                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    });

        } else
            Utils.noInternetDialog(getActivity());
    }

    private DefendantModel getDefendantDetailById(String defId) {

        if (defId == null)
            return null;
        DefendantModel defDetail = null;
        for (DefendantModel d : defList) {
            if (d.getId() != null && d.getId().equalsIgnoreCase(defId)) {
                defDetail = d;
                break;

            }

        }
        return defDetail;
    }



    @Override
    public void onLoginButtonClick(DefendantModel def) {
        openDialog(def);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (btnSaveLoginDetails != null)
            btnSaveLoginDetails.dispose();
    }

    public static class F1 extends DialogFragment {

        static Bitmap bitmap;

        public static F1 newInstance(Bitmap img) {
            F1 f1 = new F1();
            f1.setStyle(DialogFragment.STYLE_NO_FRAME,
                    android.R.style.Theme_DeviceDefault_Dialog);
            bitmap = img;
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

            if (bitmap != null) {

                bm.setImageBitmap(bitmap);
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


}
