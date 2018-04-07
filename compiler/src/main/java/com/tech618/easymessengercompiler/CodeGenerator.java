package com.tech618.easymessengercompiler;

import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

import javafx.util.Pair;

/**
 * Created by zmy on 2018/4/5.
 */

public class CodeGenerator
{
    public static final CodeGenerator instance = new CodeGenerator();

    private Messager mMessager;

    public void setMessager(Messager messager)
    {
        mMessager = messager;
    }

    public void generatorCode(String packageName, String interfaceName, List<MethodDescriptor> methods)
    {
        log("packageName=" + packageName);
        log("interfaceName=" + interfaceName);
        for (MethodDescriptor methodDescriptor : methods)
        {
            StringBuilder methodStringBuilder = new StringBuilder();
            methodStringBuilder.append("method name=").append(methodDescriptor.getMethodName());
            methodStringBuilder.append(";method return type=").append(methodDescriptor.getMethodReturnTypeName());
            methodStringBuilder.append(";method parameters=[");
            for (Pair<String, String> pair : methodDescriptor.getMethodParametersName())
            {
                methodStringBuilder.append(pair.toString());
                methodStringBuilder.append(",");
            }
            methodStringBuilder.append("]");
            log(methodStringBuilder.toString());
        }

        try
        {
            Type type = Class.forName(packageName + "." + interfaceName);
            if (type == null)
            {
                log("error");
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            log("error");
        }
        String generateClassName = interfaceName + "Binder";
        TypeSpec binder = TypeSpec.classBuilder(generateClassName)
                                  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                  .build();
    }

    private void log(String log)
    {
        if (mMessager == null)
        {
            return;
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, log);
    }
}
