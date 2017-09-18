package com.bailcompany.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Toast;

public class Methods {

	static Handler h;
	public static Boolean doanimation = true;

	public static void showAlert(final Context c, final String title,
			final String message) {
		Handler h = new Handler();
		h.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				builder.setTitle(title).setMessage(message).setCancelable(true)
						.setPositiveButton("OK", null);
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
		});
	}

	public static void showAlert(final Context c, final String title,
			final String message, final DialogInterface.OnClickListener ocl) {
		Handler h = new Handler();
		h.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				builder.setTitle(title).setMessage(message).setCancelable(true)
						.setPositiveButton("OK", ocl);
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
		});
	}

	public static void showToast(final Context c, final String message,
			final int duration) {
		Handler h = new Handler();
		h.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(c, message, duration).show();
			}
		});
	}

}
