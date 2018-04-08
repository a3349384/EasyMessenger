package com.tech618.easymessengersample;

import android.os.RemoteException;

import com.tech618.easymessenger.BinderInterface;

/**
 * Created by zmy on 2018/4/7.
 */
@BinderInterface
public interface ITestFunction
{
    void voidTest() throws RemoteException;

    int intTest(int num1, int num2) throws RemoteException;

    byte byteTest(byte b1, byte b2) throws RemoteException;

    long longTest(long l1, long l2) throws RemoteException;

    float floatTest(float f1, float f2) throws RemoteException;

    String stringTest(String s1, String s2) throws RemoteException;
}

