package cn.zmy.easymessenger.client;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
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
        ITestFunctionHelper.instance.voidTestAsync(new VoidCallback()
        {
            @Override
            public void onSuccess()
            {
                Toast.makeText(MainActivity.this, "void success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void intTest()
    {
        ITestFunctionHelper.instance.intTestAsync(1, 2, new IntCallback()
        {
            @Override
            public void onSuccess(int result)
            {
                log(String.format("%d + %d = %d", 1, 2, result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void intOverloadTest()
    {
        ITestFunctionHelper.instance.intTestAsync(1, new IntCallback()
        {
            @Override
            public void onSuccess(int result)
            {
                log("" + result);
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void byteTest()
    {
        ITestFunctionHelper.instance.byteTestAsync((byte)1, (byte)2, new ByteCallback()
        {
            @Override
            public void onSuccess(byte result)
            {
                log(String.format("%d + %d = %d", 1, 2, result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void charTest()
    {
        ITestFunctionHelper.instance.charTestAsync('1', '2', new CharCallback()
        {
            @Override
            public void onSuccess(char result)
            {
                log(String.format("%c + %c = %c", '1', '2', result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void shortTest()
    {
        ITestFunctionHelper.instance.shortTestAsync((short) 1, (short) 2, new ShortCallback()
        {
            @Override
            public void onSuccess(short result)
            {
                log(String.format("%d + %d = %d", 1, 2, result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void longTest()
    {
        ITestFunctionHelper.instance.longTestAsync(1, 2, new LongCallback()
        {
            @Override
            public void onSuccess(long result)
            {
                log(String.format("%d + %d = %d", 1, 2, result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void floatTest()
    {
        ITestFunctionHelper.instance.floatTestAsync(1, 2, new FloatCallback()
        {
            @Override
            public void onSuccess(float result)
            {
                log(String.format("%f + %f = %f", 1f, 2f, result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void doubleTest()
    {
        ITestFunctionHelper.instance.doubleTestAsync(1, 2, new DoubleCallback()
        {
            @Override
            public void onSuccess(double result)
            {
                log(String.format("%f + %f = %f", 1d, 2d, result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void booleanTest()
    {
        ITestFunctionHelper.instance.booleanTestAsync(false, new BooleanCallback()
        {
            @Override
            public void onSuccess(boolean result)
            {
                log("" + result);
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void stringTest()
    {
        ITestFunctionHelper.instance.stringTestAsync("hello", "world", new ResultCallback<String>()
        {
            @Override
            public void onSuccess(String result)
            {
                log(String.format("%s + %s = %s", "hello", "world", result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void parcelableTest()
    {
        User user = new User();
        user.setName("Bob");
        user.setAge(11);
        ITestFunctionHelper.instance.parcelableTestAsync(user, new ResultCallback<User>()
        {
            @Override
            public void onSuccess(User result)
            {
                log(String.format("name = %s;age = %d", result.getName(), result.getAge()));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void primitiveListTest()
    {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        ITestFunctionHelper.instance.primitiveListTestAsync(list, new ResultCallback<List<Integer>>()
        {
            @Override
            public void onSuccess(List<Integer> result)
            {
                log(String.format("%s", result));
            }

            @Override
            public void onError(Exception ex)
            {

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
        ITestFunctionHelper.instance.typeListTestAsync(list, new ResultCallback<List<User>>()
        {
            @Override
            public void onSuccess(List<User> result)
            {
                log(String.format("%s", result.toString()));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void enumTest()
    {
        ITestFunctionHelper.instance.enumTestAsync(Color.GREEN, new ResultCallback<Color>()
        {
            @Override
            public void onSuccess(Color result)
            {
                log(result.name());
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void nullTest()
    {
        ITestFunctionHelper.instance.nullTestAsync(null, new ResultCallback<User>()
        {
            @Override
            public void onSuccess(User result)
            {
                if (result != null)
                {
                    log(String.format("name = %s;age = %d", result.getName(), result.getAge()));
                }
                else
                {
                    log("return user is null");
                }
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void broadcastTest()
    {
        IBroadcastMessageHelper.instance.test();
    }

    private void boolArrayTest()
    {
        ITestFunctionHelper.instance.booleanArrayTestAsync(new boolean[]{false}, new boolean[]{true}, new ResultCallback<boolean[]>()
        {
            @Override
            public void onSuccess(boolean[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void byteArrayTest()
    {
        ITestFunctionHelper.instance.byteArrayTestAsync(new byte[]{(byte)1}, new byte[]{(byte) 2}, new ResultCallback<byte[]>()
        {
            @Override
            public void onSuccess(byte[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void charArrayTest()
    {
        ITestFunctionHelper.instance.charArrayTestAsync(new char[]{'a'}, new char[]{'b'}, new ResultCallback<char[]>()
        {
            @Override
            public void onSuccess(char[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void intArrayTest()
    {
        ITestFunctionHelper.instance.intArrayTestAsync(new int[]{1}, new int[]{2}, new ResultCallback<int[]>()
        {
            @Override
            public void onSuccess(int[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void longArrayTest()
    {
        ITestFunctionHelper.instance.longArrayTestAsync(new long[]{1}, new long[]{2}, new ResultCallback<long[]>()
        {
            @Override
            public void onSuccess(long[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void floatArrayTest()
    {
        ITestFunctionHelper.instance.floatArrayTestAsync(new float[]{1}, new float[]{2}, new ResultCallback<float[]>()
        {
            @Override
            public void onSuccess(float[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void doubleArrayTest()
    {
        ITestFunctionHelper.instance.doubleArrayTestAsync(new double[]{1}, new double[]{2}, new ResultCallback<double[]>()
        {
            @Override
            public void onSuccess(double[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private void parcelableArrayTest()
    {
        User[] users1 = new User[]{new User("张三", 20)};
        User[] users2 = new User[]{new User("李四", 30)};
        ITestFunctionHelper.instance.parcelableArrayTestAsync(users1, users2, new ResultCallback<User[]>()
        {
            @Override
            public void onSuccess(User[] result)
            {
                log(Arrays.toString(result));
            }

            @Override
            public void onError(Exception ex)
            {

            }
        });
    }

    private Intent getServiceIntent()
    {
        Intent intent = new Intent();
        intent.setComponent(getServiceComponentName());

        return intent;
    }

    private ComponentName getServiceComponentName()
    {
        return new ComponentName("cn.zmy.easymessenger.server", "cn.zmy.easymessenger.server.ServerService");
    }

    private void log(String s)
    {
        Log.d(TAG, s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
