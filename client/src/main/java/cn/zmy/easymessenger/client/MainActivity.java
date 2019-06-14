package cn.zmy.easymessenger.client;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.zmy.easymessenger.BooleanCallback;
import cn.zmy.easymessenger.ByteCallback;
import cn.zmy.easymessenger.CharCallback;
import cn.zmy.easymessenger.DoubleCallback;
import cn.zmy.easymessenger.FloatCallback;
import cn.zmy.easymessenger.IntCallback;
import cn.zmy.easymessenger.LongCallback;
import cn.zmy.easymessenger.ResultCallback;
import cn.zmy.easymessenger.ShortCallback;
import cn.zmy.easymessenger.VoidCallback;
import cn.zmy.easymessenger.common.Color;
import cn.zmy.easymessenger.common.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.viewAllTest).setOnClickListener(this);
        findViewById(R.id.viewVoidTest).setOnClickListener(this);
        findViewById(R.id.viewByteTest).setOnClickListener(this);
        findViewById(R.id.viewCharTest).setOnClickListener(this);
        findViewById(R.id.viewShortTest).setOnClickListener(this);
        findViewById(R.id.viewIntTest).setOnClickListener(this);
        findViewById(R.id.viewIntOverloadTest).setOnClickListener(this);
        findViewById(R.id.viewLongTest).setOnClickListener(this);
        findViewById(R.id.viewFloatTest).setOnClickListener(this);
        findViewById(R.id.viewDoubleTest).setOnClickListener(this);
        findViewById(R.id.viewBooleanTest).setOnClickListener(this);
        findViewById(R.id.viewStringTest).setOnClickListener(this);
        findViewById(R.id.viewParcelableTest).setOnClickListener(this);
        findViewById(R.id.viewPrimitiveListTest).setOnClickListener(this);
        findViewById(R.id.viewTypeListTest).setOnClickListener(this);
        findViewById(R.id.viewEnumTest).setOnClickListener(this);
        findViewById(R.id.viewNullTest).setOnClickListener(this);
        findViewById(R.id.broadcastTest).setOnClickListener(this);
        findViewById(R.id.booleanArrayTest).setOnClickListener(this);
        findViewById(R.id.byteArrayTest).setOnClickListener(this);
        findViewById(R.id.charArrayTest).setOnClickListener(this);
        findViewById(R.id.intArrayTest).setOnClickListener(this);
        findViewById(R.id.longArrayTest).setOnClickListener(this);
        findViewById(R.id.floatArrayTest).setOnClickListener(this);
        findViewById(R.id.doubleArrayTest).setOnClickListener(this);
        findViewById(R.id.parcelableArrayTest).setOnClickListener(this);

        ITestFunctionHelper.instance.__init(this);
        IBroadcastMessageHelper.instance.__init(this);
        BroadcastReceiverHelper.instance.__init(this.getApplicationContext());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.viewAllTest:
            {
                voidTest();
                byteTest();
                charTest();
                shortTest();
                intTest();
                intOverloadTest();
                longTest();
                floatTest();
                doubleTest();
                booleanTest();
                stringTest();
                parcelableTest();
                primitiveListTest();
                typeListTest();
                enumTest();
                nullTest();
                boolArrayTest();
                byteArrayTest();
                charArrayTest();
                intArrayTest();
                floatArrayTest();
                longArrayTest();
                doubleArrayTest();
                parcelableArrayTest();
                break;
            }
            case R.id.viewVoidTest:
            {
                voidTest();
                break;
            }
            case R.id.viewByteTest:
            {
                byteTest();
                break;
            }
            case R.id.viewCharTest:
            {
                charTest();
                break;
            }
            case R.id.viewShortTest:
            {
                shortTest();
                break;
            }
            case R.id.viewIntTest:
            {
                intTest();
                break;
            }
            case R.id.viewIntOverloadTest:
            {
                intOverloadTest();
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
            case R.id.viewDoubleTest:
            {
                doubleTest();
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
            case R.id.booleanArrayTest:
            {
                boolArrayTest();
                break;
            }
            case R.id.byteArrayTest:
            {
                byteArrayTest();
                break;
            }
            case R.id.charArrayTest:
            {
                charArrayTest();
                break;
            }
            case R.id.intArrayTest:
            {
                intArrayTest();
                break;
            }
            case R.id.floatArrayTest:
            {
                floatArrayTest();
                break;
            }
            case R.id.longArrayTest:
            {
                longArrayTest();
                break;
            }
            case R.id.doubleArrayTest:
            {
                doubleArrayTest();
                break;
            }
            case R.id.parcelableArrayTest:
            {
                parcelableArrayTest();
                break;
            }
        }
    }

    private void voidTest()
    {
        try
        {
            ITestFunctionHelper.instance.voidTest();
            log("sync void success");
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("sync void error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.voidTestAsync(new VoidCallback()
        {
            @Override
            public void onSuccess()
            {
                log("async void success");
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("async void error:" + ex.getMessage());
            }
        });
    }

    private void intTest()
    {
        try
        {
            log("init sync test: 1 + 2 =" + ITestFunctionHelper.instance.intTest(1, 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("int sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.intTestAsync(1, 2, new IntCallback()
        {
            @Override
            public void onSuccess(int result)
            {
                log(String.format("int async test: 1 + 2 = %d", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("int async test error:" + ex.getMessage());
            }
        });
    }

    private void intOverloadTest()
    {
        try
        {
            log("init overload sync test:" + ITestFunctionHelper.instance.intTest(1, 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("int overload sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.intTestAsync(1, new IntCallback()
        {
            @Override
            public void onSuccess(int result)
            {
                log("int overload async test:" + result);
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("int overload async test error:" + ex.getMessage());
            }
        });
    }

    private void byteTest()
    {
        try
        {
            log("byte sync test: 1 + 2 =" + ITestFunctionHelper.instance.byteTest((byte) 1, (byte) 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("byte sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.byteTestAsync((byte)1, (byte)2, new ByteCallback()
        {
            @Override
            public void onSuccess(byte result)
            {
                log(String.format("byte async test: 1 + 2 = %d", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("byte async test error:" + ex.getMessage());
            }
        });
    }

    private void charTest()
    {
        try
        {
            log("char sync test: '1' + '2' =" + ITestFunctionHelper.instance.charTest('1', '2'));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("char sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.charTestAsync('1', '2', new CharCallback()
        {
            @Override
            public void onSuccess(char result)
            {
                log(String.format("char async test: '1' + '2' =" + result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("char async test error:" + ex.getMessage());
            }
        });
    }

    private void shortTest()
    {
        try
        {
            log("short sync test: 1 + 2 =" + ITestFunctionHelper.instance.shortTest((short) 1, (short) 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("short sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.shortTestAsync((short) 1, (short) 2, new ShortCallback()
        {
            @Override
            public void onSuccess(short result)
            {
                log(String.format("short async test: 1 + 2 = %d", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log(String.format("short async error:" + ex.getMessage()));
            }
        });
    }

    private void longTest()
    {
        try
        {
            log("long sync test: 1 + 2 =" + ITestFunctionHelper.instance.longTest((long) 1, (long) 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("long sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.longTestAsync(1, 2, new LongCallback()
        {
            @Override
            public void onSuccess(long result)
            {
                log(String.format("long async test: 1 + 2 = %d", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log(String.format("long async error:" + ex.getMessage()));
            }
        });
    }

    private void floatTest()
    {
        try
        {
            log("float sync test: 1 + 2 =" + ITestFunctionHelper.instance.floatTest((float) 1, (float) 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("float sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.floatTestAsync(1, 2, new FloatCallback()
        {
            @Override
            public void onSuccess(float result)
            {
                log(String.format("float async test: 1 + 2 = %f", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log(String.format("float async error:" + ex.getMessage()));
            }
        });
    }

    private void doubleTest()
    {
        try
        {
            log("double sync test: 1 + 2 =" + ITestFunctionHelper.instance.doubleTest((double) 1, (double) 2));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("double sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.doubleTestAsync(1, 2, new DoubleCallback()
        {
            @Override
            public void onSuccess(double result)
            {
                log(String.format("double async test: 1 + 2 = %f", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log(String.format("double async error:" + ex.getMessage()));
            }
        });
    }

    private void booleanTest()
    {
        try
        {
            log("boolean sync test:" + ITestFunctionHelper.instance.booleanTest(false));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("boolean sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.booleanTestAsync(false, new BooleanCallback()
        {
            @Override
            public void onSuccess(boolean result)
            {
                log("boolean async test:" + result);
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log(String.format("boolean async error:" + ex.getMessage()));
            }
        });
    }

    private void stringTest()
    {
        try
        {
            log("string sync test: 'hello' + 'world' = " + ITestFunctionHelper.instance.stringTest("hello", "world"));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("string sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.stringTestAsync("hello", "world", new ResultCallback<String>()
        {
            @Override
            public void onSuccess(String result)
            {
                log(String.format("string async test: 'hello' + 'world' =  %s", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log(String.format("string async error:" + ex.getMessage()));
            }
        });
    }

    private void parcelableTest()
    {
        User user = new User();
        user.setName("Bob");
        user.setAge(11);
        try
        {
            User newUser = ITestFunctionHelper.instance.parcelableTest(user);
            log(String.format("parcelable sync test: name = %s;age = %d", newUser.getName(), newUser.getAge()));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("parcelable sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.parcelableTestAsync(user, new ResultCallback<User>()
        {
            @Override
            public void onSuccess(User result)
            {
                log(String.format("parcelable async test: name = %s;age = %d", result.getName(), result.getAge()));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("parcelable async test error:" + ex.getMessage());
            }
        });
    }

    private void primitiveListTest()
    {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        try
        {
            List<Integer> newList = ITestFunctionHelper.instance.primitiveListTest(list);
            log("primitiveList sync test:" + newList);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("primitiveList sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.primitiveListTestAsync(list, new ResultCallback<List<Integer>>()
        {
            @Override
            public void onSuccess(List<Integer> result)
            {
                log(String.format("primitiveList async test:%s", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("primitiveList async test error:" + ex.getMessage());
            }
        });
    }

    private void typeListTest()
    {
        List<User> list = new ArrayList<>();
        User user1 = new User();
        user1.setName("Alice");
        user1.setAge(12);
        list.add(user1);
        try
        {
            List<User> newList = ITestFunctionHelper.instance.typeListTest(list);
            log("typeList sync test:" + newList);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("typeList sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.typeListTestAsync(list, new ResultCallback<List<User>>()
        {
            @Override
            public void onSuccess(List<User> result)
            {
                log(String.format("typeList async test:%s", result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("typeList async test error:" + ex.getMessage());
            }
        });
    }

    private void enumTest()
    {
        try
        {
            log("enum sync test:" + ITestFunctionHelper.instance.enumTest(Color.GREEN));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("enum sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.enumTestAsync(Color.GREEN, new ResultCallback<Color>()
        {
            @Override
            public void onSuccess(Color result)
            {
                log("enum async test:" + result);
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("enum async test error:" + ex.getMessage());
            }
        });
    }

    private void nullTest()
    {
        try
        {
            ITestFunctionHelper.instance.nullTest(null);
            log("null sync test success");
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("null sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.nullTestAsync(null, new ResultCallback<User>()
        {
            @Override
            public void onSuccess(User result)
            {
                log("null async test success");
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("null async test error:" + ex.getMessage());
            }
        });
    }

    private void broadcastTest()
    {
        IBroadcastMessageHelper.instance.test();
    }

    private void boolArrayTest()
    {
        try
        {
            boolean[] result = ITestFunctionHelper.instance.booleanArrayTest(new boolean[]{false}, new boolean[]{true});
            log("boolArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("boolArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.booleanArrayTestAsync(new boolean[]{false}, new boolean[]{true}, new ResultCallback<boolean[]>()
        {
            @Override
            public void onSuccess(boolean[] result)
            {
                log("boolArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("boolArray async test error:" + ex.getMessage());
            }
        });
    }

    private void byteArrayTest()
    {
        try
        {
            byte[] result = ITestFunctionHelper.instance.byteArrayTest(new byte[]{(byte)1}, new byte[]{(byte) 2});
            log("byteArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("byteArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.byteArrayTestAsync(new byte[]{(byte)1}, new byte[]{(byte) 2}, new ResultCallback<byte[]>()
        {
            @Override
            public void onSuccess(byte[] result)
            {
                log("byteArray sync test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("byteArray async test error:" + ex.getMessage());
            }
        });
    }

    private void charArrayTest()
    {
        try
        {
            char[] result = ITestFunctionHelper.instance.charArrayTest(new char[]{'a'}, new char[]{'b'});
            log("charArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("charArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.charArrayTestAsync(new char[]{'a'}, new char[]{'b'}, new ResultCallback<char[]>()
        {
            @Override
            public void onSuccess(char[] result)
            {
                log("charArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("charArray async test error:" + ex.getMessage());
            }
        });
    }

    private void intArrayTest()
    {
        try
        {
            int[] result = ITestFunctionHelper.instance.intArrayTest(new int[]{1}, new int[]{2});
            log("intArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("intArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.intArrayTestAsync(new int[]{1}, new int[]{2}, new ResultCallback<int[]>()
        {
            @Override
            public void onSuccess(int[] result)
            {
                log("intArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
                log("intArray async test error:" + e.getMessage());
            }
        });
    }

    private void longArrayTest()
    {
        try
        {
            long[] result = ITestFunctionHelper.instance.longArrayTest(new long[]{1}, new long[]{2});
            log("longArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("longArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.longArrayTestAsync(new long[]{1}, new long[]{2}, new ResultCallback<long[]>()
        {
            @Override
            public void onSuccess(long[] result)
            {
                log("longArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
                log("longArray async test error:" + e.getMessage());
            }
        });
    }

    private void floatArrayTest()
    {
        try
        {
            float[] result = ITestFunctionHelper.instance.floatArrayTest(new float[]{1}, new float[]{2});
            log("floatArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("floatArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.floatArrayTestAsync(new float[]{1}, new float[]{2}, new ResultCallback<float[]>()
        {
            @Override
            public void onSuccess(float[] result)
            {
                log("floatArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("floatArray async test error:" + ex.getMessage());
            }
        });
    }

    private void doubleArrayTest()
    {
        try
        {
            double[] result = ITestFunctionHelper.instance.doubleArrayTest(new double[]{1}, new double[]{2});
            log("doubleArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("floatArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.doubleArrayTestAsync(new double[]{1}, new double[]{2}, new ResultCallback<double[]>()
        {
            @Override
            public void onSuccess(double[] result)
            {
                log("doubleArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("doubleArray async test error:" + ex.getMessage());
            }
        });
    }

    private void parcelableArrayTest()
    {
        User[] users1 = new User[]{new User("张三", 20)};
        User[] users2 = new User[]{new User("李四", 30)};
        try
        {
            User[] result = ITestFunctionHelper.instance.parcelableArrayTest(users1, users2);
            log("parcelableArray sync test:" + Arrays.toString(result));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            log("parcelableArray sync test error:" + e.getMessage());
        }
        ITestFunctionHelper.instance.parcelableArrayTestAsync(users1, users2, new ResultCallback<User[]>()
        {
            @Override
            public void onSuccess(User[] result)
            {
                log("parcelableArray async test:" + Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {
                ex.printStackTrace();
                log("parcelableArray async test error:" + ex.getMessage());
            }
        });
    }

    private void log(final String s)
    {
        Log.d(TAG, s);
//        runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
