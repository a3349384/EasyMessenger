package com.tech618.easymessengercompiler;

import javax.lang.model.type.TypeMirror;

/**
 * Created by zmy on 2018/4/8.
 */
public class TypeMirrorHelper
{
    public static String getParcelReadString(TypeMirror typeMirror)
    {
        String classString = typeMirror.toString();
        if (classString.contentEquals(int.class.getCanonicalName()))
        {
            return "readInt";
        }
        else if (classString.contentEquals(byte.class.getCanonicalName()))
        {
            return "readByte";
        }
        else if (classString.contentEquals(long.class.getCanonicalName()))
        {
            return "readLong";
        }
        else if (classString.contentEquals(float.class.getCanonicalName()))
        {
            return "readFloat";
        }
        else if (classString.contentEquals(String.class.getCanonicalName()))
        {
            return "readString";
        }
        else
        {
            return "null";
        }
    }

    public static String getParcelWriteString(TypeMirror typeMirror)
    {
        String className = typeMirror.toString();
        if (className.contentEquals(int.class.getCanonicalName()))
        {
            return "writeInt";
        }
        else if (className.contentEquals(byte.class.getCanonicalName()))
        {
            return "writeByte";
        }
        else if (className.contentEquals(long.class.getCanonicalName()))
        {
            return "writeLong";
        }
        else if (className.contentEquals(float.class.getCanonicalName()))
        {
            return "writeFloat";
        }
        else if (className.contentEquals(String.class.getCanonicalName()))
        {
            return "writeString";
        }
        else if (ClassHelper.isList(className))
        {
            return "writeList";
        }
        else
        {
            return "null";
        }
    }

    public static String getIntentReadString(String name, TypeMirror typeMirror)
    {
        String classString = typeMirror.toString();
        if (classString.contentEquals(int.class.getCanonicalName()))
        {
            return String.format("getIntExtra(\"%s\", 0)", name);
        }
        else if (classString.contentEquals(byte.class.getCanonicalName()))
        {
            return String.format("getByteExtra(\"%s\", (byte) 0)", name);
        }
        else if (classString.contentEquals(long.class.getCanonicalName()))
        {
            return String.format("getLongExtra(\"%s\", 0)", name);
        }
        else if (classString.contentEquals(float.class.getCanonicalName()))
        {
            return String.format("getFloatExtra(\"%s\", 0)", name);
        }
        else if (classString.contentEquals(String.class.getCanonicalName()))
        {
            return String.format("getStringExtra(\"%s\")", name);
        }
        else
        {
            return "null";
        }
    }

    public static String getJavaTypeStringByTypeKind(TypeMirror typeMirror)
    {
        String classStrng = typeMirror.toString();
        if (classStrng.contentEquals(int.class.getCanonicalName()))
        {
            return "int";
        }
        else if (classStrng.contentEquals(byte.class.getCanonicalName()))
        {
            return "byte";
        }
        else if (classStrng.contentEquals(long.class.getCanonicalName()))
        {
            return "long";
        }
        else if (classStrng.contentEquals(float.class.getCanonicalName()))
        {
            return "float";
        }
        else if (classStrng.contentEquals(String.class.getCanonicalName()))
        {
            return "String";
        }
        else
        {
            return "null";
        }
    }

    public static boolean isInt(TypeMirror typeMirror)
    {
        String className = typeMirror.toString();
        return className.contentEquals(int.class.getCanonicalName());
    }

    public static boolean isString(TypeMirror typeMirror)
    {
        String className = typeMirror.toString();
        return className.contentEquals(String.class.getCanonicalName());
    }

    public static boolean isParcelable(TypeMirror typeMirror)
    {
        String parcelableClassName = TypeNameHelper.typeNameOfParcelable().toString();
        return Global.types.isSubtype(typeMirror, Global.elements.getTypeElement(parcelableClassName).asType());
    }

    public static boolean isSerializable(TypeMirror typeMirror)
    {
        String serializableClassName = TypeNameHelper.typeNameOfSerializable().toString();
        return Global.types.isSubtype(typeMirror, Global.elements.getTypeElement(serializableClassName).asType());
    }

    public static boolean isList(TypeMirror typeMirror)
    {
        String listClassName = TypeNameHelper.typeNameOfList().toString();
        return Global.types.isSubtype(Global.types.erasure(typeMirror),
                Global.types.erasure(Global.elements.getTypeElement(listClassName).asType()));
    }
}
