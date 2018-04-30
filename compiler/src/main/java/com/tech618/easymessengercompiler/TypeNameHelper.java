package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created by zmy on 2018/4/7.
 */

public class TypeNameHelper
{
    public static TypeName typeNameOfIBinder()
    {
        return ClassName.get("android.os", "IBinder");
    }

    public static TypeName typeNameOfParcel()
    {
        return ClassName.get("android.os", "Parcel");
    }

    public static TypeName typeNameOfParcelable()
    {
        return ClassName.get("android.os", "Parcelable");
    }

    public static TypeName typeNameOfRemoteException()
    {
        return ClassName.get("android.os", "RemoteException");
    }

    public static TypeName typeNameOfContext()
    {
        return ClassName.get("android.content", "Context");
    }

    public static TypeName typeNameOfComponentName()
    {
        return ClassName.bestGuess("android.content.ComponentName");
    }
}
