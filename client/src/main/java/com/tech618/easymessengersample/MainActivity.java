package com.tech618.easymessengersample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.viewVoidTest).setOnClickListener(this);
        findViewById(R.id.viewIntTest).setOnClickListener(this);
        findViewById(R.id.viewByteTest).setOnClickListener(this);
        findViewById(R.id.viewLongTest).setOnClickListener(this);
        findViewById(R.id.viewFloatTest).setOnClickListener(this);
        findViewById(R.id.viewStringTest).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.viewVoidTest:
            {
                voidTest();
                break;
            }
            case R.id.viewIntTest:
            {
                intTest();
                break;
            }
            case R.id.viewByteTest:
            {
                byteTest();
                break;
            }
            case R.id.viewLongTest:
            {
                longTest();
                break;
            }
            case R.id.viewFloatTest:
            {
                floatTest();
                break;
            }
            case R.id.viewStringTest:
            {
                stringTest();
                break;
            }
        }
    }

    private void voidTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    ITestFunctionClientImpl.asInterface(service).voidTest();
                    log("see log for detail!");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {

            }
        }, BIND_AUTO_CREATE);
    }

    private void intTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    log(String.format("%d + %d = %d", 1, 2, ITestFunctionClientImpl.asInterface(service).intTest(1, 2)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {

            }
        }, BIND_AUTO_CREATE);
    }

    private void byteTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    log(String.format("%d + %d = %d", 1, 2, ITestFunctionClientImpl.asInterface(service).byteTest((byte) 1, (byte) 2)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {

            }
        }, BIND_AUTO_CREATE);
    }

    private void longTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    log(String.format("%d + %d = %d", 1, 2, ITestFunctionClientImpl.asInterface(service).longTest(1, 2)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {

            }
        }, BIND_AUTO_CREATE);
    }

    private void floatTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    log(String.format("%f + %f = %f", 1f, 2f, ITestFunctionClientImpl.asInterface(service).floatTest(1f, 2f)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {

            }
        }, BIND_AUTO_CREATE);
    }

    private void stringTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    log(String.format("%s + %s = %s", "hello", "world", ITestFunctionClientImpl.asInterface(service).stringTest("hello", "world")));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {

            }
        }, BIND_AUTO_CREATE);
    }

    private Intent getServiceIntent()
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.tech618.easymessengerserver", "com.tech618.easymessengerserver.ServerService"));

        return intent;
    }

    private void log(String s)
    {
        Log.d(TAG, s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
