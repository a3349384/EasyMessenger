package com.tech618.easymessengersample;

import com.tech618.easymessenger.BroadcastInterface;

/**
 * Created by zmy on 2018/5/10.
 */
@BroadcastInterface(key = "msg")
public interface IBroadcastMessage
{
    void test();
}
