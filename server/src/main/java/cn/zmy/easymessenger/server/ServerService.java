package cn.zmy.easymessenger.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by zmy on 2018/4/6.
 */

public class ServerService extends Service
{
    @Override
    public IBinder onBind(Intent intent)
    {
        return new TestFunctionImplBinder();
    }
}
