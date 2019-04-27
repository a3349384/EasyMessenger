package cn.zmy.easymessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Client Helper基类
 * */
public abstract class BaseClientHelper<T>
{
    private Context mAppContext;
    private ComponentName mServiceComponentName;
    private Queue<Runnable> mWaitTasks;
    protected T mClient;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClient = getClientWithBinder(service);
            while (!mWaitTasks.isEmpty()) {
                mWaitTasks.poll().run();
            }
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
        mWaitTasks = new ConcurrentLinkedQueue<>();
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

    public void __runAfterConnected(Runnable runnable)
    {
        mWaitTasks.add(runnable);
    }

    protected abstract T getClientWithBinder(IBinder binder);

    protected boolean checkClientAvailable() {
        if (mClient == null) {
            __startBindService();
        }
        return true;
    }
}
