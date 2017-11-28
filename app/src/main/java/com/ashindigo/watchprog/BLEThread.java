package com.ashindigo.watchprog;

import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.Calendar;

class BLEThread extends Thread {

    boolean running = true;
    static ArrayList<StatusBarNotification> notifs = new ArrayList<>();

    @Override
    public void run() {
        // Sends time
        // _should work_
        //Calendar cal = Calendar.getInstance();
        //BLEGattCallback.chara.setValue("T|" + (cal.get(Calendar.YEAR) - 2000) + "|" + cal.get(Calendar.MONTH) + "|" + cal.get(Calendar.DAY_OF_MONTH) + "|" + cal.get(Calendar.HOUR_OF_DAY) + "|" + cal.get(Calendar.MINUTE) + "|" + cal.get(Calendar.SECOND) + "|" + cal.get(Calendar.DAY_OF_WEEK)); // Byte conversion needed? // T|year|month|day|hour|minute|second|DOW|E - Time packet
        //MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
        while (running) {
            if (BLEGattCallback.chara != null && MainActivity.gattD != null) {
                BLEGattCallback.chara.setValue("B|" + "99");
                 MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
                if (notifs.size() > 0) {
                    for (int i = 0; notifs.size() > i; i++) {
                        // Notifs packet
                        // TODO: Check for 20 byte limit
                        //BLEGattCallback.chara.setValue(NotificationParser.parseNotification(notifs.get(i)));
                        BLEGattCallback.chara.setValue("N|" + notifs.get(i).getNotification().extras.getString("android.title") + "| "); // Just try notif title
                        //BLEGattCallback.chara.setValue("N|" + notifs.get(i).getNotification().extras.getString("android.title") + "|" + notifs.get(i).getNotification().extras.getString("android.text"));
                        MainActivity.gattD.writeCharacteristic(BLEGattCallback.chara);
                        notifs.remove(i);
                    }
                }
            }

        }

    }
}
