package com.tech618.easymessengersample;

import com.tech618.easymessenger.BinderClient;
import com.tech618.easymessengerclientservercommon.Color;
import com.tech618.easymessengerclientservercommon.User;

import java.util.List;

/**
 * Created by zmy on 2018/4/7.
 */
@BinderClient
public interface ITestFunction
{
    void voidTest();

    byte byteTest(byte b1, byte b2);

    char charTest(char c1, char c2);

    boolean booleanTest(boolean b);

    short shortTest(short s1, short s2);

    int intTest(int num1, int num2);

    long longTest(long l1, long l2);

    float floatTest(float f1, float f2);

    double doubleTest(double d1, double d2);

    String stringTest(String s1, String s2);

    User parcelableTest(User user);

    List<Integer> primitiveListTest(List<Integer> list);

    List<User> typeListTest(List<User> list);

    Color enumTest(Color color);

    User nullTest(User user);

    boolean[] booleanArrayTest(boolean[] array1, boolean[] array2);

    byte[] byteArrayTest(byte[] array1, byte[] array2);

    char[] charArrayTest(char[] array1, char[] array2);

    int[] intArrayTest(int[] array1, int[] array2);

    long[] longArrayTest(long[] array1, long[] array2);

    float[] floatArrayTest(float[] array1, float[] array2);

    double[] doubleArrayTest(double[] array1, double[] array2);
}

