package com.bailcompany;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.bailcompany.web.WebAccess;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class FiberBaseMessagingServices extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgServices";

    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("update_drawer");

        // put whatever data you want to send, if any
        intent.putExtra("message", message);

        // send broadcast
        context.sendBroadcast(intent);
    }
    // [END receive_message]

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        JSONObject data_OBJ = null;
        // Check if message contains a notification payload.

        if (remoteMessage.getData().size() > 0) {
            String data = remoteMessage.getData().toString();
            try {
                data_OBJ = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (data_OBJ != null) {
                try {
                    // String receiveMessage =
                    // b.getString(ConstantVariable.Key_Recieve_Message);
                    SharedPreferences prefs = getSharedPreferences("MyPreferences",
                            Context.MODE_PRIVATE);
                    final JSONObject messageObj = data_OBJ.optJSONObject(WebAccess.Key_Push_Message);
                    final String message = messageObj.optString("Message");
                    WebAccess.type = messageObj.getString("Type");
                    if (WebAccess.type != null) {
                        if (WebAccess.type.equalsIgnoreCase("6")) {
                            prefs = getSharedPreferences("MyPreferences",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("bond", true).commit();
                            WebAccess.tranferBondBadge = prefs.getBoolean("bond",
                                    false);
                            updateMyActivity(getApplicationContext(), WebAccess.type);
                        } else if (WebAccess.type.equalsIgnoreCase("8")) {
                            prefs = getSharedPreferences("MyPreferences",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = prefs.edit();
                            editor2.putBoolean("referrel", true).commit();
                            WebAccess.referBailBadge = prefs.getBoolean("referrel",
                                    false);
                            updateMyActivity(getApplicationContext(), WebAccess.type);
                        } else if (WebAccess.type.equalsIgnoreCase("10")) {
                            prefs = getSharedPreferences("MyPreferences",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor3 = prefs.edit();
                            editor3.putBoolean("fugitive", true).commit();
                            updateMyActivity(getApplicationContext(), WebAccess.type);
                            WebAccess.fugitiveBadge = prefs.getBoolean("fugitive",
                                    false);
                        }
                    }
                    if (WebAccess.type.equalsIgnoreCase("20")) {
                        final String title = messageObj.getString("title");
                        if (WebAccess.isIndividualChatOpen) {
                            String from = messageObj.getString("FromUserId");
                            if (WebAccess.indUserId != null) {
                                if (!MainActivity.user.getCompanyId()
                                        .equalsIgnoreCase(from)
                                        && !WebAccess.indUserId
                                        .equalsIgnoreCase(from)) {
                                    showNotifications(title
                                            + " sent you Instant Message please see Instant Chat");
                                    sentInstantTag();
                                }
                            }
                        } else {
                            showNotifications(title
                                    + " sent you Instant Message please see Instant Chat");
                            sentInstantTag();
                        }
                    } else if (WebAccess.type.equalsIgnoreCase("21")) {
                        final String title = messageObj.getString("title");
                        if (WebAccess.isGroupChatOpen) {
                            String from = messageObj.getString("RequestId");
                            if (WebAccess.grpReqId != null)
                                if (!WebAccess.grpReqId.equalsIgnoreCase(from)) {
                                    showNotifications(title
                                            + " sent you Instant Message please see fugitive request");
                                    sentFugitiveTag();
                                }
                        } else {
                            showNotifications(title
                                    + " sent you Instant Message please see fugitive request");
                            sentFugitiveTag();
                        }
                    } else if (WebAccess.type.equalsIgnoreCase("28")) {
                        final String title = messageObj.getString("title");
                        if (!WebAccess.isPersonalGroupChatOpen) {
                            showNotifications(title
                                    + " sent you Instant Group Message please see Instant Group Chat");
                            sentInstantGroupTag();
                        }
                    } else {
                        showNotifications(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String type) {
        Intent intent = null;
        intent = new Intent(this, MainActivity.class).putExtra("type", type);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo).setTicker("PEL Smart AC").setContentTitle(title)
                .setDefaults(Notification.DEFAULT_LIGHTS).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo))
                .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    void sentFugitiveTag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences",
                Context.MODE_PRIVATE);

        prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.putBoolean("fugitive", true).commit();
        WebAccess.fugitiveBadge = prefs.getBoolean("fugitive", false);
        updateMyActivity(getApplicationContext(), WebAccess.type);
    }

    void sentInstantGroupTag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences",
                Context.MODE_PRIVATE);

        prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.putBoolean("instantGroup", true).commit();
        WebAccess.instantGroup = prefs.getBoolean("instantGroup", false);
        updateMyActivity(getApplicationContext(), WebAccess.type);
    }

    void sentInstantTag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences",
                Context.MODE_PRIVATE);

        prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.putBoolean("instant", true).commit();
        WebAccess.instant = prefs.getBoolean("instant", false);
        updateMyActivity(getApplicationContext(), WebAccess.type);
    }

    public void showNotifications(String message) {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification note = new Notification(R.drawable.ic_launcher, message,
        // System.currentTimeMillis());
        Intent notificationIntent = null;

        if (WebAccess.loginUser) {
            notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        } else {
            notificationIntent = new Intent(getApplicationContext(), Login.class);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        // note.setLatestEventInfo(context, "InstaBail", message,
        // pendingIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());

        builder.setAutoCancel(true);
        builder.setContentTitle("InstaBail");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setSound(Uri.parse("android.resource://com.bailcompany/"
                + R.raw.bells_message));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE
                | NotificationCompat.DEFAULT_LIGHTS);
        builder.build();
        //
        // note.defaults |= Notification.DEFAULT_VIBRATE;
        // note.defaults |= Notification.DEFAULT_LIGHTS;
        // note.flags |= Notification.FLAG_AUTO_CANCEL;
        // note.sound = Uri.parse("android.resource://com.postingagent/"
        // + R.raw.bells_message);
        // note.priority = Notification.PRIORITY_HIGH;
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, builder.build());
    }
}

