package com.bailcompany;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bailcompany.adapter.DefendantListAdapter;
import com.bailcompany.custom.CustomActivity;
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
public class DefendantSelectionActivity extends CustomActivity {


    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;

    int page = 0;
    boolean loadingMore;

    RecyclerView mRecyclerView;
    android.widget.SearchView svDefendant;
    private DefendantListAdapter mAdapter;
    private ArrayList<DefendantModel> defList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendants);
        setActionBar();
        svDefendant = (android.widget.SearchView) findViewById(R.id.svDefendant);
        Button btnTrackDefendant = (Button) findViewById(R.id.btnTrackDefendant);
        btnTrackDefendant.setVisibility(View.GONE);

        initViews();
        geDefendantsList();
        search(svDefendant);

    }


    protected void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.title_activity_select_defendant));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
    private void initViews() {


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_defendant_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(THIS);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    void geDefendantsList() {
        if (Utils.isOnline(THIS)) {
            if (page == 0)
                showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("Page", page);

            String url = WebAccess.MAIN_URL + WebAccess.GET_DEFENDANT_LIST;
            client.setTimeout(getCallTimeout);
            client.post(THIS, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            if (page == 0)
                                dismissProgressDialog();
                            Utils.showDialog(THIS,
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
                                        mAdapter = new DefendantListAdapter(THIS, defList, true, null);
                                        mRecyclerView.setAdapter(mAdapter);


                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                THIS,
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(THIS,
                                                Launcher.class));
                                    } else {

                                        if (page == 0) {
                                            Utils.showDialog(THIS,
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
            Utils.noInternetDialog(THIS);
    }


    @Override
    public void onResume() {
        super.onResume();

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

}
