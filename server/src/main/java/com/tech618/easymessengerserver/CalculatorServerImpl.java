package com.tech618.easymessengerserver;

import com.tech618.easymessenger.BinderInterfaceImpl;

/**
 * Created by zmy on 2018/4/6.
 */
@BinderInterfaceImpl
public class CalculatorServerImpl
{
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
}

