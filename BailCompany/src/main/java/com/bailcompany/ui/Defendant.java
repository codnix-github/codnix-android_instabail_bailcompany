package com.bailcompany.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.DefendantBasicProfileDetails;
import com.bailcompany.DefendantBondDetails;
import com.bailcompany.DefendantEmploymentDetails;
import com.bailcompany.Launcher;
import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.DefendantEmploymentModel;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.model.DefendantNotesModel;
import com.bailcompany.model.DefendantVehicleModel;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class Defendant extends CustomFragment {

    public static ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;
    ArrayList<String> historyItem = new ArrayList<String>();
    String defendantUrl, defendantBondUrl;
    boolean isSender, IsBailRequest;
    ListView incomingRequestList;
    int page = 0;
    boolean loadingMore;
    Defendant.IncomingListAdapter adapter;
    View v;
    ImageView defProfile;
    TextView tvDefName, tvDefLocation;
    int defId = 1;
    private ListView historyList;
    private Animator mCurrentAnimator;
    private DefendantModel defModel;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_defendant_profile, null);

        defProfile = (ImageView) v.findViewById(R.id.profile_pic);
        tvDefLocation = (TextView) v.findViewById(R.id.tvDefLocation);
        tvDefName = (TextView) v.findViewById(R.id.tvDefName);
        defendantUrl = WebAccess.GET_DEFENDANT_DETAIL;
        defendantBondUrl = WebAccess.GET_DEFENDANT_BOND_DETAIL;
        isSender = false;
        IsBailRequest = true;
        geDefendantProfile();
        incomingRequestList = (ListView) v.findViewById(R.id.incoming_request_list);
        ((Button) v.findViewById(R.id.btnBasicDetails)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(getContext(),
                                DefendantBasicProfileDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });
        ((Button) v.findViewById(R.id.btnEmployment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(getContext(),
                                DefendantEmploymentDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });
        ((Button) v.findViewById(R.id.btnVehicle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(getContext(),
                                DefendantEmploymentDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });
        ((Button) v.findViewById(R.id.btnNotes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(getContext(),
                                DefendantEmploymentDetails.class).putExtra(
                                "defendant", defModel), 5555);
            }
        });


        adapter = new IncomingListAdapter(getActivity());
        incomingRequestList.setAdapter(adapter);
        incomingRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                bailReqList.get(position).setRead("1");

                startActivityForResult(
                        new Intent(getActivity(),
                                DefendantBondDetails.class).putExtra(
                                "bail", bailReqList.get(position)).putExtra("defendant", defModel).putExtra(
                                "position", position), 5555);
            }
        });

        getBondDetails();

        final View thumb1View = v.findViewById(R.id.profile_pic);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(thumb1View, R.drawable.profile_pic);
            }
        });
        final ImageView ivFacebook = (ImageView) v.findViewById(R.id.ivFacebook);
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!defModel.getFacebookURL().equals("")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(defModel.getFacebookURL()));
                    startActivity(i);
                }
            }
        });

        final ImageView ivGoogle = (ImageView) v.findViewById(R.id.ivGoogle);
        ivGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!defModel.getGoogleURL().equals("")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(defModel.getGoogleURL()));
                    startActivity(i);
                }
            }
        });
        final ImageView ivTwitter = (ImageView) v.findViewById(R.id.ivTwitter);
        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!defModel.getTwitterURL().equals("")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(defModel.getTwitterURL()));
                    startActivity(i);
                }
            }
        });
        final ImageView ivCallDefendant = (ImageView) v.findViewById(R.id.ivCallDefendant);
        ivCallDefendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!defModel.getCellTele().equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + defModel.getCellTele()));
                    startActivity(callIntent);
                } else if (!defModel.getHomeTele().equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + defModel.getHomeTele()));
                    startActivity(callIntent);
                }

            }
        });

        tvDefLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + tvDefLocation.getText().toString()));
                startActivity(i);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        setHasOptionsMenu(true);
        return v;
    }


    void geDefendantProfile() {
        if (Utils.isOnline(getActivity())) {
            if (page == 0)
                showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("Page", page);
            param.put("Id", defId);

            String url = WebAccess.MAIN_URL + defendantUrl;
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
                                WebAccess.AllBidListCompany.clear();
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

                                        defModel = new DefendantModel();
                                        JSONArray arrDef = resObj.getJSONArray("profile");
                                        if (arrDef.length() > 0) {
                                            JSONObject defObject = arrDef.getJSONObject(0);
                                            defModel.setId(defObject.getString("Id"));
                                            defModel.setCompanyId(defObject.getString("CompanyId"));
                                            defModel.setFirstName(defObject.getString("FirstName"));
                                            defModel.setLastName(defObject.getString("LastName"));
                                            defModel.setAddress(defObject.getString("Address"));
                                            defModel.setTown(defObject.getString("Town"));
                                            defModel.setState(defObject.getString("State"));
                                            defModel.setZipcode(defObject.getString("Zipcode"));
                                            defModel.setDOB(defObject.getString("DOB"));
                                            defModel.setSSN(defObject.getString("SSN"));
                                            defModel.setPOB(defObject.getString("POB"));
                                            defModel.setHomeTele(defObject.getString("HomeTele"));
                                            defModel.setCellTele(defObject.getString("CellTele"));
                                            defModel.setHeight(defObject.getString("Height"));
                                            defModel.setWeight(defObject.getString("Weight"));
                                            defModel.setHairColor(defObject.getString("HairColor"));
                                            defModel.setEyeColor(defObject.getString("EyeColor"));
                                            defModel.setTattoos(defObject.getString("Tattoos"));
                                            defModel.setMaritalStatus(defObject.getString("MaritalStatus"));
                                            defModel.setPhoto(defObject.getString("Photo"));
                                            defModel.setFacebookURL(defObject.getString("FacebookURL"));
                                            defModel.setTwitterURL(defObject.getString("TwitterURL"));
                                            defModel.setGoogleURL(defObject.getString("GoogleURL"));
                                            defModel.setStatus(defObject.getString("Status"));
                                            defModel.setModifyOn(defObject.getString("ModifyOn"));
                                            defModel.setStateName(defObject.getString("StateName"));

                                            JSONArray defEmploymentDtl = resObj.getJSONArray("employementdetails");
                                            JSONArray defVehicleDtl = resObj.getJSONArray("vehicledetails");
                                            JSONArray defNoteDtl = resObj.getJSONArray("notes");

                                            ArrayList<DefendantEmploymentModel> employmentDtl = new ArrayList<DefendantEmploymentModel>();
                                            if (defEmploymentDtl != null && defEmploymentDtl.length() > 0) {
                                                for (int i = 0; i < defEmploymentDtl.length(); i++) {
                                                    DefendantEmploymentModel empModel = new DefendantEmploymentModel();
                                                    JSONObject empObj = defEmploymentDtl.getJSONObject(i);
                                                    empModel.setId(empObj.getString("Id"));
                                                    empModel.setDefId(empObj.getString("DefendantId"));
                                                    empModel.setEmployer(empObj.getString("Employer"));
                                                    empModel.setOccupation(empObj.getString("Occupation"));
                                                    empModel.setAddress(empObj.getString("Address"));
                                                    empModel.setCity(empObj.getString("City"));
                                                    empModel.setState(empObj.getString("State"));
                                                    empModel.setZip(empObj.getString("Zip"));
                                                    empModel.setTelephone(empObj.getString("Telephone"));
                                                    empModel.setSupervisor(empObj.getString("Supervisor"));
                                                    empModel.setDuration(empObj.getString("Duration"));
                                                    empModel.setStatus(empObj.getString("Status"));
                                                    empModel.setModifyOn(empObj.getString("ModifyOn"));
                                                    employmentDtl.add(empModel);

                                                }
                                            }
                                            defModel.setEmploymentDtl(employmentDtl);

                                            ArrayList<DefendantVehicleModel> vehicleDtl = new ArrayList<>();
                                            if (defVehicleDtl != null && defVehicleDtl.length() > 0) {
                                                for (int i = 0; i < defVehicleDtl.length(); i++) {
                                                    DefendantVehicleModel vehiModel = new DefendantVehicleModel();
                                                    JSONObject empObj = defVehicleDtl.getJSONObject(i);
                                                    vehiModel.setId(empObj.getString("Id"));
                                                    vehiModel.setDefId(empObj.getString("DefendantId"));
                                                    vehiModel.setYear(empObj.getString("Year"));
                                                    vehiModel.setMake(empObj.getString("Make"));
                                                    vehiModel.setModel(empObj.getString("Model"));
                                                    vehiModel.setColor(empObj.getString("Color"));
                                                    vehiModel.setState(empObj.getString("State"));
                                                    vehiModel.setRegistration(empObj.getString("Registration"));
                                                    vehiModel.setStatus(empObj.getString("Status"));
                                                    vehiModel.setModifyOn(empObj.getString("ModifyOn"));
                                                    vehicleDtl.add(vehiModel);

                                                }
                                            }
                                            defModel.setVehicleDtl(vehicleDtl);
                                            ArrayList<DefendantNotesModel> nodeDtl = new ArrayList<>();
                                            if (defNoteDtl != null && defNoteDtl.length() > 0) {
                                                for (int i = 0; i < defNoteDtl.length(); i++) {
                                                    DefendantNotesModel noteModel = new DefendantNotesModel();
                                                    JSONObject empObj = defNoteDtl.getJSONObject(i);
                                                    noteModel.setId(empObj.getString("Id"));
                                                    noteModel.setDefId(empObj.getString("DefId"));
                                                    noteModel.setNote(empObj.getString("Note"));
                                                    noteModel.setStatus(empObj.getString("Status"));
                                                    noteModel.setModifyOn(empObj.getString("ModifyOn"));
                                                    nodeDtl.add(noteModel);

                                                }
                                            }
                                            defModel.setNotesDtl(nodeDtl);


                                            //  Glide.with(getActivity()).load(WebAccess.PHOTO + defModel.getPhoto()).into(defProfile);

                                            String fullAddress = "";
                                            if (defModel.getAddress() != "") {
                                                fullAddress = defModel.getAddress() + ", ";
                                            }
                                            if (defModel.getTown() != "") {
                                                fullAddress += defModel.getTown() + ", ";
                                            }
                                            if (defModel.getStateName() != "") {
                                                fullAddress += defModel.getStateName() + ", ";
                                            }
                                            if (defModel.getZipcode() != "") {
                                                fullAddress += defModel.getZipcode();
                                            }

                                            tvDefLocation.setText(fullAddress);


                                            tvDefName.setText(defModel.getFirstName() + " " + defModel.getLastName());

                                            Bitmap bm = new ImageLoader(StaticData.getDIP(60),
                                                    StaticData.getDIP(60), ImageLoader.SCALE_FITXY).loadImage(
                                                    WebAccess.PHOTO + defModel.getPhoto(), new ImageLoader.ImageLoadedListener() {

                                                        @Override
                                                        public void imageLoaded(Bitmap bm) {
                                                            if (bm != null)
                                                                defProfile.setImageBitmap(ImageUtils
                                                                        .getCircularBitmap(bm));

                                                        }
                                                    });
                                            if (bm != null)
                                                defProfile.setImageBitmap(ImageUtils.getCircularBitmap(bm));
                                            else
                                                defProfile.setImageResource(R.drawable.default_profile_image);


                                        }


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
                                            adapter.notifyDataSetChanged();
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

    void getBondDetails() {
        if (Utils.isOnline(getActivity())) {
            if (page == 0)
                showProgressDialog("");
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            param.put("Page", page);
            param.put("Id", defId);

            String url = WebAccess.MAIN_URL + defendantBondUrl;
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
                                WebAccess.AllBidListCompany.clear();
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
                                        bailReqList.clear();
                                        WebAccess.getDefendantBonds(response2);
                                        if (bailReqList != null
                                                && bailReqList.size() > 0) {
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Utils.showDialog(getActivity(),
                                                    "No Bail Request Available")
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        }


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
                                            adapter.notifyDataSetChanged();
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
        if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        incomingRequestList.setAdapter(adapter);
        if (resultCode == Activity.RESULT_OK) {
            String key = data.getStringExtra(Const.RETURN_FLAG);
            if (key.equalsIgnoreCase(Const.BOND_DETAILS_UPDATED) || key.equalsIgnoreCase(Const.BOND_DOCUMENT_UPLOADED))
                getBondDetails();
            else if (key.equalsIgnoreCase(Const.DEFENDANT_BASIC_DETAILS_UPDATED))
                geDefendantProfile();


        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) v.findViewById(
                R.id.expanded_image);


        Glide.with(getActivity()).load(WebAccess.PHOTO + defModel.getPhoto()).placeholder(R.drawable.ic_action_name).error(R.drawable.default_profile_image).into(expandedImageView);
        //expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        v.findViewById(R.id.mainTopLayout)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    @SuppressLint("InflateParams")
    private class IncomingListAdapter extends BaseAdapter {
        public IncomingListAdapter(Context context) {

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return bailReqList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return bailReqList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.defendant_bond_item, null);
            LinearLayout lp = (LinearLayout) convertView.findViewById(R.id.lp);
            if (position % 2 == 0)
                lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
            else
                lp.setBackgroundColor(Color.WHITE);

            String powerNumbers = "";
            for (int i = 0; i < bailReqList.get(position).getWarrantList().size(); i++) {
                powerNumbers += bailReqList.get(position).getWarrantList().get(i).getTownship();
                //   powerNumbers+=bailReqList.get(position).getWarrantList().get(i).getPowerNo();

                powerNumbers += "\n";
            }

            ((TextView) convertView.findViewById(R.id.tvTownName))
                    .setText(powerNumbers);


            ((TextView) convertView.findViewById(R.id.date)).setText(Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                    "MM/dd/yyyy", bailReqList.get(position).getCreatedDate()));


            int i = incomingRequestList.getLastVisiblePosition();
            int j = incomingRequestList.getAdapter().getCount();
            i++;

            return convertView;
        }
    }

}
