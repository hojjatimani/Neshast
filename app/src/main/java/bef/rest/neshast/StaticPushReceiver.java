package bef.rest.neshast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import rest.bef.Befrest;
import rest.bef.BefrestMessage;
import rest.bef.BefrestPushReceiver;
import rest.bef.FileLog;

/**
 * Created by hojjatimani on 1/25/2016 AD.
 */
public class StaticPushReceiver extends BefrestPushReceiver {
    @Override
    public void onPushReceived(Context context, BefrestMessage[] messages) {
        for (BefrestMessage message : messages) {
            processMsg(context, message);
        }
    }

    private void processMsg(Context context, BefrestMessage message) {
        JSONObject jsObject = null;
        try {
            jsObject = new JSONObject(message.getData());
            switch (jsObject.getString(Util.SIGNAL)) {
                case "1":
                    Util.enableIntroductionBtn(context);
                    break;
                case "2":
                    Util.disableIntroductionBtn(context);
                    break;
                case "3":
                    showNotification(context, "" + jsObject.getString(Util.MESSAGE), 0, ActivitySignUp.class);
                    break;
                case "4":
                    showNotification(context, "" + jsObject.getString(Util.MESSAGE), 1, ActivitySignUp.class);
                    break;
                case "5":
                    showNotification(context, "" + jsObject.getString(Util.MESSAGE), 2, ActivitySignUp.class);
                    break;
                case "100":
                    Util.addMessageToChatTable(context, "" + jsObject.getString(Util.MESSAGE), true, System.currentTimeMillis());
                    if (!ApplicationLoader.isChatting)
                        showNotification(context, "پیام جدیدی از بفرست دارید!", 3, ActivityChat.class);
                    break;
                default:
                    Util.unlockContent(context, Integer.valueOf(jsObject.getString(Util.SIGNAL)) % 10);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(Context context, String msg, int id, Class<?> activityToShow) {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setContentTitle("پیام از بفرست!")
//                .setContentText(msg)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setAutoCancel(true);
//        NotificationManager nofitManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        nofitManager.notify(0, mBuilder.build());


//        int icon = R.mipmap.ic_launcher;
//        long when = System.currentTimeMillis();
//
//        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
//        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
//        contentView.setTextViewText(R.id.title, "Custom notification");
//        contentView.setTextViewText(R.id.text, "This is a custom layout");
//        notification.contentView = contentView;
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        notification.contentIntent = contentIntent;
//
//        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
//        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
//        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
//
//        mNotificationManager.notify(1, notification);


        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_custom);

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, activityToShow);

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                        // Set Ticker Message
                .setTicker("پیام از بفرست!")
                        // Dismiss Notification
                .setAutoCancel(true)
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Set RemoteViews into Notification
                .setContent(remoteViews)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 300, 50, 300});

        // Locate and set the Text into customnotificationtext.xml TextViews
//        remoteViews.setTextViewText(R.id.title, "پیام از بفرست");

        remoteViews.setTextViewText(R.id.text, msg);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(id, builder.build());
    }
}
