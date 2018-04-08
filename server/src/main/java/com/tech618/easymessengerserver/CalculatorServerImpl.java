package com.tech618.easymessengerserver;

import android.os.RemoteException;
import android.util.Log;

import com.tech618.easymessenger.BinderInterfaceImpl;
import com.tech618.easymessengerclientservercommon.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmy on 2018/4/6.
 */
@BinderInterfaceImpl
public class CalculatorServerImpl
{
    public static final String TAG = "CalculatorServerImpl";

    public void voidTest()
    {
        Log.d(TAG, "voidTest called!");
    }

    public int intTest(int num1, int num2)
    {
        return num1 + num2;
    }

    public byte byteTest(byte b1, byte b2)
    {
        return (byte) (b1 + b2);
    }

    public long longTest(long l1, long l2)
    {
        return l1 + l2;
    }

    public float floatTest(float f1, float f2)
    {
        return f1 + f2;
    }

    public String stringTest(String s1, String s2)
    {
        return s1 + s2;
    }

    public User parcelableTest(User user)
    {
        User user1 = new User();
        user1.setName(user.getName());
        user1.setAge(user.getAge());

        return user1;
    }

    public List<Integer> primitiveListTest(List<Integer> list)
    {
        return new ArrayList<>(list);
    }

    public List<User> typeListTest(List<User> list)
    {
        return new ArrayList<>(list);
    }

    private void privateTest()
    {

    }
}

