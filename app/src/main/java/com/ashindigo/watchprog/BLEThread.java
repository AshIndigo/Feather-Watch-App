package com.ashindigo.watchprog;

import android.os.BatteryManager;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.BATTERY_SERVICE;

class BLEThread extends Thread {

    boolean running = true;
    static HashMap<String, String> notifs = new HashMap<>();

    @Override
    public void run() {
        try {
            while (running) {
                if (BLEGattCallback.chara != null && MainActivity.gattD != null) {
                    sleep(5000);
                    if (notifs.size() <= 0) {
                        BLEGattCallback.chara.setValue("B|" + Integer.toString(MainActivity.bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)) + "|E");
                        MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
                    }
                    if (notifs.size() > 0) {
                        for (int i = 0; notifs.size() > i; i++) {
                            // Notifs packet
                            // TODO: Check for 20 byte limit
                            String title = (String) ((HashMap.Entry) notifs.entrySet().toArray()[i]).getValue();
                            BLEGattCallback.chara.setValue("N|" + Integer.toString(i) + "|" + title + "|E"); // Just try notif title
                            MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
