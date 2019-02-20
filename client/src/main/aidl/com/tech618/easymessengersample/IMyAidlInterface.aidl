// IMyAidlInterface.aidl
package com.tech618.easymessengersample;
import com.tech618.easymessengersample.Book;

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
//    List<int> listTest(in List<int> list);

//    Book testBook(out Book book);
    int[] intArrayTest(inout int[] array1, inout int[] array2);

    Book[] parcelableArrayTest(inout Book[] array1, inout Book[] array2);
}
