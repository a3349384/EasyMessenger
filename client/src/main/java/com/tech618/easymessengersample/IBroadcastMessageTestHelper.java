package com.tech618.easymessengersample;

import android.content.Context;
import android.content.Intent;

/**
 * Created by zmy on 2018/5/10.
 */

public class IBroadcastMessageTestHelper
{
    public static final IBroadcastMessageTestHelper instance = new IBroadcastMessageTestHelper();

    private Context mAppContext;
    private String mBroadcastKey;

    public void __init(Context appContext)
    {
        mAppContext = appContext;
    }

    public void test()
    {
        Intent intent = new Intent();
        intent.putExtra("broadcastKey", mBroadcastKey);
        intent.putExtra("methodName", "test");
        mAppContext.sendBroadcast(intent);
    }

    public void testWithArgs(int num)
    {
        Intent intent = new Intent();
        intent.putExtra("broadcastKey", mBroadcastKey);
        intent.putExtra("methodName", "testWithArgs");
        intent.putExtra("num", num);
        intent.getStringExtra(null);
        mAppContext.sendBroadcast(intent);
    }
}
