package com.bailcompany;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.User;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.Log;
import com.bailcompany.utils.Methods;
import com.bailcompany.utils.ObjectSerializer;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class Login extends CustomActivity {

    private EditText username;
    private EditText password;
    private User response;
    static int getCallTimeout = 50000;
    int i;
    String response2;
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    String message;
    JSONObject jsonObj;
    String key;
    int count = 0;

    boolean loginBtnPressed;
    String REG_ID;
    public static User user;
    private TextView fbSig;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(Login.this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        fbSig = (TextView) findViewById(R.id.authButton);

        setContentView(R.layout.login);
        setTouchNClick(R.id.signin_login);
        setTouchNClick(R.id.forgot_pass);
        setTouchNClick(R.id.authButton);
        WebAccess.loginUser = false;
        registerFCM();

        username = (EditText) findViewById(R.id.email_login);
        password = (EditText) findViewById(R.id.pass_login);
        // username.setText("john.butler3");
        // password.setText("123456");

        setActionBar();
        // fbLoginSetup(savedInstanceState);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(THIS);
        String usr = sp.getString("user", "");
        if (!usr.equalsIgnoreCase("")) {
            try {
                user = (User) ObjectSerializer.deserialize(usr);
                boolean isFblogin = sp.getBoolean("isFbLogin", false);
                if (Utils.isOnline())
                    if (isFblogin) {
                        doLogin(true, user.getEmail(), "");
                    } else {
                        doLogin(false, user.getUsername(), user.getPassword());
                    }
                else
                    Utils.noInternetDialog(THIS);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Utility.checkPermission(this);
    }

    private void setActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.signin));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void onCall() {

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json,
                                                            GraphResponse response) {

                                        if (response.getError() != null) {
                                            Toast.makeText(Login.this,
                                                    "Error occurs",
                                                    Toast.LENGTH_LONG).show();
                                            System.out.println("ERROR");
                                        } else {
                                            loadvalue(json);
                                        }
                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id ,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Utils.showDialog(Login.this,
                                "Error occurs,some thing went wrong");
                    }

                });
    }

    void loadvalue(JSONObject json) {
        try {
            String email = json.getString("email");

            if (email != null) {
                doLogin(true, email, "");
            } else {
                Toast.makeText(
                        Login.this,
                        "Account restriction not able to get email address of facebook account",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.signin_login) {
            loginBtnPressed = true;

            hideKeyboard();

            boolean result = Utility.checkPermission(this);
            if (result) {
                if (isValid()) {
                    if (Utils.isOnline())
                        doLogin(false, username.getText().toString(), password
                                .getText().toString());
                    else
                        Utils.noInternetDialog(THIS);
                }
            }
        }
        if (v.getId() == R.id.authButton) {
            hideKeyboard();
            onCall();
        }
        if (v.getId() == R.id.forgot_pass) {
            hideKeyboard();
            startActivity(new Intent(THIS, ForgotPassword.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private boolean isValid() {
        if (username.getText().toString().equalsIgnoreCase(""))
            Utils.showDialog(THIS, "Invalid Username !");
        else if (password.getText().toString().equalsIgnoreCase(""))
            Utils.showDialog(THIS, "Password cannot be blank !");
        else
            return true;

        return false;
    }

    private void doLogin(final boolean isFBLogin, final String email,
                         final String pwd) {
        showProgressDialog("");

        SharedPreferences sp = getApplicationContext().getSharedPreferences(
                "MyPreferences", 0);
        String REG_ID = sp.getString(WebAccess.Pkey_DEVICE_ID, "12345");

        RequestParams param = new RequestParams();
        if (isFBLogin) {
            param.put("FBEmailAddress", email);

        } else {
            param.put("Password", pwd);
            param.put("UserName", email);
        }
        param.put("DeviceId", REG_ID);
        String url = WebAccess.MAIN_URL + WebAccess.LOGIN_URL;
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
                Utils.showDialog(THIS,
                        "Sorry,Internet connection problem occurs please try again later");
            }


            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                dismissProgressDialog();

                String response2 = new String(responseBody);
                if (response2 != null) {
                    JSONObject json;
                    try {
                        json = new JSONObject(response2);


                        if (json.optString("status").equalsIgnoreCase("1")) {
                            JSONObject obj = json.getJSONObject("data");
                            JSONObject arr = obj.getJSONObject("pendingAmount");
                            boolean isPendingAmount = false;
                            String pendingAmount = arr.optString("Amount");
                            if (pendingAmount != null && !pendingAmount.trim().equalsIgnoreCase("")) {
                                if (arr.optDouble("Amount") < 0) {
                                    isPendingAmount = true;
                                }
                            }
                            if (isPendingAmount) {
                                Utils.showDialog(THIS, "PLEASE VISIT ACCOUNT", "VISIT ACCOUNT", "CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.INSTABAIL_COMPANY_LOGIN_URL));
                                        startActivity(browserIntent);
                                    }
                                });

                            } else {
                                response = WebAccess.getResponse(response2);
                                if (response == null) {
                                    // callFacebookLogout(Login.this);
                                    Utils.showDialog(THIS,
                                            "Invalid username and password!");
                                } else {

                                    SharedPreferences sp = PreferenceManager
                                            .getDefaultSharedPreferences(THIS);

                                    SharedPreferences.Editor editor = sp.edit();

                                    try {
                                        response.setPassword(pwd);
                                        String user = ObjectSerializer
                                                .serialize(response);
                                        editor.putString("user", user);

                                        if (isFBLogin) {
                                            editor.putBoolean("isFbLogin", true);
                                        } else {
                                            editor.putBoolean("isFbLogin", false);
                                        }
                                        editor.commit();
                                        WebAccess.loginUser = true;
                                        startActivity(new Intent(THIS,
                                                MainActivity.class).putExtra(
                                                "user", user));
                                        // startActivity(new Intent(THIS,
                                        // SubscriptionActivity.class));
                                        finish();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                }
                            }


                        } else {
                            Utils.showDialog(THIS,
                                    "Invalid username and password!");
                        }
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            }

        });

        // response = WebAccess.doLogin(isFBLogin, email, pwd, REG_ID);
        // THIS.runOnUiThread(new Runnable() {
        //
        // @Override
        // public void run() {
        // dismissProgressDialog();
        //
        // });

    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void registerFCM() {
        try {
            String token = FirebaseInstanceId.getInstance().getToken();

            if (token != null) {
                SharedPreferences pref = getApplicationContext()
                        .getSharedPreferences("MyPreferences", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(WebAccess.Pkey_DEVICE_ID, token);
                REG_ID = token;
                editor.commit();
            } else {
                if (count < 3) {
                    count++;
                    registerFCM();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            boolean result = Utility.checkPermission(this);
            if (result) {
                if (loginBtnPressed) {
                    if (isValid()) {
                        if (Utils.isOnline())
                            doLogin(false, username.getText().toString(), password
                                    .getText().toString());
                        else
                            Utils.noInternetDialog(THIS);
                    }
                }
            }
        } else {
            Methods.showToast(this, "Permission Denny", Toast.LENGTH_SHORT);
        }

    }
    // @Override
    // public void call(Session session, SessionState state, Exception
    // exception) {
    // // TODO Auto-generated method stub
    //
    // }

}