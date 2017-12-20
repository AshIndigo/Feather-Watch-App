package com.ashindigo.watchprog;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class NotificationListener extends NotificationListenerService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Log.i("Notif Test Add", sbn.getNotification().extras.getString("android.title"));
        BLEThread.notifs.put(sbn.getNotification().extras.getString("android.title"), sbn.getNotification().extras.getString("android.title"));
        Log.i("Notif List", BLEThread.notifs.toString());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        Log.i("Notif Test Remove", sbn.getNotification().extras.getString("android.title"));
        Log.i("Notif List", BLEThread.notifs.toString());
        if (BLEGattCallback.chara != null && MainActivity.gattD != null) {
            BLEGattCallback.chara.setValue("N|" + new ArrayList(BLEThread.notifs.keySet()).indexOf(sbn.getNotification().extras.getString("android.title")) + "|" + "null" + "|E"); // Just try notif title
            MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
        }
        BLEThread.notifs.remove(sbn.getNotification().extras.getString("android.title"));
    }

}
