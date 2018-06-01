package com.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by guyuchao on 18-4-4.
 */

public class MusicService extends Service {

    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}