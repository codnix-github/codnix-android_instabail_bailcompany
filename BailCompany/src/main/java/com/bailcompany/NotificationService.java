package com.bailcompany;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		// Declare the timer
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				getApplicationContext()
						.sendBroadcast(
								new Intent(
										"com.google.android.intent.action.GTALK_HEARTBEAT"));
				getApplicationContext()
						.sendBroadcast(
								new Intent(
										"com.google.android.intent.action.MCS_HEARTBEAT"));
			}
		};

		new Timer().scheduleAtFixedRate(timerTask, 1000, 120000);
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}
}
