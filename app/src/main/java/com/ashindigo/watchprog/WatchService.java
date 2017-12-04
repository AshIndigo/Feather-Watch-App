package com.ashindigo.watchprog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class WatchService extends Service {
    BLEThread backThread = new BLEThread();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (!backThread.isAlive()) {
            backThread.start();
        }

    }

    @Override
    public void onDestroy() {
        backThread.running = false;
    }
}