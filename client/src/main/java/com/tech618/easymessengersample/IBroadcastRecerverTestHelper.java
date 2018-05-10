package com.tech618.easymessengersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by zmy on 2018/5/10.
 */

public class IBroadcastRecerverTestHelper
{
    public static final IBroadcastRecerverTestHelper instance = new IBroadcastRecerverTestHelper();

    private TheBroadcastReceiver mBroadcastReceiver;

    public void __init(Context appContext)
    {
        mBroadcastReceiver = new TheBroadcastReceiver();
        appContext.registerReceiver(mBroadcastReceiver, new IntentFilter(""));
    }

    class TheBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String key = intent.getStringExtra("");
            String methodName = intent.getStringExtra("");
            if (key.contentEquals(""))
            {

            }
        }
    }
}
