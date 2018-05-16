package com.tech618.easymessengersample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tech618.easymessenger.IntCallback;
import com.tech618.easymessengerclientservercommon.Color;
import com.tech618.easymessengerclientservercommon.User;

import java.util.ArrayList;
import java.util.List;

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
        findViewById(R.id.viewBooleanTest).setOnClickListener(this);
        findViewById(R.id.viewStringTest).setOnClickListener(this);
        findViewById(R.id.viewParcelableTest).setOnClickListener(this);
        findViewById(R.id.viewPrimitiveListTest).setOnClickListener(this);
        findViewById(R.id.viewTypeListTest).setOnClickListener(this);
        findViewById(R.id.viewEnumTest).setOnClickListener(this);
        findViewById(R.id.viewNullTest).setOnClickListener(this);
        findViewById(R.id.broadcastTest).setOnClickListener(this);

        ITestFunctionHelper.instance.__init(this, getServiceComponentName());
        IBroadcastMessageHelper.instance.__init(this);
        BroadcastReceiverHelper.instance.__init(this.getApplicationContext());
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
            case R.id.viewBooleanTest:
            {
                booleanTest();
                break;
            }
            case R.id.viewStringTest:
            {
                stringTest();
                break;
            }
            case R.id.viewParcelableTest:
            {
                parcelableTest();
                break;
            }
            case R.id.viewPrimitiveListTest:
            {
                primitiveListTest();
                break;
            }
            case R.id.viewTypeListTest:
            {
                typeListTest();
                break;
            }
            case R.id.viewEnumTest:
            {
                enumTest();
                break;
            }
            case R.id.viewNullTest:
            {
                nullTest();
                break;
            }
            case R.id.broadcastTest:
            {
                broadcastTest();
                break;
            }
        }
    }

    private void voidTest()
    {
        try {
            ITestFunctionTestHelper.instance.voidTest();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void intTest()
    {
        ITestFunctionHelper.instance.intTestAsync(1, 2, new IntCallback() {
            @Override
            public void onSuccess(int result) {
                Toast.makeText(MainActivity.this, "" + result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception ex) {

            }
        });
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
                    log(String.format("%d + %d = %d", 1, 2, ITestFunctionClient.fromBinder(service).byteTest((byte) 1, (byte) 2)));
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
                    log(String.format("%d + %d = %d", 1, 2, ITestFunctionClient.fromBinder(service).longTest(1, 2)));
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
                    log(String.format("%f + %f = %f", 1f, 2f, ITestFunctionClient.fromBinder(service).floatTest(1f, 2f)));
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

    private void booleanTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    log("" + ITestFunctionClient.fromBinder(service).booleanTest(false));
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
                    log(String.format("%s + %s = %s", "hello", "world", ITestFunctionClient.fromBinder(service).stringTest("hello", "world")));
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

    private void parcelableTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    User user = new User();
                    user.setName("Bob");
                    user.setAge(11);

                    User user1 = ITestFunctionClient.fromBinder(service).parcelableTest(user);
                    log(String.format("name = %s;age = %d", user1.getName(), user1.getAge()));
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

    private void primitiveListTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    List<Integer> list = new ArrayList<>();
                    list.add(1);
                    list.add(2);
                    List<Integer> list1 = ITestFunctionClient.fromBinder(service).primitiveListTest(list);
                    log(String.format("%s", list1.toString()));
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

    private void typeListTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    List<User> list = new ArrayList<>();
                    User user1 = new User();
                    user1.setName("Alice");
                    user1.setAge(12);
                    list.add(user1);

                    List<User> list1 = ITestFunctionClient.fromBinder(service).typeListTest(list);
                    log(String.format("%s", list1.toString()));
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

    private void enumTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    Color color = ITestFunctionClient.fromBinder(service).enumTest(Color.BLUE);
                    log(color.name());
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

    private void nullTest()
    {
        bindService(new Intent(getServiceIntent()), new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                try
                {
                    User user1 = ITestFunctionClient.fromBinder(service).nullTest(null);
                    if (user1 != null)
                    {
                        log(String.format("name = %s;age = %d", user1.getName(), user1.getAge()));
                    }
                    else
                    {
                        log("return user is null");
                    }
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

    private void broadcastTest()
    {
        IBroadcastMessageHelper.instance.test();
    }

    private Intent getServiceIntent()
    {
        Intent intent = new Intent();
        intent.setComponent(getServiceComponentName());

        return intent;
    }

    private ComponentName getServiceComponentName()
    {
        return new ComponentName("com.tech618.easymessengerserver", "com.tech618.easymessengerserver.ServerService");
    }

    private void log(String s)
    {
        Log.d(TAG, s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
