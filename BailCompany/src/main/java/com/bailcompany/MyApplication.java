package com.bailcompany;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Shahid on 23-Jun-17.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);


    }
}
