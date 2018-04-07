package com.tech618.easymessengercompiler;

import java.util.List;
import java.util.Map;

import javafx.util.Pair;

/**
 * Created by zmy on 2018/4/5.
 */

public class MethodDescriptor
{
    private String methodName;
    private String methodReturnTypeName;
    private List<Pair<String, String>> methodParametersName;

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getMethodReturnTypeName()
    {
        return methodReturnTypeName;
    }

    public void setMethodReturnTypeName(String methodReturnTypeName)
    {
        this.methodReturnTypeName = methodReturnTypeName;
    }

    public List<Pair<String, String>> getMethodParametersName()
    {
        return methodParametersName;
    }

    public void setMethodParametersName(List<Pair<String, String>> methodParametersName)
    {
        this.methodParametersName = methodParametersName;
    }
}
