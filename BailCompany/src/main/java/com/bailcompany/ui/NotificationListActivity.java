package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bailcompany.HistoryRequestDetail;
import com.bailcompany.HistoryRequestList;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.adapter.NotificationListAdapter;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.NotificationModel;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class NotificationListActivity extends CustomFragment implements NotificationListAdapter.NotificationClickListner {


    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;
    String message;
    JSONObject jsonObj;
    RecyclerView mRecyclerView;
    NotificationListAdapter mAdapter;
    Activity THIS;
    View v;
    int page = 0;
    boolean loadingMore;
    private Context _activity;
    private ArrayList<NotificationModel> notificationList;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_notifications_list, null);
        mRecyclerView = v.findViewById(R.id.rv_notification_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        setHasOptionsMenu(true);
        THIS = getActivity();
        notificationList = new ArrayList<>();
        mAdapter = new NotificationListAdapter(getActivity(), mRecyclerView, notificationList, false, this);

        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnLoadMoreListener(new NotificationListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (loadingMore) {
                    page++;
                    notificationList.add(null);
                    mAdapter.notifyItemInserted(notificationList.size() - 1);

                    getNotificationList();
                }
            }
        });

        getNotificationList();
        return v;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

    }

    void getNotificationList() {
        if (Utils.isOnline(getActivity())) {
            if (page == 0) {
                showProgressDialog("");
                notificationList.clear();
            }
            loadingMore = true;

            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("ReceiverId", MainActivity.user.getCompanyId());
            param.put("Page", page);

            String url = WebAccess.MAIN_URL + WebAccess.GET_NOTIFICATION_LIST;
            client.setTimeout(getCallTimeout);
            client.post(THIS, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            dismissProgressDialog();
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
                                loadingMore = true;
                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {

                                        if (page != 0) {
                                            notificationList.remove(notificationList.size() - 1);
                                            mAdapter.notifyItemRemoved(notificationList.size());

                                        }

                                        // notificationList.clear();
                                        int previousLength = notificationList.size();
                                        JSONArray arrDef = resObj.getJSONArray("notification_list");
                                        if (arrDef.length() > 0) {
                                            for (int i = 0; i < arrDef.length(); i++) {
                                                JSONObject defObject = arrDef.getJSONObject(i);
                                                NotificationModel objDef = new NotificationModel();
                                                objDef.setNotificationId(defObject.getString("NotificationId"));
                                                objDef.setSenderId(defObject.getString("SenderId"));
                                                objDef.setReceiverId(defObject.getString("ReceiverId"));
                                                objDef.setRequestId(defObject.getString("RequestId"));
                                                objDef.setIsRead(defObject.getString("IsRead"));
                                                objDef.setMessage(defObject.getString("Message"));
                                                // objDef.setPhoto(defObject.getString("Photo"));
                                                objDef.setCreatedAt(defObject.getString("CreatedAt"));
                                                objDef.setType(defObject.getString("Type"));
                                                objDef.setToastShown(defObject.getString("ToastShown"));
                                                objDef.setIs_Seen(defObject.getString("Is_Seen"));
                                                objDef.setTop_bar_click(defObject.getString("top_bar_click"));
                                                //objDef.setName(defObject.getString("Name"));

                                                notificationList.add(objDef);
                                            }

                                        }

                                        if (previousLength == notificationList.size()) {
                                            loadingMore = false;
                                        }

                                        if (notificationList != null
                                                && notificationList.size() > 0) {
                                            mAdapter.notifyDataSetChanged();
                                            mAdapter.setLoaded();
                                        } else {
                                            loadingMore = false;

                                            mAdapter.notifyDataSetChanged();
                                        }

                                        // refreshing recycler view
                                        /// mAdapter.notifyDataSetChanged();


                                        //mAdapter = new DefendantListAdapter(defList, getActivity(), false, DefendantList.this);
                                        //  mAdapter = new DefendantListAdapter(defList, getActivity(), false, DefendantList.this);
                                        // mRecyclerView.setAdapter(mAdapter);


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


                                        Utils.showDialog(THIS,
                                                "Details not available")
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
            Utils.noInternetDialog(THIS);
    }

    private void updateAdapter() {
        mAdapter = new NotificationListAdapter(getActivity(), mRecyclerView, notificationList, false, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onNotificationClick(NotificationModel notDetails) {


        String type = notDetails.getType();

        if (type.equalsIgnoreCase("106") || type.equalsIgnoreCase("107") || type.equalsIgnoreCase("108")) {
            Intent intent = new Intent(THIS, Defendant.class);
            intent.putExtra("defId", notDetails.getSenderId());
            THIS.startActivity(intent);
        } else {
            getBailRequestDetail(notDetails.getRequestId());
        }
        //

        // Calling Request Page

    }

    public void getBailRequestDetail(String reqId) {
        if (Utils.isOnline()) {
            showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("RequestId", reqId);
            param.put("UserName", MainActivity.user.getUsername());
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            String url = WebAccess.MAIN_URL + WebAccess.GET_REQUEST_DETAIL;
            client.setTimeout(getCallTimeout);
            client.post(THIS, url, param, new AsyncHttpResponseHandler() {

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
                        JSONObject resObj = new JSONObject(response2);
                        if (resObj != null) {
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {
                                JSONObject dObj;

                                dObj = resObj.getJSONObject("request_details");

                                //  mod = WebAccess.parseBailRequestDetail(dObj);
                                HistoryRequestList.IsBailRequest = true;
                                startActivity(new Intent(THIS,
                                        HistoryRequestDetail.class)
                                        .putExtra("bail", WebAccess.parseBailRequestDetail(dObj))
                                        .putExtra("title", "All Posting Agent Transactions"));


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
                                        THIS, Launcher.class));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(THIS,
                                R.string.err_unexpect);
                        e.printStackTrace();
                    }
                }

            });

        } else {
            Utils.noInternetDialog(THIS);

        }

    }
}
