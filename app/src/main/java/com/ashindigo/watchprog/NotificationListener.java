package com.ashindigo.watchprog;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Log.i("Notif Test Add", sbn.getPackageName());
        BLEThread.notifs.add(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        Log.i("Notif Test Remove", sbn.getPackageName());
        BLEThread.notifs.remove(sbn);
    }

}
