package com.tech618.easymessengercompiler;

/**
 * Created by zmy on 2018/4/8.
 */
public class ClassHelper
{
    public static boolean isThirdPartyClass(String className)
    {
        return className.contains(".") && !className.startsWith("java");
    }
}
