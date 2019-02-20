package com.tech618.easymessengerserver;

import android.util.Log;

import com.tech618.easymessenger.BinderServer;
import com.tech618.easymessengerclientservercommon.Color;
import com.tech618.easymessengerclientservercommon.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmy on 2018/4/6.
 */
@BinderServer
public class TestFunctionImpl
{
    public static final String TAG = "TestFunctionImpl";

    public void voidTest()
    {
        Log.d(TAG, "voidTest called!");
    }

    public byte byteTest(byte b1, byte b2)
    {
        return (byte) (b1 + b2);
    }

    public char charTest(char c1, char c2)
    {
        return (char) (c1 + c2);
    }

    public short shortTest(short s1, short s2)
    {
        return (short) (s1 + s2);
    }

    public int intTest(int num1, int num2)
    {
        return num1 + num2;
    }

    public long longTest(long l1, long l2)
    {
        return l1 + l2;
    }

    public float floatTest(float f1, float f2)
    {
        return f1 + f2;
    }

    public double doubleTest(double d1, double d2)
    {
        return d1 + d2;
    }

    public boolean booleanTest(boolean b)
    {
        return !b;
    }

    public String stringTest(String s1, String s2)
    {
        return s1 + s2;
    }

    public User parcelableTest(User user)
    {
        return new User("server:" + user.getName(), user.getAge());
    }

    public List<Integer> primitiveListTest(List<Integer> list)
    {
        return new ArrayList<>(list);
    }

    public List<User> typeListTest(List<User> list)
    {
        return new ArrayList<>(list);
    }

    public Color enumTest(Color color)
    {
        return Color.RED;
    }

    public User nullTest(User user)
    {
        if (user == null)
        {
            return null;
        }
        else
        {
            User user1 = new User();
            user1.setName(user.getName());
            user1.setAge(user.getAge());
            return user1;
        }
    }

    public boolean[] booleanArrayTest(boolean[] array1, boolean[] array2)
    {
        return new boolean[]{array1[0], array2[0]};
    }

    public byte[] byteArrayTest(byte[] array1, byte[] array2)
    {
        return new byte[]{array1[0], array2[0]};
    }

    public char[] charArrayTest(char[] array1, char[] array2)
    {
        return new char[]{array1[0], array2[0]};
    }

    public int[] intArrayTest(int[] array1, int[] array2)
    {
        return new int[]{array1[0], array2[0]};
    }

    public long[] longArrayTest(long[] array1, long[] array2)
    {
        return new long[]{array1[0], array2[0]};
    }

    public float[] floatArrayTest(float[] array1, float[] array2)
    {
        return new float[]{array1[0], array2[0]};
    }

    public double[] doubleArrayTest(double[] array1, double[] array2)
    {
        return new double[]{array1[0], array2[0]};
    }

    public User[] parcelableArrayTest(User[] array1, User[] array2)
    {
        return new User[]{array1[0], array2[0]};
    }

    private void privateTest()
    {

    }
}

