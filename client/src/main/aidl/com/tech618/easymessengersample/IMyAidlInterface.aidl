// IMyAidlInterface.aidl
package com.tech618.easymessengersample;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    int add(int num1, int num2);

    int test(char c);

    boolean boolTest(boolean b);
}
