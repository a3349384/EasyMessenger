package com.tech618.easymessengersample;

import android.util.Log;
import android.widget.Toast;

import com.tech618.easymessenger.BrodcastReceiver;

/**
 * Created by zmy on 2018/5/10.
 */
@BrodcastReceiver(key = "msg")
public class MessageBroadcastReceiver
{
    public void test()
    {
        Log.d("MessageBroadcastReceiver", "test called");
    }
}
