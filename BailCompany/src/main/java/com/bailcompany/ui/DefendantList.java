package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.adapter.DefendantListAdapter;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.DefendantModel;
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
public class DefendantList extends CustomFragment {


    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;

    int page = 0;
    boolean loadingMore;
    View v;
    RecyclerView mRecyclerView;
    private DefendantListAdapter mAdapter;
    private ArrayList<DefendantModel> defList;
android.widget.SearchView svDefendant;
boolean returnResult=false;
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_defendants, null);

        svDefendant=(android.widget.SearchView)v.findViewById(R.id.svDefendant);
        setHasOptionsMenu(true);
        initViews();
        geDefendantsList();
        search(svDefendant);
        return v;
    }
    private void initViews() {

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_defendant_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
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


                                        Log.d("Data", resObj.toString());

                                        defList = new ArrayList<>();

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
                                                defList.add(objDef);
                                            }

                                        }
                                        mAdapter = new DefendantListAdapter(defList,getActivity(),false);
                                        mRecyclerView.setAdapter(mAdapter);


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
                                                    "Details not avaialble")
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

    public static class F1 extends DialogFragment {

       static Bitmap bitmap;
        public static F1 newInstance(Bitmap img) {
            F1 f1 = new F1();
            f1.setStyle(DialogFragment.STYLE_NO_FRAME,
                    android.R.style.Theme_DeviceDefault_Dialog);
            bitmap=img;
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
