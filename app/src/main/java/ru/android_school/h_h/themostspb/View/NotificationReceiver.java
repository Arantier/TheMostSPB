package ru.android_school.h_h.themostspb.View;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import ru.android_school.h_h.themostspb.BridgePresenter;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.InfoActivity.BridgeInfoActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String TAG = "ru.android_school.h_h.themostspb.NOTIFY_DIVORSE";
    public static final String ID = "notification_intent_id";
    public static final String NAME = "notification_intent_name";
    public static final String MINUTES = "notification_intent_minutes";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case TAG:
                int id = intent.getIntExtra(ID, -1);
                String name = intent.getStringExtra(NAME);
                int minutesToCall = intent.getIntExtra(MINUTES, 0);

                NotificationCompat.Builder builder;
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                String notificationText = "До развода ";
                if (minutesToCall < 60) {
                    String mins = context.getResources().getQuantityString(R.plurals.minute_plurals, minutesToCall, minutesToCall);
                    notificationText += mins;
                } else {
                    String hours = context.getResources().getQuantityString(R.plurals.hours_plurals, minutesToCall, minutesToCall);
                    notificationText += hours;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = "" + id;
                    NotificationChannel notificationChannel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
                    notificationChannel.setDescription(notificationText);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    manager.createNotificationChannel(notificationChannel);
                    builder = new NotificationCompat.Builder(context, channelId);
                } else {
                    builder = new NotificationCompat.Builder(context);
                }

                Intent launchInfoIntent = new Intent(context, BridgeInfoActivity.class);
                launchInfoIntent.putExtra(BridgeInfoActivity.ID_EXTRA, id);
                PendingIntent wrapPIntent = PendingIntent.getActivity(context, id, launchInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification bridgeNotification = builder.setSmallIcon(R.drawable.ic_adb_black_24dp)
                        .setContentTitle(name)
                        .setContentText(notificationText)
                        .setAutoCancel(true)
                        .setContentIntent(wrapPIntent)
                        .build();
                manager.notify(id, bridgeNotification);

                SharedPreferences bridgesAndTimes = context.getSharedPreferences(BridgePresenter.SHARED_PREFERENCES_TIMES,Context.MODE_PRIVATE);
                bridgesAndTimes.edit()
                        .remove(id+"")
                        .apply();
        }
    }
}
