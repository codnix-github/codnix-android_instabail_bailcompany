package com.bailcompany;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.bailcompany.events.NotificationReceived;
import com.bailcompany.utils.PreferenceUtil;
import com.bailcompany.web.WebAccess;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;


public class FiberBaseMessagingServices extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    private static final String TAG = "FirebaseMsgServices";
    private NotificationManager notificationManager;

    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("update_drawer");

        // put whatever data you want to send, if any
        intent.putExtra("message", message);

        // send broadcast
        context.sendBroadcast(intent);
    }

    @Override
    public void onNewToken(String refreshedToken) {

        final Intent intent = new Intent("tokenReceiver");

        // You can also include some extra data.
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        intent.putExtra("token", refreshedToken);

        broadcastManager.sendBroadcast(intent);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences pref = getApplicationContext()
                .getSharedPreferences("MyPreferences", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(WebAccess.Pkey_DEVICE_ID, token);
        editor.commit();
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

    public void showNotifications1(String message) {

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

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, builder.build());
    }

    public void showNotifications(String message) {

        PreferenceUtil pref = new PreferenceUtil(getApplicationContext());
        pref.setUnreadNotificationCount(1);
        EventBus.getDefault().post(new NotificationReceived.NotificationReceivedEvent(message, WebAccess.type));

        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

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

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), ADMIN_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("InstaBail")
                        //.setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0, 500, 1000})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        //  .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getPackageName()+"/"+R.raw.bells_message))
                        //  .setSound(defaultSoundUri)
                        //    .setSound(Uri.parse("android.resource://com.bailcompany/"
                        //          + R.raw.bells_message))
                        .setContentIntent(pendingIntent);

        int notificationId = new Random().nextInt(60000);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.bells_message));
        }

        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        if (notificationManager != null) {
            List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

           /* for (int i = 0; channelList != null && i < channelList.size(); i++) {
                notificationManager.deleteNotificationChannel(channelList.get(i).getId());
            }*/
        }

        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.bells_message);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel mChannel = new NotificationChannel(ADMIN_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH);

        // Configure the notification channel.
        mChannel.setDescription(adminChannelDescription);
        mChannel.enableLights(true);
        mChannel.enableVibration(true);
        mChannel.setSound(sound, attributes); // This is IMPORTANT


        if (notificationManager != null)
            notificationManager.createNotificationChannel(mChannel);
    }
}

