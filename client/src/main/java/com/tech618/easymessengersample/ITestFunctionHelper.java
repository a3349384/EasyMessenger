package com.tech618.easymessengersample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmy on 2018/4/27.
 */

public class ITestFunctionHelper
{
    public static final ITestFunctionHelper instance = new ITestFunctionHelper();

    private Context mContext;
    private ITestFunction mInterface;
    private List<Runnable> mWaitTasks;

    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mInterface = ITestFunctionClientImpl.asInterface(service);
            for (Runnable runnable : mWaitTasks)
            {
                runnable.run();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mInterface = null;
            __startBindService();
        }
    };

    public void __init(Context context)
    {
        mContext = context.getApplicationContext();
        mWaitTasks = new ArrayList<>();
    }

    public void __destory()
    {
        mContext.unbindService(mServiceConnection);
        mContext = null;
        mInterface = null;
        mWaitTasks.clear();
        mWaitTasks = null;
    }

    public void __startBindService()
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.tech618.easymessengerserver", "com.tech618.easymessengerserver.ServerService"));
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void voidTest() throws RemoteException
    {
        if (mInterface == null)
        {
            __startBindService();
            throw new RemoteException("Remote NOT ready!!!");
        }
        else
        {
            mInterface.voidTest();
        }
    }

    public int intTest(final int num1, final int num2) throws RemoteException
    {
        if (mInterface == null)
        {
            __startBindService();
            throw new RemoteException("Remote NOT ready!!!");
        }
        else
        {
            return mInterface.intTest(num1, num2);
        }
    }

    public void intTestAsync(final int num1, final int num2, final IntCallBack callBack)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int result;
                try
                {
                    result = mInterface.intTest(num1, num2);
                }
                catch (Exception ex)
                {
                    callBack.onError(ex);
                    return;
                }
                callBack.onSuccess(result);
            }
        };
        if (mInterface == null)
        {
            __startBindService();
            mWaitTasks.add(runnable);
        }
        else
        {
            runnable.run();
        }
    }

    public interface ResultCallBack<T>
    {
        void onSuccess(T result);

        void onError(Exception ex);
    }

    public interface IntCallBack
    {
        void onSuccess(int result);

        void onError(Exception ex);
    }
}
