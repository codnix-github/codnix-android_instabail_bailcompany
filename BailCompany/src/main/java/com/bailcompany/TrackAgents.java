package com.bailcompany;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.custom.LocationAdapter;
import com.bailcompany.custom.LocationImpl;
import com.bailcompany.model.AgentModel;
import com.bailcompany.model.BailRequestModel;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("InflateParams")
public class TrackAgents extends CustomActivity implements OnMapReadyCallback {

    /**
     * The map view.
     */
    private MapView mMapView;
    public static PendingIntent pendingIntent;
    /**
     * The Google map.
     */
    private static GoogleMap mMap;
    public static int i = 0;
    public static AgentModel agModel, oldAgMoodel;
    BroadcastReceiver receiver;
    static int getCallTimeout = 50000;
    static String res;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    // static MarkerOptions opt = new MarkerOptions();
    static float zoomPostion = 16;
    String defendantId = "0";
    public static boolean IsBailRequest;
    String Latitude = "", Longitude = "";
    String oldLatitude = "", oldLongitude = "";
    ArrayList<String> latit = new ArrayList<String>();
    ArrayList<String> longit = new ArrayList<String>();
    Marker opt, opt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_hired_agent);

        // opt = new MarkerOptions();
        // opt2 = new MarkerOptions();
        //
        // latit.add("38.08958");
        // latit.add("38.089445");
        // latit.add("38.089445");
        // latit.add("38.089242");
        // latit.add("38.089242");
        // latit.add("38.089175");
        // latit.add("38.088027");
        // latit.add("38.08573");
        // latit.add("38.083027");
        // latit.add("38.081879");
        // latit.add("38.081879");
        // longit.add("-88.536329");
        // longit.add("-88.539848");
        // longit.add("-88.543882");
        // longit.add("-88.547401");
        // longit.add("-88.550835");
        // longit.add("-88.554354");
        // longit.add("-88.556671");
        // longit.add("-88.558903");
        // longit.add("-88.561478");
        // longit.add("-88.565083");
        // longit.add("-88.569031");

        IsBailRequest = true;
        agModel = (AgentModel) getIntent().getSerializableExtra("agent");
        defendantId = getIntent().getExtras().getString("defendant", "0");
        setActionBar();
        try {
            initMap(savedInstanceState);
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // runTime = (TextView)findViewById(R.id.runtime);

        // runTime.setText(Html.fromHtml("<font color=#F5F5F4>Run Time : </font><font color=#C9B445>01hr : 10 min"));
        setTouchNClick(R.id.btnCall);
        setTouchNClick(R.id.btnMessage);
        getAgentLocation();
        // setAlarmUpdate();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnCall) {
            Utils.makeCall(THIS, "+1" + agModel.getPhone());
            // CallDialog.show(THIS);
        } else if (v.getId() == R.id.btnMessage)
            Utils.sendMessage(THIS, "+1" + agModel.getPhone());
    }

    private void initMap(Bundle savedInstanceState)
            throws GooglePlayServicesNotAvailableException {
        MapsInitializer.initialize(THIS);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

    }

    private void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.trck_agnt));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    /**
     * This method can be used to show the markers on the map. Current
     * implementation of this method will show only a single Pin with title and
     * snippet. You must customize this method to show the pins as per your
     * need.
     */
    public void setupMapMarkers() {
        // MarkerOptions opt = new MarkerOptions();
        // MarkerOptions opt2 = new MarkerOptions();
        mMap.clear();
        double loclatt = Utils.isNumeric(agModel.getLocationLatitude()) ? Double
                .parseDouble(agModel.getLocationLatitude()) : 0;
        double loclongt = Utils.isNumeric(agModel.getLocationLongitude()) ? Double
                .parseDouble(agModel.getLocationLongitude()) : 0;
        LatLng llAgent = new LatLng(loclatt, loclongt);

        View convertView = TrackAgents.this.getLayoutInflater().inflate(
                R.layout.popup, null);
        ((TextView) convertView.findViewById(R.id.title)).setText("Defendant");
        Bitmap bm = createDrawableFromView(this, convertView);

        MarkerOptions a1 = new MarkerOptions().position(llAgent);
        a1.anchor(0.5f, 1.0f);
        a1.icon(BitmapDescriptorFactory.fromBitmap(bm));
        opt = mMap.addMarker(a1);
        opt.setPosition(llAgent);
        opt.setTitle("");
        opt.setSnippet("Defendant");

        // opt.icon(BitmapDescriptorFactory.fromBitmap(bm));
        // opt.position(llAgent).title("").snippet("Defendant");
        // mMap.addMarker(opt);

        double latt = Utils.isNumeric(agModel.getLatitude()) ? Double
                .parseDouble(agModel.getLatitude()) : 0;
        double longt = Utils.isNumeric(agModel.getLongitude()) ? Double
                .parseDouble(agModel.getLongitude()) : 0;

        LatLng l = new LatLng(latt, longt);

        View convertView2 = TrackAgents.this.getLayoutInflater().inflate(
                R.layout.popup, null);
        ((TextView) convertView2.findViewById(R.id.title)).setText(""
                + agModel.getAgentName());
        Bitmap bm2 = createDrawableFromView(this, convertView2);

        // opt2.icon(BitmapDescriptorFactory.fromBitmap(bm2));
        // opt2.position(l).title("").snippet("" + agModel.getAgentName());
        // mMap.addMarker(opt2);
        MarkerOptions a2 = new MarkerOptions().position(l);
        a2.anchor(0.5f, 1.0f);
        a2.icon(BitmapDescriptorFactory.fromBitmap(bm2));
        opt2 = mMap.addMarker(a2);
        opt2.setPosition(l);
        opt2.setTitle("");
        opt2.setSnippet(agModel.getAgentName());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 17));

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub

                if (marker.getSnippet().equalsIgnoreCase("Defendant")) {
                    getBailRequestDetail();
                } else {
                    // startActivity(new Intent(THIS, AgentProfile.class)
                    // .putExtra("agent", agModel));
                    getAgentDetail(agModel.getAgentId());
                }
                return false;
            }
        });
    }

    public void updateMapMarker() {
        // MarkerOptions opt = new MarkerOptions();
        // MarkerOptions opt2 = new MarkerOptions();
        CameraPosition cp = mMap.getCameraPosition();

        zoomPostion = cp.zoom;
        if (zoomPostion == 0.0 || zoomPostion == 0)
            zoomPostion = 17;
        // mMap.clear();
        double loclatt = Utils.isNumeric(agModel.getLocationLatitude()) ? Double
                .parseDouble(agModel.getLocationLatitude()) : 0;
        double loclongt = Utils.isNumeric(agModel.getLocationLongitude()) ? Double
                .parseDouble(agModel.getLocationLongitude()) : 0;
        LatLng llAgent = new LatLng(loclatt, loclongt);
        opt.setPosition(llAgent);
        // opt.position(llAgent).title("").snippet("Defendant");
        // View convertView = TrackAgents.this.getLayoutInflater().inflate(
        // R.layout.popup, null);
        // ((TextView)
        // convertView.findViewById(R.id.title)).setText("Defendant");
        // Bitmap bm = createDrawableFromView(this, convertView);
        // opt.icon(BitmapDescriptorFactory.fromBitmap(bm));
        // mMap.addMarker(opt);
        double latt = Utils.isNumeric(agModel.getLatitude()) ? Double
                .parseDouble(agModel.getLatitude()) : 0;
        double longt = Utils.isNumeric(agModel.getLongitude()) ? Double
                .parseDouble(agModel.getLongitude()) : 0;
        LatLng l = new LatLng(latt, longt);
        opt2.setPosition(l);

        //
        // View convertView2 = TrackAgents.this.getLayoutInflater().inflate(
        // R.layout.popup, null);
        // ((TextView) convertView2.findViewById(R.id.title)).setText(""
        // + agModel.getAgentName());
        // Bitmap bm2 = createDrawableFromView(this, convertView2);
        // opt2.icon(BitmapDescriptorFactory.fromBitmap(bm2));
        // opt2.position(l).title("").snippet("" + agModel.getAgentName());
        //
        // mMap.addMarker(opt2);
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 16));
        double oldLatt = Utils.isNumeric(oldLatitude) ? Double
                .parseDouble(oldLatitude) : 0;
        double oldLongt = Utils.isNumeric(oldLongitude) ? Double
                .parseDouble(oldLongitude) : 0;
        double bearing = bearing(oldLatt, oldLongt, l.latitude, l.longitude);
        double s = 0;
        float f = (float) bearing;
        s = LocationImpl.getDistanceMeter2(oldLatt, oldLongt, latt, longt);
        CameraPosition position;
        if (s > 4) {
            position = CameraPosition.builder().bearing(f).target(l).zoom(17)
                    .tilt(mMap.getCameraPosition().tilt).build();

        } else {
            position = CameraPosition.builder().target(l).zoom(17)
                    .tilt(mMap.getCameraPosition().tilt).build();
        }
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        //
        // mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
        //
        // @Override
        // public boolean onMarkerClick(Marker marker) {
        // if (marker.getSnippet().equalsIgnoreCase("Defendant")) {
        // getBailRequestDetail();
        // } else {
        // // startActivity(new Intent(THIS, AgentProfile.class)
        // // .putExtra("agent", agModel));
        // getAgentDetail(agModel.getAgentId());
        // }
        // return false;
        // }
        // });
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


    // public void call_activity() {
    // mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
    //
    // @Override
    // public void onInfoWindowClick(Marker arg0) {
    //
    // Log.d("track agent user", agModel.getUsername() + "==="
    // + agModel.getEmail());
    // startActivity(new Intent(THIS, AgentProfile.class).putExtra(
    // "agent", agModel));
    //
    // }
    // });
    // }

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

    protected static double bearing(double lat1, double lon1, double lat2,
                                    double lon2) {
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2)
                - Math.sin(latitude1) * Math.cos(latitude2)
                * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    // private class CustomInfoWindowAdapter implements InfoWindowAdapter {
    //
    // /** The contents view. */
    // private final View mContents;
    //
    // /**
    // * Instantiates a new custom info window adapter.
    // */
    // CustomInfoWindowAdapter() {
    //
    // mContents = getLayoutInflater().inflate(R.layout.map_popup, null);
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // * com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoWindow
    // * (com.google.android.gms.maps.model.Marker)
    // */
    // @Override
    // public View getInfoWindow(Marker marker) {
    //
    // render(marker, mContents);
    // return mContents;
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // * com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoContents
    // * (com.google.android.gms.maps.model.Marker)
    // */
    // @Override
    // public View getInfoContents(Marker marker) {
    //
    // return null;
    // }
    //
    // /**
    // * Render the marker content on Popup view. Customize this as per your
    // * need.
    // *
    // * @param marker
    // * the marker
    // * @param view
    // * the content view
    // */
    // private void render(final Marker marker, View view) {
    //
    // String title = marker.getTitle();
    // TextView titleUi = (TextView) view.findViewById(R.id.title);
    // if (title != null) {
    // SpannableString titleText = new SpannableString(title);
    // titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
    // titleText.length(), 0);
    // titleUi.setText(titleText);
    // } else {
    // titleUi.setText("");
    // }
    //
    // String snippet = marker.getSnippet();
    // TextView snippetUi = (TextView) view.findViewById(R.id.snippet);
    // if (snippet != null) {
    // SpannableString snippetText = new SpannableString(snippet);
    // snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
    // snippet.length(), 0);
    // snippetUi.setText(snippetText);
    // } else {
    // snippetUi.setText("");
    // }
    // }
    // }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        com.bailcompany.utils.Log.e("updated location", agModel.getLatitude()
                + "," + agModel.getLongitude());
      //  mMap = mMapView.getMap();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            // mMap.setOnInfoWindowClickListener(this);
            // mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            setupMapMarkers();

        }
        // mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
        //
        // @Override
        // public void onInfoWindowClick(Marker arg0) {
        // if (arg0.getTitle().toString().equalsIgnoreCase("Defendant"))
        // // startActivity(new Intent(getActivity(),
        // // HistoryRequestDetail.class).putExtra("bail",
        // // bailReqList.get(position)).putExtra("title",
        // // "All Posting Agent Transactions"));
        // Utils.showDialog(TrackAgents.this, "nothing");
        // else
        // startActivity(new Intent(TrackAgents.this,
        // AgentProfile.class).putExtra("agent", agModel));
        //
        // }
        // });
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onPause()
     */
    @Override
    public void onPause() {

        mMapView.onPause();
        if (mMap != null)
            mMap.setInfoWindowAdapter(null);
        super.onPause();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        stopUpdatingLocation();
        super.onDestroy();
        IsBailRequest = false;
    }

    public void stopUpdatingLocation() {
        if (pendingIntent != null) {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }

        LocationImpl.removeListener(new LocationAdapter() {

            @Override
            public void onLocationChange(Location location) {
                // TODO Auto-generated method stub

            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAgentLocation() {
        Calendar calendar = Calendar.getInstance();
        Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime()
                .getTime());
        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("agentid", TrackAgents.agModel.getAgentId());
        param.put("timestamp", currentTimestamp.toString());

        String url = WebAccess.MAIN_URL + WebAccess.GET_AGENT_LOCATION;
        client.setTimeout(getCallTimeout);
        client.post(TrackAgents.THIS, url, param,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getAgentLocation();
                            }
                        }, 500);

                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                        String response2;
                        response2 = new String(responseBody);
                        JSONObject json;
                        try {
                            json = new JSONObject(response2);

                            if (json.optString("status").equalsIgnoreCase("1")) {
                                JSONObject obj = json
                                        .getJSONObject("agent_details");
                                // Calendar calendar = Calendar.getInstance();
                                // Timestamp currentTimestamp = new
                                // java.sql.Timestamp(
                                // calendar.getTime().getTime());
                                // generateNoteOnSD("BailCompanyLog",
                                // currentTimestamp.toString() + "--long:"
                                // + Longitude + "/lat:"
                                // + Latitude, TrackAgents.THIS);
                                // if (i < 11) {
                                //
                                // // Latitude = obj.optString("Latitude");
                                // // Longitude = obj.optString("Longitude");
                                // Latitude = latit.get(i);
                                // Longitude = longit.get(i);
                                // // Latitude = "31.4726";
                                // // Longitude = "74.2959";
                                // i++;
                                // } else {
                                // i = 0;
                                // Latitude = latit.get(i);
                                // Longitude = longit.get(i);
                                // }
                                // oldLatitude = agModel.getLatitude();
                                // oldLongitude = agModel.getLongitude();
                                // agModel.setLatitude(Latitude);
                                // agModel.setLongitude(Longitude);
                                // updateMapMarker();
                                oldLatitude = agModel.getLatitude();
                                oldLongitude = agModel.getLongitude();
                                Latitude = obj.optString("Latitude");
                                Longitude = obj.optString("Longitude");
                                // Latitude = "31.48361321";
                                // Longitude = "74.28843839";
                                // agModel.setLatitude(Latitude);
                                // agModel.setLongitude(Longitude);
                                // updateMapMarker();

                                if (!Latitude.equalsIgnoreCase(oldLatitude)
                                        && !Longitude
                                        .equalsIgnoreCase(oldLongitude)) {
                                    agModel.setLatitude(Latitude);
                                    agModel.setLongitude(Longitude);
                                    // Toast.makeText(THIS, "Updated",
                                    // Toast.LENGTH_SHORT).show();
                                    updateMapMarker();
                                }

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAgentLocation();
                                    }
                                }, 500);

                            } else if (json.optString("status")
                                    .equalsIgnoreCase("3")) {
                                Toast.makeText(
                                        THIS,
                                        "Session was closed please login again",
                                        Toast.LENGTH_LONG).show();
                                MainActivity.sp.edit().putBoolean("isFbLogin",
                                        false);
                                MainActivity.sp.edit().putString("user", null)
                                        .commit();
                                startActivity(new Intent(TrackAgents.this,
                                        Launcher.class));
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                });

    }

    public static void generateNoteOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(),
                    "BailCompanyLog");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append("\n" + sBody);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void getBailRequestDetail() {

        if (!defendantId.equalsIgnoreCase("0")) {
            if (Utils.isOnline()) {
                showProgressDialog("");
                RequestParams param = new RequestParams();

                param.put("RequestId", defendantId);
                param.put("UserName", MainActivity.user.getUsername());
                param.put("TemporaryAccessCode",
                        MainActivity.user.getTempAccessCode());
                String url = WebAccess.MAIN_URL + WebAccess.GET_REQUEST_DETAIL;
                client.setTimeout(getCallTimeout);
                client.post(this, url, param, new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        dismissProgressDialog();
                        Utils.showDialog(TrackAgents.this,
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

                                    dObj = resObj
                                            .getJSONObject("request_details");

                                    BailRequestModel mod = WebAccess
                                            .parseBailRequestDetail(dObj);
                                    startActivity(new Intent(TrackAgents.this,
                                            HistoryRequestDetail.class)
                                            .putExtra("bail", mod)
                                            .putExtra("title",
                                                    "All Posting Agent Transactions"));
                                } else if (resObj.optString("status")
                                        .equalsIgnoreCase("3")) {
                                    Toast.makeText(
                                            THIS,
                                            "Session was closed please login again",
                                            Toast.LENGTH_LONG).show();
                                    MainActivity.sp.edit().putBoolean(
                                            "isFbLogin", false);
                                    MainActivity.sp.edit()
                                            .putString("user", null).commit();
                                    startActivity(new Intent(TrackAgents.this,
                                            Launcher.class));
                                } else {
                                    Utils.showDialog(THIS,
                                            resObj.optString("message"));
                                }
                            }
                        } catch (JSONException e) {
                            Utils.showDialog(TrackAgents.this,
                                    R.string.err_unexpect);
                            e.printStackTrace();
                        }
                    }

                });

            } else {
                Utils.noInternetDialog(THIS);

            }
        } else {
            Utils.showDialog(this, "Sorry,no Defendant record found");
        }
    }

    private void getAgentDetail(String id) {
        showProgressDialog("");
        RequestParams param = new RequestParams();

        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("agentid", id);
        String url = WebAccess.MAIN_URL + WebAccess.GET_AGENT_DETAIL;
        client.setTimeout(getCallTimeout);

        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                dismissProgressDialog();
                Utils.showDialog(TrackAgents.this, R.string.err_unexpect);

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
                                startActivity(new Intent(THIS,
                                        AgentProfile.class).putExtra("agent",
                                        model));
                        } else if (resObj.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(THIS,
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            MainActivity.sp.edit().putBoolean("isFbLogin",
                                    false);
                            MainActivity.sp.edit().putString("user", null)
                                    .commit();
                            startActivity(new Intent(TrackAgents.this,
                                    Login.class));
                        } else {
                            Utils.showDialog(TrackAgents.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(TrackAgents.this, R.string.err_unexpect);
                    e.printStackTrace();
                }

            }

        });

    }
}
