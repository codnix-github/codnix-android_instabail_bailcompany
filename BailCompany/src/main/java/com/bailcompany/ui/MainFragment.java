package com.bailcompany.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.AgentProfile;
import com.bailcompany.FindBestAgent;
import com.bailcompany.Login;
import com.bailcompany.MainActivity;
import com.bailcompany.R;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.AgentModel;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static com.bailcompany.custom.CustomActivity.THIS;

// TODO: Auto-generated Javadoc

/**
 * The Class MainFragment is the base fragment that shows the Google Map. You
 * can add your code to do whatever you want related to Map functions for your
 * app. For example you can add Map markers here or can show places on map.
 */
@SuppressLint("InflateParams")
public class MainFragment extends CustomFragment implements OnMapReadyCallback {

    /**
     * The map view.
     */
    private MapView mMapView;

    /**
     * The Google map.
     */
    private GoogleMap mMap;
    private TextView txtLocation;
    public static String agentRequestId;
    AgentModel agModel;
    int i;
    String response2;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    String message;
    JSONObject jsonObj;
    String key;
    Marker opt;
    static int getCallTimeout = 50000;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_container, null);
        setHasOptionsMenu(true);

        if (getArguments() != null)
            agModel = (AgentModel) getArguments().getSerializable("agent");

        Log.d("Agent", agModel == null ? "NULL" : "NOT NULL");

        agentRequestId = FindBestAgent.agentRequestId;
        setTouchNClick(v.findViewById(R.id.btnCheckin));
        setTouchNClick(v.findViewById(R.id.btnCall));
        txtLocation = (TextView) v.findViewById(R.id.txt_location);
        if (!Commons.isEmpty(GetAnAgent.location_entered))
            txtLocation.setText(GetAnAgent.location_entered);

        try {
            initMap(v, savedInstanceState);
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initButtons(v);

        return v;
    }

    /**
     * Initialize the buttons and set the Touch and Click listner for each
     * button of this view. You can add or change the buttons here.
     *
     * @param v the v
     */
    private void initButtons(View v) {
        View b = v.findViewById(R.id.btnMessage);
        b.setOnTouchListener(CustomActivity.TOUCH);
        b.setOnClickListener(this);

        b = v.findViewById(R.id.btnCall);
        b.setOnTouchListener(CustomActivity.TOUCH);
        b.setOnClickListener(this);

        b = v.findViewById(R.id.btnCheckin);
        b.setOnTouchListener(CustomActivity.TOUCH);
        b.setOnClickListener(this);
    }

    /**
     * Initialize the Map view.
     *
     * @param v                  the v
     * @param savedInstanceState the saved instance state object passed from OnCreateView
     *                           method of fragment.
     */
    private void initMap(View v, Bundle savedInstanceState)
            throws GooglePlayServicesNotAvailableException {
        MapsInitializer.initialize(getActivity());
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

    }

    /**
     * This method can be used to show the markers on the map. Current
     * implementation of this method will show only a single Pin with title and
     * snippet. You must customize this method to show the pins as per your
     * need.
     */
    private void setupMapMarkers() {

        mMap.clear();

        double latt = Utils.isNumeric(agModel.getLatitude()) ? Double
                .parseDouble(agModel.getLatitude()) : 0;
        double longt = Utils.isNumeric(agModel.getLongitude()) ? Double
                .parseDouble(agModel.getLongitude()) : 0;
        LatLng llAgent = new LatLng(latt, longt);
        View convertView = getActivity().getLayoutInflater().inflate(
                R.layout.popup, null);
        ((TextView) convertView.findViewById(R.id.title)).setText(""
                + agModel.getAgentName());
        Bitmap bm = createDrawableFromView(getActivity(), convertView);

        MarkerOptions a1 = new MarkerOptions().position(llAgent);
        a1.anchor(0.5f, 1.0f);
        a1.icon(BitmapDescriptorFactory.fromBitmap(bm));
        opt = mMap.addMarker(a1);
        opt.setPosition(llAgent);
        opt.setTitle("");
        opt.setSnippet("" + agModel.getAgentName());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(llAgent, 17));
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet().equalsIgnoreCase(
                        "" + agModel.getAgentName())) {
                    getAgentDetail(agModel.getAgentId());
                }
                return false;
            }
        });

    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        // ((Activity) context).getWindowManager().getDefaultDisplay()
        // .getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }

    /**
     * This class creates a Custom a InfoWindowAdapter that is used to show
     * popup on map when user taps on a pin on the map. Current implementation
     * of this class will show a Title and a snippet.
     */

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            // mMap.setOnInfoWindowClickListener(this);
            // mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            setupMapMarkers();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onPause()
     */
    @Override
    public void onPause() {

        super.onPause();
        mMapView.onPause();
        if (mMap != null)
            mMap.setInfoWindowAdapter(null);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v == v.findViewById(R.id.btnCheckin)) {
            submitRequestAgent();
        }
        if (v.getId() == R.id.btnCall) {
            Utils.makeCall(getActivity(), "+1" + agModel.getPhone());

        }
        if (v.getId() == R.id.btnMessage)
            Utils.sendMessage(getActivity(), "+1" + agModel.getPhone());

    }

    void submitRequestAgent() {
        showProgressDialog("");
        RequestParams param = new RequestParams();
        if (agentRequestId != null && WebAccess.params == null) {
            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("AgentId", agModel.getAgentId());
            param.put("RequestId", agentRequestId);
        } else {
            if (WebAccess.params != null)
                param = WebAccess.params;

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("AgentId", agModel.getAgentId());
            param.put("RequestId", agentRequestId);
        }
        String url = WebAccess.MAIN_URL + WebAccess.SEND_REQUEST_AGENT;
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
                    WebAccess.params = null;
                    response2 = new String(responseBody);
                    if (response2 != null) {
                        JSONObject json = new JSONObject(response2);
                        // Toast.makeText(getActivity(),
                        // json.optString("message"),Toast.LENGTH_LONG).show();
                        FindBestAgent.isRequestedAgent = true;
                        showDialog(getActivity(), json.optString("message"));

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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
                WebAccess.agentHire = true;
                dialog.dismiss();
                getActivity().finish();
                // getFragmentManager().popBackStack();
            }
        });

    }

    public static AlertDialog showDialog(Context ctx, String msg,
                                         DialogInterface.OnClickListener listener) {

        return showDialog(ctx, msg, ctx.getString(android.R.string.ok), null,
                listener, null);
    }

    public static AlertDialog showDialog(Context ctx, String msg, String btn1,
                                         String btn2, DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(btn1, listener1);
        if (btn2 != null && listener2 != null)
            builder.setNegativeButton(btn2, listener2);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;

    }

    private void getAgentDetail(String id) {
        showProgressDialog("");
        RequestParams param = new RequestParams();

        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("agentid", id);
        String url = WebAccess.MAIN_URL + WebAccess.GET_AGENT_DETAIL;
        client.setTimeout(getCallTimeout);

        client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
                Utils.showDialog(getActivity(), R.string.err_unexpect);
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
                                startActivity(new Intent(getActivity(),
                                        AgentProfile.class).putExtra("agent",
                                        model));
                        } else if (resObj.optString("status").equalsIgnoreCase(
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
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(getActivity(), R.string.err_unexpect);
                    e.printStackTrace();
                }

            }

        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(THIS,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Utility.checkPermission(THIS);
            } else {
                mMap.setMyLocationEnabled(true);
                setupMapMarkers();

            }

        }
    }
}
