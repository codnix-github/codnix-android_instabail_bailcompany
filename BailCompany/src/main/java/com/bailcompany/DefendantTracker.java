package com.bailcompany;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.FirebaseDefendantModel;
import com.bailcompany.ui.Defendant;
import com.bailcompany.utils.Dates;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class DefendantTracker extends CustomActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    GoogleMap googleMap;
    String DEFAULT_ICON = "https://instabailapp.com/web/assets/default.jpg";
    String compnayId = MainActivity.user.getCompanyId();
    Bitmap defaultmarkerimg;
    HashMap<String, Marker> markers;
    DateTimeFormatter inputDateFormat;
    private DatabaseReference mDatabase;
    private MapView mMapView;
    private ArrayList<DefendantModel> defList;
    private ArrayList<FirebaseDefendantModel> firebaseDefList;
    private String FIREBASE_PREFERENCE = "Instabail";
    private String COMPANY = "Company";
    private String DEFENDANT = "Defendants/";
    private boolean isZoomLevelSet = false;
    private Marker selectedMarker;
    private DefendantModel selectedDefendant;

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        // ((Activity) context).getWindowManager().getDefaultDisplay()
        // .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defendant_tracker);
        try {
            defList = (ArrayList<DefendantModel>) getIntent().getSerializableExtra("DefList");
            defaultmarkerimg = BitmapFactory.decodeResource(getResources(), R.drawable.marker_map);
            setActionBar();
            inputDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);

            initMap(savedInstanceState);
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(getString(R.string.title_activity_defendant_tracker));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);

    }


    private void initMap2(Bundle savedInstanceState)
            throws GooglePlayServicesNotAvailableException {
        MapsInitializer.initialize(THIS);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

    }

    private void initMap(Bundle savedInstanceState)
            throws GooglePlayServicesNotAvailableException {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        markers = new HashMap<>();
        mapFragment.getMapAsync(this);


    }

    public Bitmap getOverlay(Bitmap bitmap, Bitmap overlay) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(overlay, 0, 0, paint);

        return bitmap;
        //defaultmarkerimg

    }

    private Marker addMarker(final FirebaseDefendantModel fDefModel) {


        final LatLng position = new LatLng(Double.parseDouble(fDefModel.getLat()), Double.parseDouble(fDefModel.getLng()));

        final DefendantModel defDetail = getDefendantDetailById(fDefModel.getDefId());
        if (defDetail == null) {
            return null;
        }

        String photoURL = DEFAULT_ICON;
        if (defDetail.getPhoto() != null && !defDetail.getPhoto().equalsIgnoreCase("")) {
            photoURL = WebAccess.PHOTO + defDetail.getPhoto();
        } else {
            //default_profile_image
            markers.put(fDefModel.getDefId(), getMarker(fDefModel, defDetail, null, position));
        }

        try {
            Glide.with(getApplicationContext())
                    .load(photoURL)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(50, 50) {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            // Do nothing.
                            markers.put(fDefModel.getDefId(), getMarker(fDefModel, defDetail, null, position));

                        }

                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            // Do something with bitmap here.
                            Log.d("ImageLoaded", "yes=" + fDefModel.getDefId());
                            markers.put(fDefModel.getDefId(), getMarker(fDefModel, defDetail, bitmap, position));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_google))));
                */

                /*
        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(defModel, longitude))
                .anchor(0.5f, 0.5f)
                .title(defModel.getFirstName())
                .snippet(defModel.getLastName())
            .icon(BitmapDescriptorFactory.fromResource(R.id.accepted)));
            */
        return null;
    }

    private Marker getMarker(FirebaseDefendantModel fDefModel, DefendantModel defModel, Bitmap bitmap, LatLng position) {

        View convertView2 = DefendantTracker.this.getLayoutInflater().inflate(
                R.layout.row_custom_marker, null);

        ((TextView) convertView2.findViewById(R.id.title)).setText(""
                + fDefModel.getDefName());

        if (bitmap != null)
            ((ImageView) convertView2.findViewById(R.id.snippet)).setImageBitmap(bitmap);

        Bitmap bm2 = createDrawableFromView(getApplicationContext(), convertView2);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .snippet(fDefModel.getDefId())
                .icon(BitmapDescriptorFactory.fromBitmap(bm2)));


        // Move the camera instantly to hamburg with a zoom of 15.
        if (!isZoomLevelSet) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            isZoomLevelSet = true;

        }

        return marker;


    }

    private void infoWindow() {
        //  googleMap.setInfoWindowAdapter(new CustomInfoWindowDefendantAdapter(DefendantTracker.this, defList));


        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);


    }

    @Override
    public View getInfoWindow(final Marker marker) {
        getInfoContents(marker);
        selectedMarker = marker;
        View myContentsView = getLayoutInflater().inflate(R.layout.dialog_defendant_details, null);
        selectedDefendant = getDefendantDetailById(marker.getSnippet());
        if (selectedDefendant == null)
            return null;
/*
                Intent intent = new Intent(DefendantTracker.this, Defendant.class);
                intent.putExtra("defId", defDetail.getId());
                startActivity(intent);*/


        ProgressBar batteryValue = ((ProgressBar) myContentsView.findViewById(R.id.batteryValue));

        //batteryValue.setProgress();


        FirebaseDefendantModel defPhoneDetails = getDefendantPhoneDetails(selectedDefendant.getId());
        if (defPhoneDetails != null) {


            try {
                if (defPhoneDetails.getBattery() != null && !defPhoneDetails.getBattery().equalsIgnoreCase("")) {

                    ((TextView) myContentsView.findViewById(R.id.tvBatteryValue)).setText("" + defPhoneDetails.getBattery() + "%");
                    batteryValue.setProgress(Integer.parseInt(defPhoneDetails.getBattery()));

                } else {
                    findViewById(R.id.batterydetails).setVisibility(View.GONE);

                }

                if (defPhoneDetails.getWifi() != null && !defPhoneDetails.getWifi().equalsIgnoreCase("")) {
                    ((TextView) myContentsView.findViewById(R.id.tvWifi)).setText("WiFi : " + (defPhoneDetails.getWifi().equalsIgnoreCase("Y") ? "On" : "Off"));
                }
                if (defPhoneDetails.getMobileData() != null && !defPhoneDetails.getMobileData().equalsIgnoreCase("")) {
                    ((TextView) myContentsView.findViewById(R.id.tvMobileData)).setText("Mobile Data : " + (defPhoneDetails.getMobileData().equalsIgnoreCase("Y") ? "On" : "Off"));
                }

                TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.tvName));
                tvTitle.setText(selectedDefendant.getFirstName() + " " + selectedDefendant.getLastName());
                TextView tvLastUpdated = ((TextView) myContentsView.findViewById(R.id.tvLastUpdated));


                tvLastUpdated.setText("Last Updated : " + Dates.formatDate(getApplicationContext(), inputDateFormat.parseDateTime(defPhoneDetails.getDateTime())));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return myContentsView;
        //return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        return null;
    }


    private void moveMarker(final FirebaseDefendantModel fDefModel) {


        if (markers == null || !markers.containsKey(fDefModel.getDefId())) {
            return;

        }

        Marker m = markers.get(fDefModel.getDefId());
        if (m != null) {
           // Log.d("Marker Move", "yes=" + fDefModel.getDefName());
            final LatLng position = new LatLng(Double.parseDouble(fDefModel.getLat()), Double.parseDouble(fDefModel.getLng()));
            m.setPosition(position);
        }
        updateDefendantDetails(fDefModel);


    }

    private void updateDefendantDetails(FirebaseDefendantModel defmodal) {
        if (defmodal == null)
            return;

        for (int i = 0; i < firebaseDefList.size(); i++) {
            if (firebaseDefList.get(i).getDefId() != null && firebaseDefList.get(i).getDefId().equalsIgnoreCase(defmodal.getDefId())) {
                firebaseDefList.set(i, defmodal);
                break;

            }
        }


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
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        this.googleMap = googleMap;
      /*  LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
      */
        infoWindow();
        initFirebaseListner();

    }

    private void initFirebaseListner() {
        mDatabase = FirebaseDatabase.getInstance().getReference(FIREBASE_PREFERENCE + "/" + COMPANY + "/" + compnayId + "/" + DEFENDANT);
        firebaseDefList = new ArrayList<>();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                try {
                    FirebaseDefendantModel fDModel = dataSnapshot.getValue(FirebaseDefendantModel.class);
                    firebaseDefList.add(fDModel);
                    addMarker(fDModel);
                    //This call only once and also when new item add
                    Log.d("Child Added=", dataSnapshot.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d("DefData=", dataSnapshot.getKey() + "=" + dataSnapshot.getValue());
                try {
                    FirebaseDefendantModel defdata = dataSnapshot.getValue(FirebaseDefendantModel.class);
                    moveMarker(defdata);
                    Log.d("DefDataTime=", dataSnapshot.getKey() + "=" + defdata.getDateTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

                /*
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        FirebaseDefendantModel defdata = child.getValue(FirebaseDefendantModel.class);
                        Log.d("DefDataTime111=", dataSnapshot.getKey() + "=" + defdata.getDateTime());


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
*/
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        if (selectedDefendant != null) {
            Intent intent = new Intent(DefendantTracker.this, Defendant.class);
            intent.putExtra("defId", selectedDefendant.getId());
            startActivity(intent);
        }


        marker.hideInfoWindow();
    }

    private FirebaseDefendantModel getDefendantPhoneDetails(String defId) {
        if (defId == null)
            return null;
        FirebaseDefendantModel defDetail = null;
        for (FirebaseDefendantModel d : firebaseDefList) {
            if (d.getDefId() != null && d.getDefId().equalsIgnoreCase(defId)) {
                defDetail = d;
                break;

            }

        }
        return defDetail;
    }
}
