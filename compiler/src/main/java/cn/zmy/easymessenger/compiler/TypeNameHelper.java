package cn.zmy.easymessenger.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
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

    public static TypeName typeNameOfBinder()
    {
        return ClassName.get("android.os", "Binder");
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

    public static TypeName typeNameOfIntent()
    {
        return ClassName.bestGuess("android.content.Intent");
    }

    public static TypeName typeNameOfBroadcastReceiver()
    {
        return ClassName.bestGuess("android.content.BroadcastReceiver");
    }

    public static TypeName typeNameOfIntentFilter()
    {
        return ClassName.bestGuess("android.content.IntentFilter");
    }

    public static TypeName typeNameOfSerializable()
    {
        return ClassName.get("java.io", "Serializable");
    }

    public static TypeName typeNameOfList()
    {
        return ClassName.get("java.util", "List");
    }

    public static TypeName typeNameOfBaseClientHelper(String clientName)
    {
        ClassName base = ClassName.get("cn.zmy.easymessenger",
                "BaseClientHelper");
        ClassName parameter = ClassName.bestGuess(clientName);
        return ParameterizedTypeName.get(base, parameter);
    }

    public static TypeName typeNameOfAbsBinderProvider()
    {
        return ClassName.bestGuess("cn.zmy.easymessenger.contentprovider.AbsBinderProvider");
    }
}
