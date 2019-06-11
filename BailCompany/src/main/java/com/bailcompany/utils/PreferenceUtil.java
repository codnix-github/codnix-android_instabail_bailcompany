package com.bailcompany.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Codnix on 05/08/2019.
 */

public class PreferenceUtil {

    private static final String NOTIFICATION_COUNTER = "notification_counter";

    SharedPreferences sp;
    Context context;

    public PreferenceUtil(Context context) {
        sp = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

      public int getUnreadNotificationCount() {

        try {
            return sp.getInt(NOTIFICATION_COUNTER, 0);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;


    }

    public void setUnreadNotificationCount(int newcount) {
        try {

            int counter = getUnreadNotificationCount();
            SharedPreferences.Editor editor = sp.edit();

            editor.putInt(NOTIFICATION_COUNTER, counter + newcount);
            editor.apply();
            editor.commit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    public void resetNotificationCount() {
        try {
            SharedPreferences.Editor editor = sp.edit();

            editor.putInt(NOTIFICATION_COUNTER, 0);
            editor.apply();
            editor.commit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
