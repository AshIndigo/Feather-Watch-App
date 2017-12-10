package com.ashindigo.watchprog;

import android.os.BatteryManager;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.BATTERY_SERVICE;

class BLEThread extends Thread {

    boolean running = true;
    static ArrayList<StatusBarNotification> notifs = new ArrayList<>();
    String title[] = new String[]{null, null, null, null};

    @Override
    public void run() {
        try {
            // Sends time
            // _should work_
            //Calendar cal = Calendar.getInstance();
            //BLEGattCallback.chara.setValue("T|" + (cal.get(Calendar.YEAR) - 2000) + "|" + cal.get(Calendar.MONTH) + "|" + cal.get(Calendar.DAY_OF_MONTH) + "|" + cal.get(Calendar.HOUR_OF_DAY) + "|" + cal.get(Calendar.MINUTE) + "|" + cal.get(Calendar.SECOND) + "|" + cal.get(Calendar.DAY_OF_WEEK)); // Byte conversion needed? // T|year|month|day|hour|minute|second|DOW|E - Time packet
            //MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
            while (running) {
                if (BLEGattCallback.chara != null && MainActivity.gattD != null) {
                    sleep(5000);
                    if (notifs.size() <= 0) {
                        BLEGattCallback.chara.setValue("B|" + Integer.toString(MainActivity.bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)) + "|E");
                        MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
                    }
                    if (notifs.size() > 0) {
                        title[0] = notifs.get(0).getNotification().extras.getString("android.title");
                        title[1] = (!notifs.isEmpty()) ? notifs.get(1).getNotification().extras.getString("android.title") : "null";
                        title[2] = (!notifs.isEmpty()) ? notifs.get(2).getNotification().extras.getString("android.title") : "null";
                        title[3] = (!notifs.isEmpty()) ? notifs.get(3).getNotification().extras.getString("android.title") : "null";
                        title[4] = (!notifs.isEmpty()) ? notifs.get(4).getNotification().extras.getString("android.title") : "null";
                        //for (int i = 0; notifs.size() > i; i++) {
                            // Notifs packet
                            // TODO: Check for 20 byte limit
                            //BLEGattCallback.chara.setValue("N|" + Integer.toString(i) + "|" + notifs.get(i).getNotification().extras.getString("android.title") + "|E"); // Just try notif title
                            for (int i = 0; i < 5; i++) {
                                BLEGattCallback.chara.setValue("N|" + title[i] + "|E");
                                MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
                                wait(500);
                            }
                            //notifs.remove(i);
                        //}
                        if (notifs.size() == 4) {
                            notifs.clear();
                        }
                    }
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
