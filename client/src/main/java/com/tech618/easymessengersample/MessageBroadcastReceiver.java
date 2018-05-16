package com.tech618.easymessengersample;

import android.util.Log;

import com.tech618.easymessenger.BrodcastReceiver;
import com.tech618.easymessengerclientservercommon.User;

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

    public void testWithArgs(int num)
    {

    }

    public void testUser(User user)
    {

    }
}
