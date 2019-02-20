package cn.zmy.easymessenger.client;

import android.util.Log;

import cn.zmy.easymessenger.BrodcastReceiver;
import cn.zmy.easymessenger.common.User;

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
