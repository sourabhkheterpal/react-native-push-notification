package com.dieam.reactnativepushnotification.modules;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.security.SecureRandom;

import static com.dieam.reactnativepushnotification.modules.RNPushNotification.LOG_TAG;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.ComponentName;

public class RNPushNotificationPublisher extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "notificationId";

    @Override
    public void onReceive(final Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        long currentTime = System.currentTimeMillis();

        Log.i(LOG_TAG, "NotificationPublisher: " + id + ", Now Time: " + currentTime);

        final Bundle bundle = intent.getExtras();

        Log.v(LOG_TAG, "onMessageReceived: " + bundle);

        Application applicationContext = (Application) context.getApplicationContext();
        Intent intentReceived = new Intent("com.localNotification.RECEIVED");
        intentReceived.putExtras(bundle);
        PackageManager packageManager = applicationContext.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryBroadcastReceivers(intentReceived, 0);
        for (ResolveInfo info : infos) {
            ComponentName cn = new ComponentName(info.activityInfo.packageName,
                    info.activityInfo.name);
            intentReceived.setComponent(cn);
            applicationContext.sendBroadcast(intentReceived);
        }

        handleLocalNotification(context, bundle);
    }

    private void handleLocalNotification(Context context, Bundle bundle) {

        // If notification ID is not provided by the user for push notification, generate one at random
        if (bundle.getString("id") == null) {
            SecureRandom randomNumberGenerator = new SecureRandom();
            bundle.putString("id", String.valueOf(randomNumberGenerator.nextInt()));
        }

        Application applicationContext = (Application) context.getApplicationContext();
        RNPushNotificationHelper pushNotificationHelper = new RNPushNotificationHelper(applicationContext);
        
        Log.v(LOG_TAG, "sendNotification: " + bundle);

        pushNotificationHelper.sendToNotificationCentre(bundle);
    }
}