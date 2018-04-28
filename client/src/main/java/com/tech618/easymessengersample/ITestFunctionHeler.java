package com.tech618.easymessengersample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.tech618.easymessengerclientservercommon.Color;
import com.tech618.easymessengerclientservercommon.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmy on 2018/4/27.
 */

public class ITestFunctionHeler
{
    public static final ITestFunctionHeler instance = new ITestFunctionHeler();

    private Context mContext;
    private ITestFunction mInterface;
    private List<Runnable> mWaitTasks;

    public ITestFunctionHeler()
    {
        mWaitTasks = new ArrayList<>();
    }

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
            mWaitTasks.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mInterface = null;
        }
    };

    public void init(Context context)
    {
        mContext = context.getApplicationContext();
    }

    private Intent getServiceIntent()
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.tech618.easymessengerserver", "com.tech618.easymessengerserver.ServerService"));

        return intent;
    }

    private void log(String s)
    {
        Log.d("ITestFunctionHeler", s);
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    public void voidTest()
    {
        if (mInterface == null)
        {
            mContext.bindService(new Intent(getServiceIntent()), mServiceConnection, Context.BIND_AUTO_CREATE);
            mWaitTasks.add(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        mInterface.voidTest();
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            try
            {
                mInterface.voidTest();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public int intTest(final int num1, final int num2)
    {
        if (mInterface == null)
        {
            mContext.bindService(new Intent(getServiceIntent()), mServiceConnection, Context.BIND_AUTO_CREATE);
            mWaitTasks.add(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        mInterface.intTest(num1, num2);
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            try
            {
                mInterface.intTest(num1, num2);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
