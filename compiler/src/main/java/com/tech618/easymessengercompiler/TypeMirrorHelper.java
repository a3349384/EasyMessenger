package com.tech618.easymessengercompiler;

import java.util.List;

import javax.lang.model.type.TypeKind;
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
}