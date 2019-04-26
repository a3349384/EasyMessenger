package cn.zmy.easymessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Client Helper基类
 * */
public abstract class BaseClientHelper<T>
{
    protected Context mAppContext;
    protected ComponentName mServiceComponentName;
    protected List<Runnable> mWaitTasks;
    protected T mClient;
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClient = getClientWithBinder(service);
            for (Runnable runnable : mWaitTasks) {
                runnable.run();
            }
            mWaitTasks.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mClient = null;
            __startBindService();
        }
    };

    public void __init(Context context, ComponentName serviceComponentName) {
        mAppContext = context.getApplicationContext();
        mServiceComponentName = serviceComponentName;
        mWaitTasks = new ArrayList<>();
    }

    public void __destroy() {
        mAppContext.unbindService(mServiceConnection);
        mAppContext = null;
        mClient = null;
        mWaitTasks.clear();
        mWaitTasks = null;
    }

    public void __startBindService() {
        Intent intent = new Intent();
        intent.setComponent(mServiceComponentName);
        mAppContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public boolean __isServiceBind() {
        return mClient != null;
    }

    protected abstract T getClientWithBinder(IBinder binder);
}
