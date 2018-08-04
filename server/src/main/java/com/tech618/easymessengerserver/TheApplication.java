package com.tech618.easymessengerserver;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by 82538 on 2018/6/4.
 */

public class TheApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("TheApplication", "attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TheApplication", "onCreate");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

