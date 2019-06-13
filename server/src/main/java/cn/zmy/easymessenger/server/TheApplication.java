package cn.zmy.easymessenger.server;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by zmy on 2019/6/13.
 */
public class TheApplication extends Application
{
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        Log.d("TheApplication", "attachBaseContext");
   }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("TheApplication", "onCreate");
    }
}
