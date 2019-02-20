package cn.zmy.easymessenger.client;

import cn.zmy.easymessenger.BroadcastInterface;
import cn.zmy.easymessenger.common.User;

/**
 * Created by zmy on 2018/5/10.
 */
@BroadcastInterface(key = "msg")
public interface IBroadcastMessage
{
    void test();

    void testWithArgs(int num);

    void testUsers(User[] users);
}
