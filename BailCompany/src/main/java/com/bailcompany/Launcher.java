package com.bailcompany;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.Methods;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;

public class Launcher extends CustomActivity {
    int selectype = 0;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.launch_screen);
        Utils.createNoMediaFile(Const.TEMP_PHOTO);
        setTouchNClick(R.id.signin);
        setTouchNClick(R.id.register);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText("Version : " + Utils.getVersion(getApplicationContext()));
        startService();
        Utility.checkNotificationPermission(THIS);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.signin) {
            selectype = 1;
            boolean result = Utility.checkNotificationPermission(THIS);
            if (result)
                startActivity(new Intent(THIS, Login.class));
//			startActivity(new Intent(THIS, Login.class));
        }
        if (v.getId() == R.id.register) {
            selectype = 2;
            boolean result = Utility.checkNotificationPermission(THIS);
            if (result)
                startActivity(new Intent(THIS, Register.class));
//			startActivity(new Intent(THIS, Register.class));
        }
    }

    public void startService() {
        // use this to start and trigger a service
     /*   Intent i = new Intent(Launcher.this, NotificationService.class);
        Launcher.this.startService(i);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            boolean result = Utility.checkNotificationPermission(THIS);
            if (result) {
                if (selectype == 1) {
                    startActivity(new Intent(THIS, Login.class));
                } else if (selectype == 2) {
                    startActivity(new Intent(THIS, Register.class));
                }
            }
        } else {
            Methods.showToast(this, "Permission Denny", Toast.LENGTH_SHORT);
        }

    }
}