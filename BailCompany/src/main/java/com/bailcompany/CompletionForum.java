package com.bailcompany;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.custom.CustomGridView;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.ui.PaymentReceiptPopup;
import com.bailcompany.ui.SelfAssigned;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.FilePath;
import com.bailcompany.utils.ImageSelector;
import com.bailcompany.utils.ImageSelector.RemoveListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Methods;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class CompletionForum extends CustomActivity {
    private static final int Take_DROPBOX = 2;
    public static String pay = "0";
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 200000;
    private static boolean isDefendent, isCoSigner;
    ArrayList<String> imgPathList, docImgPaths;
    ArrayList<Uri> uriArrayList;
    BailRequestModel bailRequestMod;
    String pwrNum = "";
    // String payNum="";
    EditText payNum, PowerNum, comments;
    ProgressDialog pd;
    String message;
    JSONObject jsonObj;
    String key;
    LinearLayout coSigner;
    float totalSize = 0;
    TextView totalSizeTV;
    String cosignerPhotoPath, defPhotoPath;
    int adpaterPosition = 0;
    ArrayList<String> dropDownValuesList = new ArrayList<>();
    ArrayList<WarrantStruct> warrantIdList = new ArrayList<>();
    ArrayList<PreFixesHolder> preFixesHolders = new ArrayList<>();
    String path = "";
    private CircleImageView addDefendant;
    private CircleImageView addCoSigner;
    private LinearLayout preFixedViewLL;
    private File file, cosignerPhotoFile, defPhotoFile;
    private CustomGridView photoGrid;
    private PhotoAdapter adapter;
    private Uri cosignerPhotoUri, defPhotoUri;
    private DropboxAPI<AndroidAuthSession> mApi;

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
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
        // builder.setTitle(R.string.app_name);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(btn1, listener1);
        if (btn2 != null && listener2 != null)
            builder.setNegativeButton(btn2, listener2);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_forum);
        PowerNum = ((EditText) findViewById(R.id.pwrNum2));
        payNum = (EditText) findViewById(R.id.payment);
        preFixedViewLL = (LinearLayout) findViewById(R.id.preFixedViewLL);
        totalSizeTV = (TextView) findViewById(R.id.total_size);
        addDefendant = (CircleImageView) findViewById(R.id.add_defendant);
        addCoSigner = (CircleImageView) findViewById(R.id.add_co_signer);
        photoGrid = (CustomGridView) findViewById(R.id.my_grid_view);
        coSigner = (LinearLayout) findViewById(R.id.co_sign_container);
        comments = (EditText) findViewById(R.id.comments);
        bailRequestMod = (BailRequestModel) getIntent().getSerializableExtra(
                "bail");

        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        setLoggedIn(false);
        setTouchNClick(R.id.btn_complete);
        setTouchNClick(R.id.btn_abort);
        setTouchNClick(R.id.btn_cancel);
        setTouchNClick(R.id.add_defendant);
        setTouchNClick(R.id.add_co_signer);
        setActionBar();

        payNum.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && payNum.getText().toString().startsWith("$")) {
                    if (payNum.getText().toString().contains("0.00")) {
                        payNum.setText("$");
                    } else {
                        payNum.setText(payNum.getText().toString().replace("$", ""));
                    }
                } else if (!hasFocus && payNum.getText().toString().equals("$")) {
                    payNum.setText(payNum.getText().toString().replace("$", ""));
                } else if (!hasFocus && !payNum.getText().toString().startsWith("$") && payNum.getText().toString().length() > 1) {
                    payNum.setText("$" + payNum.getText().toString());
                }
            }
        });

        payNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (payNum.getText().toString().length() > 0 && !payNum.getText().toString().startsWith("$")) {
                    payNum.setText("$" + payNum.getText().toString());
                    payNum.setSelection(payNum.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgPathList = new ArrayList<String>();
        uriArrayList = new ArrayList<>();
        docImgPaths = new ArrayList<String>();
        adapter = new PhotoAdapter();
        photoGrid.setAdapter(adapter);
        photoGrid.setExpanded(true);
        photoGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                isCoSigner = false;
                isDefendent = false;
                adpaterPosition = position;
                file = new File(Const.TEMP_PHOTO + Const.getUniqueIdforImage()
                        + ".png");
                ImageView iv = (ImageView) view.findViewById(R.id.my_image);
                boolean result = Utility.checkPermission(THIS);
                if (result) {
                    if (iv.getTag().equals("1")) {
                        openChooser(THIS, file, null);
                    } else {
                        openChooser(THIS, file, new RemoveListener() {

                            @Override
                            public void onRemove() {
                                if (position >= imgPathList.size())
                                    return;
                                File f = new File(imgPathList.get(position));
                                float size = f.length() / 1024f;
                                String sizeStr = getDecrToatSize(size);
                                totalSizeTV.setText(sizeStr);
                                imgPathList.remove(position);
                                if (position >= uriArrayList.size())
                                    uriArrayList.remove(position);

                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

            }
        });

        getDropDownValues();
    }

    @Override
    protected void onResume() {
        hideKeyboard();
        AndroidAuthSession session = mApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);

            } catch (IllegalStateException e) {
                Utils.showDialog(this, "Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());
            }
        }
        super.onResume();
    }

    private void storeKeys(String key, String secret) {
        SharedPreferences prefs = getSharedPreferences(
                WebAccess.ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(WebAccess.ACCESS_KEY_NAME, key);
        edit.putString(WebAccess.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    public void openChooser(final Activity act, final File file,
                            final RemoveListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Select Picture");
        builder.setItems(listener == null ? ImageSelector.DIALOG_OPTIONS1
                        : ImageSelector.DIALOG_OPTIONS,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0)
                            ImageSelector.openGallary(act);
                        else if (position == 1)
                            ImageSelector.openCamera(act, file);

                        else if (position == 2 && listener != null)
                            listener.onRemove();

                    }
                });
        builder.create().show();
    }

    private void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.cmplte_frm));
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(WebAccess.DROPBOX_APP_KEY,
                WebAccess.DROPBOX_APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, WebAccess.ACCESS_TYPE,
                    accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, WebAccess.ACCESS_TYPE);
        }

        return session;
    }

    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(
                WebAccess.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(WebAccess.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(WebAccess.ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    public void setLoggedIn(boolean loggedIn) {

    }

    public void removeDropboxTempFiles() {
        for (int i = 0; i < imgPathList.size(); i++) {
            String path = imgPathList.get(i);
            if (path.contains("dropbox_content_")) {
                File file = new File(path);
                boolean deleted = file.delete();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_complete:
                hideKeyboard();
                if (isValid()) {
                    SelfAssigned.completeTask = true;
                    startActivityForResult(
                            new Intent(CompletionForum.this,
                                    PaymentReceiptPopup.class).putExtra("bail",
                                    bailRequestMod).putExtra("pay", payNum.getText().toString()), 111);
                }
                break;

            case R.id.btn_abort:
                hideKeyboard();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure to want abort ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        SelfAssigned.completeTask = true;
                                        if (Utils.isOnline()) {
                                            pwrNum = "0";
                                            pay = "0";
                                            abortRequest(false);
                                        } else {
                                            Utils.noInternetDialog(THIS);
                                        }

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Setting the title manually
                alert.setTitle("");
                alert.show();

                break;

            case R.id.btn_cancel:
                hideKeyboard();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Are you sure to want cancel ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        pwrNum = PowerNum.getText().toString();

                                        if (Utils.isOnline()) {
                                            SelfAssigned.completeTask = true;
                                            pwrNum = "0";
                                            pay = "0";
                                            abortRequest(true);
                                        } else {
                                            Utils.noInternetDialog(THIS);
                                        }

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert1 = builder1.create();
                alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                alert1.setTitle("");
                alert1.show();

                break;

            case R.id.add_co_signer:
                boolean result = Utility.checkPermission(THIS);
                if (result) {
                    isCoSigner = true;
                    isDefendent = false;
                    file = new File(Const.TEMP_PHOTO + Const.getUniqueIdforImage()
                            + ".png");
                    if (addCoSigner.getTag().equals("1")) {
                        openChooser(THIS, file, null);
                    } else {
                        openChooser(THIS, file, new RemoveListener() {

                            @Override
                            public void onRemove() {
                                File f = new File(cosignerPhotoPath);
                                float size = f.length() / 1024f;
                                String sizeStr = getDecrToatSize(size);
                                totalSizeTV.setText(sizeStr);
                                cosignerPhotoUri = null;
                                cosignerPhotoPath = null;
                                addCoSigner
                                        .setImageResource(R.drawable.completion_form_add_photo_btn);
                                addCoSigner.setTag("1");
                            }
                        });
                    }
                }
                break;

            case R.id.add_defendant:
                boolean result2 = Utility.checkPermission(THIS);
                if (result2) {
                    isCoSigner = false;
                    isDefendent = true;
                    file = new File(Const.TEMP_PHOTO + Const.getUniqueIdforImage()
                            + ".png");
                    if (addDefendant.getTag().equals("1")) {
                        openChooser(THIS, file, null);
                    } else {
                        openChooser(THIS, file, new RemoveListener() {

                            @Override
                            public void onRemove() {
                                File f = new File(defPhotoPath);
                                float size = f.length() / 1024f;
                                String sizeStr = getDecrToatSize(size);
                                totalSizeTV.setText(sizeStr);
                                defPhotoUri = null;
                                defPhotoPath = null;
                                addDefendant
                                        .setImageResource(R.drawable.completion_form_add_photo_btn);
                                addDefendant.setTag("1");
                            }
                        });
                    }
                    break;
                }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            boolean result = Utility.checkPermission(this);

        } else {
            Methods.showToast(this, "Permission Denny", Toast.LENGTH_SHORT);
        }

    }

    void hideKeyboard() {
        View view = THIS.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) THIS
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isValid() {
        for (int index = 0; index < preFixesHolders.size(); index++) {
            PreFixesHolder holder = preFixesHolders.get(index);

            if (holder.serialET.getText().toString().equals("") || holder.caseNoET.getText().toString().equals("")) {
                Utils.showDialog(THIS, "Enter Warrants Info").show();

                return false;
            }
        }

//        pwrNum = PowerNum.getText().toString();
//        // payNum=((TextView)findViewById(R.id.payment)).getText().toString();
//        pay = payNum.getText().toString().replace("$", "");
//        if (pwrNum.length() == 0) {
//            Utils.showDialog(THIS, "Enter Power Number !").show();
//            return false;
//        }

        if (payNum.getText().toString().equals("")) {
            Utils.showDialog(THIS, "Enter Payment !").show();
            return false;
        }
        return true;
    }

    private void submitCompletionForm() {

        try {

            pd = ProgressDialog.show(CompletionForum.this, "", "Loading.....");
            RequestParams param = new RequestParams();
            String phoneNum = "", agentReqId = "";

            if (bailRequestMod != null) {
                agentReqId = bailRequestMod.getAgentRequestId() + "";
            }

            phoneNum = MainActivity.user.getPhone() + "";

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());

            if (cosignerPhotoUri != null) {

                InputStream imgISCoSign;
                imgISCoSign = getContentResolver().openInputStream(
                        cosignerPhotoUri);
                param.put("CoSignerPhoto", imgISCoSign, "cosignerPhoto.jpg",
                        "image/jpg");
            }
            if (defPhotoUri != null) {

                InputStream imgISdef;
                imgISdef = getContentResolver().openInputStream(defPhotoUri);

                param.put("DefendentPhoto", imgISdef, "defPhoto.jpg",
                        "image/jpg");

            }
            for (int i = 0; i < imgPathList.size(); i++) {
                String type;
                String DocPhotos = "DocPhotos_" + i;
                String path = imgPathList.get(i);

                type = null;
                if (path.lastIndexOf(".") != -1) {
                    type = path.substring(path.lastIndexOf(".") + 1);

                }
                if (type != null) {
                    if (type.equalsIgnoreCase("pdf")
                            || type.equalsIgnoreCase("pdf")) {
                        File file1 = new File(path);
                        param.put(DocPhotos, file1);
                    }
                    if (type.equalsIgnoreCase("doc")
                            || type.equalsIgnoreCase("doc")) {
                        File file2 = new File(path);
                        param.put(DocPhotos, file2);
                    } else {
                        File file3 = new File(path);
                        param.put(DocPhotos, file3);
                    }
                } else {
                    File file4 = new File(path);
                    param.put(DocPhotos, file4, DocPhotos + ".jpg", "image/jpg");
                }
            }
            param.put("RequestId", agentReqId);
            param.put("phoneNumber", phoneNum);
            param.put("PowerNo", pwrNum);
            param.put("comments", comments.getText().toString());

            String amount = payNum.getText().toString();

            if (amount.contains("$"))
                param.put("Amount", amount.replace("$", ""));
            else
                param.put("Amount", amount);

            for (int index = 0; index < preFixesHolders.size(); index++) {
                PreFixesHolder holder = preFixesHolders.get(index);

                param.put("pre_fix[" + index + "]", holder.spinner.getSelectedItem().toString());
                param.put("serial[" + index + "]", holder.serialET.getText().toString());
                param.put("warrant[" + index + "]", holder.warrantID);
                param.put("case_no[" + index + "]", holder.caseNoET.getText().toString());
            }

            String url = WebAccess.MAIN_URL + WebAccess.SUBMIT_COMPLETION_FORM;
            client.setTimeout(getCallTimeout);
            client.post(this, url, param, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();

                    Utils.showDialog(CompletionForum.this,
                            R.string.err_unexpect);
                    removeDropboxTempFiles();
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                    pd.dismiss();
                    try {
                        String response2;
                        response2 = new String(responseBody);
                        JSONObject resObj;
                        resObj = new JSONObject(response2);
                        if (resObj != null) {
                            if (resObj.optString("status")
                                    .equalsIgnoreCase("1")) {
                                // setCompleteStatus();
                                message = resObj.optString("message");
                                if (!Commons.isEmpty(message)
                                        || message.equalsIgnoreCase("success")) {
                                    showDialog(CompletionForum.this, message);

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
                                startActivity(new Intent(CompletionForum.this,
                                        Launcher.class));
                            } else {

                                Utils.showDialog(CompletionForum.this,
                                        resObj.optString("message"));
                            }
                        }
                    } catch (JSONException e) {
                        Utils.showDialog(CompletionForum.this, "Error occurs");
                        e.printStackTrace();
                    }

                    removeDropboxTempFiles();
                }

            });
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void getDropDownValues() {
        pd = ProgressDialog.show(CompletionForum.this, "", "Loading.....");

        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("RequestId", bailRequestMod.getAgentRequestId());
        param.put("UserName", MainActivity.user.getUsername());
        client.setTimeout(getCallTimeout);
        String url = WebAccess.MAIN_URL + "get-prefixes";
        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Utils.showDialog(CompletionForum.this, error.toString());
                pd.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                pd.dismiss();

                try {
                    String response2;
                    response2 = new String(responseBody);
                    JSONObject resObj;
                    resObj = new JSONObject(response2);
                    if (resObj != null) {
                        if (resObj.optString("status").equalsIgnoreCase("1")) {
                            dropDownValuesList = new ArrayList<>();
                            warrantIdList = new ArrayList<>();

                            JSONArray warrantArr = resObj.optJSONArray("warrantList");
                            JSONArray preFixArr = resObj.optJSONArray("pre_fixes");

                            if (warrantArr != null) {

                                for (int wIndex = 0; wIndex < warrantArr.length(); wIndex++) {
                                    JSONObject warrantObj = warrantArr.getJSONObject(wIndex);

                                    WarrantStruct struct = new WarrantStruct();
                                    struct.id = warrantObj.optInt("Id");
                                    struct.title = warrantObj.optString("Township");

                                    warrantIdList.add(struct);
                                }
                            }

                            if (preFixArr != null) {
                                dropDownValuesList.add("Select Pre-fix");
                                for (int pIndex = 0; pIndex < preFixArr.length(); pIndex++) {
                                    JSONObject preFixObj = preFixArr.getJSONObject(pIndex);

                                    dropDownValuesList.add(preFixObj.optString("pre_fix"));
                                }
                            }

                            addDropDownViews();
                        } else if (resObj.optString("status").equalsIgnoreCase("3")) {
                            Toast.makeText(CompletionForum.this, "Session was closed please login again", Toast.LENGTH_LONG).show();
                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(CompletionForum.this);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove("login");// login
                            editor.commit();
                            startActivity(new Intent(CompletionForum.this,
                                    Login.class));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(CompletionForum.this, "Error occurs");
                    e.printStackTrace();
                }
            }
        });
    }

    public void addDropDownViews() {
        preFixesHolders = new ArrayList<>();

        for (int index = 0; index < warrantIdList.size(); index++) {
            View child = getLayoutInflater().inflate(R.layout.pre_fixes_view, null);

            WarrantStruct struct = warrantIdList.get(index);

            PreFixesHolder holder = new PreFixesHolder();
            holder.spinner = (Spinner) child.findViewById(R.id.powerNumberSp);
            holder.titleTV = (TextView) child.findViewById(R.id.titleTV);
            holder.serialET = (EditText) child.findViewById(R.id.serialET);
            holder.caseNoET = (EditText) child.findViewById(R.id.caseNoET);
            holder.warrantID = struct.id;


            holder.titleTV.setText(struct.title);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dropDownValuesList);
            holder.spinner.setAdapter(adapter);

            preFixedViewLL.addView(child);
            preFixesHolders.add(holder);
        }
    }

    public void setCompleteStatus() {

        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("RequestId", bailRequestMod.getAgentRequestId());
        param.put("IsComplete", "1");
        client.setTimeout(getCallTimeout);
        String url = WebAccess.MAIN_URL + WebAccess.SET_COMPLETE_STATUS;
        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                Utils.showDialog(CompletionForum.this, error.toString());

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                try {
                    String response2;
                    response2 = new String(responseBody);
                    JSONObject resObj;
                    resObj = new JSONObject(response2);
                    if (resObj != null) {
                        if (!resObj.optString("status").equalsIgnoreCase("1")) {
                            // Utils.showDialog(CompletionForm.this,
                            // "Error occurs in status Request");
                            Toast.makeText(THIS, message + "",
                                    Toast.LENGTH_SHORT).show();

                        } else if (resObj.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(CompletionForum.this,
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(CompletionForum.this);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove("login");// login
                            editor.commit();
                            startActivity(new Intent(CompletionForum.this,
                                    Login.class));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(CompletionForum.this, "Error occurs");
                    e.printStackTrace();
                }
            }

        });

    }

    public void abortRequest(final boolean cancel) {
        int i = 0;
        if (cancel) {
            i = 1;
        }
        showProgressDialog("");
        RequestParams param = new RequestParams();
        param.put("TemporaryAccessCode", MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());
        param.put("RequestId", bailRequestMod.getAgentRequestId());
        param.put("IsCancel", i);
        param.put("PowerNo", pwrNum);
        param.put("Amount", pay);
        param.put("comments", comments.getText().toString());
        client.setTimeout(getCallTimeout);
        String url = WebAccess.MAIN_URL + WebAccess.ABORT_REQUEST;
        client.post(this, url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                dismissProgressDialog();
                Utils.showDialog(CompletionForum.this, R.string.err_unexpect);
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
                            message = resObj.optString("message");
                            if (!Commons.isEmpty(message)
                                    || message.equalsIgnoreCase("success")) {
                                if (cancel)
                                    setResult(RESULT_OK, new Intent().putExtra(
                                            "cancel", true));
                                else
                                    setResult(RESULT_OK, new Intent().putExtra(
                                            "abort", true));
                                SharedPreferences sp = PreferenceManager
                                        .getDefaultSharedPreferences(THIS);
                                sp.edit().putString("bail", null).commit();

                                MainActivity.bailReqModel = null;

                                finish();
                            }

                            Toast.makeText(THIS, message, Toast.LENGTH_SHORT)
                                    .show();

                        } else if (resObj.optString("status").equalsIgnoreCase(
                                "3")) {
                            Toast.makeText(CompletionForum.this,
                                    "Session was closed please login again",
                                    Toast.LENGTH_LONG).show();
                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(CompletionForum.this);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove("login");// login
                            editor.commit();
                            startActivity(new Intent(CompletionForum.this,
                                    Login.class));
                        } else {
                            Utils.showDialog(CompletionForum.this,
                                    resObj.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Utils.showDialog(CompletionForum.this, "Error occurs");
                    e.printStackTrace();

                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // popop
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (resultCode == RESULT_OK) {

            if (requestCode == 111) {
                if (Utils.isOnline()) {
                    submitCompletionForm();
                } else {
                    Utils.noInternetDialog(THIS);
                }
            } else if (requestCode == 222) {
                if (Utils.isOnline()) {
                    abortRequest(false);
                } else {
                    Utils.noInternetDialog(THIS);
                }
            } else if (requestCode == 333) {
                if (Utils.isOnline()) {
                    abortRequest(true);
                } else {
                    Utils.noInternetDialog(THIS);
                }

            } else {
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
                } else if (requestCode == Take_DROPBOX) {
                    path = data.getStringExtra("path");
                    uri = Uri.fromFile(new File(path));
                    if (isCoSigner || isDefendent) {
                        if (path.contains(".pdf") || path.contains(".docx")) {
                            path = null;
                            uri = null;
                        }
                    }

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        uri = result.getUri();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                } else {
                    uri = data.getData();
                    if (uri == null) {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        uri = ImageUtils.getImageUri(THIS, imageBitmap);
                    }

                    path = FilePath.getPath(THIS, uri);
                    if (uri.toString().contains("com.dropbox")) {
                        if (isCoSigner || isDefendent) {
                            if (path.contains(".pdf") || path.contains(".docx")) {
                                path = null;
                                uri = null;
                            }

                        }
                    }
                }
                String sizeStr = "Total Attachments Size: 0.00 KB";
                if (isCoSigner) {
                    if (uri != null && requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        if (ImageSelector.isImage(path.trim().substring(path.lastIndexOf('/')))) {
                            cosignerPhotoUri = uri;
                            CropImage.activity(cosignerPhotoUri).setAspectRatio(1, 1)
                                    .start(this);
                        } else {
                            Toast.makeText(THIS, "Please select valid image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (uri != null && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        if (cosignerPhotoPath != null) {
                            File f = new File(cosignerPhotoPath);
                            float size1 = f.length() / 1024f;
                            getDecrToatSize(size1);
                        }

                        File file = new File(path);
                        float size = file.length() / 1024f;
                        sizeStr = getToatSize(size);
                        if (sizeStr != null) {
                            cosignerPhotoPath = uri.getPath();
                            cosignerPhotoUri = uri;
                            addCoSigner.setImageResource(0);
                            addCoSigner.setImageURI(uri);
                            addCoSigner.setTag("0");
                            totalSizeTV.setText(sizeStr);
                        } else {
                            Utils.showDialog(this,
                                    "Too Big!  All attachments \"combined\" can not exceed 24mb");
                        }

                    }
                    return;
                } else if (isDefendent) {
                    if (uri != null && requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        if (ImageSelector.isImage(path.trim().substring(path.lastIndexOf('/')))) {
                            defPhotoUri = uri;
                            CropImage.activity(defPhotoUri).setAspectRatio(1, 1)
                                    .start(this);
                        } else {
                            Toast.makeText(THIS, "Please select valid image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (uri != null && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        defPhotoUri = uri;
                        if (defPhotoPath != null) {
                            File f = new File(defPhotoPath);
                            float size1 = f.length() / 1024f;
                            sizeStr = getDecrToatSize(size1);
                        }
                        File file = new File(path);
                        float size = file.length() / 1024f;
                        sizeStr = getToatSize(size);
                        if (sizeStr != null) {
                            defPhotoPath = uri.getPath();
                            defPhotoUri = uri;
                            addDefendant.setImageResource(0);
                            // addDefendant.setImageBitmap(compressFile(uri.getPath()));

                            addDefendant.setImageURI(uri);
                            addDefendant.setTag("0");
                            totalSizeTV.setText(sizeStr);
                        } else {
                            Utils.showDialog(this,
                                    "Too Big!  All attachments \"combined\" can not exceed 24mb");
                        }


                    }


                } else if (path != null) {
                    if (!imgPathList.isEmpty()) {
                        if (adpaterPosition < imgPathList.size()) {
                            File f = new File(imgPathList.get(adpaterPosition));
                            float size1 = f.length() / 1024f;
                            sizeStr = getDecrToatSize(size1);

                            imgPathList.remove(adpaterPosition);
                            if (adpaterPosition < uriArrayList.size())
                                uriArrayList.remove(adpaterPosition);
                        }

                    }
                    File file = new File(path);
                    float size = file.length() / 1024f;
                    sizeStr = getToatSize(size);
                    if (sizeStr != null) {
                        imgPathList.add(path);
                        uriArrayList.add(uri);
                        adapter.notifyDataSetChanged();
                        totalSizeTV.setText(sizeStr);
                    } else {
                        Utils.showDialog(this,
                                "Too Big!  All attachments \"combined\" can not exceed 24mb");
                    }

                    return;
                }

            }

        }
    }

    public void toatSize(String fileType) {
        if (fileType.equalsIgnoreCase("defendant")) {

        } else if (fileType.equalsIgnoreCase("cosigner")) {

        } else {

        }
    }

    public String getToatSize(float size) {
        DecimalFormat df = new DecimalFormat("0.00");
        String total = "Total Attachments Size: 0.00 KB";
        float sizeFile = 0;
        sizeFile = totalSize;
        if (sizeFile > 24576) {
            return null;
        } else {
            sizeFile += size;
            if (sizeFile > 24576) {
                return null;
            } else {
                totalSize = sizeFile;
                if (totalSize >= 1024) {
                    total = "Total Attachments Size: "
                            + df.format(totalSize / 1024) + " MB";
                } else {
                    total = "Total Attachments Size: " + df.format(totalSize)
                            + " KB";
                }

            }
        }
        return total;
    }

    public String getDecrToatSize(float size) {
        DecimalFormat df = new DecimalFormat("0.00");
        String total = totalSizeTV.getText().toString();
        float sizeFile = 0;
        sizeFile = totalSize;
        if (sizeFile >= size) {
            sizeFile -= size;
            totalSize = sizeFile;
            if (totalSize >= 1024) {
                total = "Total Attachments Size: "
                        + df.format(totalSize / 1024) + " MB";
            } else {
                total = "Total Attachments Size: " + df.format(totalSize)
                        + " KB";
            }

        }
        return total;
    }

    private Bitmap compressFile(String path) {
        File f = new File(path);
        Bitmap bm = ImageUtils.getOrientationFixedImage(f,
                StaticData.getDIP(110), StaticData.getDIP(110),
                ImageUtils.SCALE_FIT_CENTER);
        bm = ImageUtils.getCircularBitmap(bm);
        return bm;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public AlertDialog showDialog(Context ctx, String msg)// ///hello
    {

        return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                finish();
            }
        });
    }

    private Bitmap compressFilePhoto(String path) {
        File f = new File(path);
        Bitmap bm = ImageUtils.getOrientationFixedImage(f,
                (StaticData.width / 2) - 50, StaticData.height,
                ImageUtils.SCALE_FITXY);
        return bm;
    }

    public class PreFixesHolder {
        Spinner spinner;
        TextView titleTV;
        EditText serialET;
        EditText caseNoET;
        int warrantID;
    }

    public class WarrantStruct {
        int id;
        String title;
    }

    private class PhotoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imgPathList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(
                        R.layout.document_item, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.my_image);

            if (position >= imgPathList.size()) {
                iv.setImageResource(R.drawable.add_photo_square);
                iv.setTag("1");
                iv.setScaleType(ScaleType.FIT_XY);
            } else if (!imgPathList.isEmpty()) {
                iv.setTag("0");
                String url = imgPathList.get(position);
                String type = null;
                if (url.lastIndexOf(".") != -1) {
                    type = url.substring(url.lastIndexOf(".") + 1);
                    // MimeTypeMap mime = MimeTypeMap.getSingleton();
                    // type = mime.getMimeTypeFromExtension(ext);
                } else {
                    type = "png";
                }
                if (type.equalsIgnoreCase("pdf")) {
                    Drawable res = getResources().getDrawable(R.drawable.pdf);
                    iv.setImageDrawable(res);
                } else if (type.equalsIgnoreCase("docx") || type.equalsIgnoreCase("doc")) {
                    Drawable res = getResources().getDrawable(R.drawable.docs);
                    iv.setImageDrawable(res);
                } else {
                    Bitmap bitmap = compressFilePhoto(imgPathList.get(position));

                    if (bitmap == null) {
                        iv.setImageURI(uriArrayList.get(position));
                    } else {
                        iv.setImageBitmap(bitmap);
                    }

                }
                iv.setScaleType(ScaleType.FIT_XY);
            }
            return convertView;
        }

    }

}
