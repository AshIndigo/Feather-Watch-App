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
            // Sends time
            // _should work_
            if (BLEGattCallback.chara != null && MainActivity.gattD != null) {
                Calendar cal = Calendar.getInstance();
                BLEGattCallback.chara.setValue("T|" + (cal.get(Calendar.YEAR) - 2000) + "|" + cal.get(Calendar.MONTH) + "|" + cal.get(Calendar.DAY_OF_MONTH) + "|" + cal.get(Calendar.HOUR_OF_DAY) + "|" + cal.get(Calendar.MINUTE) + "|" + cal.get(Calendar.SECOND) + "|" + cal.get(Calendar.DAY_OF_WEEK)); // Byte conversion needed? // T|year|month|day|hour|minute|second|DOW|E - Time packet
                MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
            }
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
