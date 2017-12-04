package com.ashindigo.watchprog;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationParser {

    //final byte maxLength = 20;

    /**
     * Parses a {@link StatusBarNotification}
     * @param sbn The notification to parse
     * @return The properly formatted string for the notification
     */
    // Format on screen
    // 1. Email: 1 (Just a counter)
    // 1.5 Email: 1 - NEW (NEW stays there for 1 min)
    // 2. Email: Youtube, TestMan, Gmail (Shows who its from, issue is that the line could run out of space)
    // 2.5 Email: Youtube (One title per line derivative of 2)
    // 3. Email: TestMan - Lorem ipsum docet [...] (Shows the name, and as much of the email as possible withen the size limit)
    public static String parseNotification(StatusBarNotification sbn) {
        String str = "";
        Log.i("WatchProg", sbn.getNotification().category);
        switch (sbn.getNotification().category) {
            case Notification.CATEGORY_EMAIL:  break; // Format: 1.5
            case Notification.CATEGORY_MESSAGE: str = "N|" + sbn.getNotification().extras.getString("android.title") + "|"; break; // Format 1.5
            case Notification.CATEGORY_SOCIAL:  break; // Format 1.5
            //case Notification.CATEGORY_SYSTEM
            default: break; // Format 3(?)
        };

        return str;
    }
}

