package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BooleanCallback;
import com.tech618.easymessenger.ByteCallback;
import com.tech618.easymessenger.CharCallBack;
import com.tech618.easymessenger.DoubleCallback;
import com.tech618.easymessenger.FloatCallback;
import com.tech618.easymessenger.IntCallback;
import com.tech618.easymessenger.LongCallback;
import com.tech618.easymessenger.ResultCallBack;
import com.tech618.easymessenger.ShortCallback;
import com.tech618.easymessenger.VoidCallback;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * Created by zmy on 2019/2/19.
 * 生成IPC通信Helper类
 */

public class HelperGenerator
{
    public static TypeSpec generateHelper(TypeElement binderInterfaceTypeElement, List<ExecutableElement> binderInterfaceMethodElements)
    {
        String helperClassName = getHelperFullName(binderInterfaceTypeElement);
        TypeName helperName = ClassName.bestGuess(helperClassName);
        TypeName clientName = ClassName.bestGuess(ClientGenerator.getClientFullName(binderInterfaceTypeElement));

        //单例
        FieldSpec instanceFiled = FieldSpec.builder(helperName, "instance", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                          .initializer("new $T()", helperName)
                                          .build();
        //相关全局字段
        FieldSpec contextFiled = FieldSpec.builder(TypeNameHelper.typeNameOfContext(), "mAppContext", Modifier.PROTECTED).build();
        FieldSpec componentNameField = FieldSpec.builder(TypeNameHelper.typeNameOfComponentName(), "mServiceComponentName", Modifier.PROTECTED).build();
        FieldSpec waitTaskField = FieldSpec.builder(ParameterizedTypeName.get(List.class, Runnable.class), "mWaitTasks", Modifier.PROTECTED).build();
        FieldSpec clientField = FieldSpec.builder(clientName, "mClient", Modifier.PROTECTED).build();
        //ServiceConnection
        TypeName serviceConnectionType = ClassName.get("android.content", "ServiceConnection");
        ParameterSpec componentNameParameter = ParameterSpec.builder(TypeNameHelper.typeNameOfComponentName(), "name").build();
        ParameterSpec ibinderParameter = ParameterSpec.builder(TypeNameHelper.typeNameOfIBinder(), "service").build();
        MethodSpec onServiceConnectedMethod = MethodSpec.methodBuilder("onServiceConnected")
                                                      .addAnnotation(Override.class)
                                                      .addModifiers(Modifier.PUBLIC)
                                                      .returns(TypeName.VOID)
                                                      .addParameter(componentNameParameter)
                                                      .addParameter(ibinderParameter)
                                                      .addStatement("$N = $T.fromBinder($N)", clientField, clientName, ibinderParameter)
                                                      .beginControlFlow("for ($T runnable : $N)", Runnable.class, waitTaskField)
                                                      .addStatement("runnable.run()")
                                                      .endControlFlow()
                                                      .addStatement("$N.clear()", waitTaskField)
                                                      .build();
        MethodSpec onServiceDisconnectedMethod = MethodSpec.methodBuilder("onServiceDisconnected")
                                                         .addAnnotation(Override.class)
                                                         .addModifiers(Modifier.PUBLIC)
                                                         .returns(TypeName.VOID)
                                                         .addParameter(componentNameParameter)
                                                         .addStatement("$N = null", clientField)
                                                         .addStatement("__startBindService()")
                                                         .build();
        TypeSpec serviceConnectionAnonymousInnerType = TypeSpec.anonymousClassBuilder("")
                                                               .superclass(serviceConnectionType)
                                                               .addMethod(onServiceConnectedMethod)
                                                               .addMethod(onServiceDisconnectedMethod)
                                                               .build();
        FieldSpec serviceConnectionField = FieldSpec.builder(serviceConnectionType, "mServiceConnection", Modifier.PROTECTED)
                                                   .initializer("$L", serviceConnectionAnonymousInnerType)
                                                   .build();
        //构造方法
        MethodSpec constructor = MethodSpec.constructorBuilder()
                                         .addModifiers(Modifier.PRIVATE)
                                         .build();
        //__init方法
        MethodSpec initMethod = MethodSpec.methodBuilder("__init")
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(TypeNameHelper.typeNameOfContext(), "context")
                                        .addParameter(TypeNameHelper.typeNameOfComponentName(), "serviceComponentName")
                                        .addStatement("$N = context.getApplicationContext()", contextFiled)
                                        .addStatement("$N = serviceComponentName", componentNameField)
                                        .addStatement("$N = new $T<>()", waitTaskField, ArrayList.class)
                                        .build();
        //__destroy方法
        MethodSpec destroyMethod = MethodSpec.methodBuilder("__destroy")
                                           .addModifiers(Modifier.PUBLIC)
                                           .addStatement("$N.unbindService($N)", contextFiled, serviceConnectionField)
                                           .addStatement("$N = null", contextFiled)
                                           .addStatement("$N = null", clientField)
                                           .addStatement("$N.clear()", waitTaskField)
                                           .addStatement("$N = null", waitTaskField)
                                           .build();
        //__startBindService方法
        MethodSpec startBindServiceMethod = MethodSpec.methodBuilder("__startBindService")
                                                    .addModifiers(Modifier.PUBLIC)
                                                    .addStatement("$1T intent = new $1T()", ClassName.bestGuess("android.content.Intent"))
                                                    .addStatement("intent.setComponent($N)", componentNameField)
                                                    .addStatement("$N.bindService(intent, $N, $T.BIND_AUTO_CREATE)", contextFiled, serviceConnectionField, TypeNameHelper.typeNameOfContext())
                                                    .build();
        //__isServiceBind方法
        MethodSpec isServiceBindMethod = MethodSpec.methodBuilder("__isServiceBind")
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .returns(TypeName.BOOLEAN)
                                                 .addStatement("return $N != null", clientField)
                                                 .build();
        //开始生成Helper类
        TypeSpec.Builder helperTypeBuilder = TypeSpec.classBuilder(helperClassName)
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .addMethod(constructor)
                                                 .addField(instanceFiled)
                                                 .addField(contextFiled)
                                                 .addField(componentNameField)
                                                 .addField(waitTaskField)
                                                 .addField(clientField)
                                                 .addField(serviceConnectionField)
                                                 .addMethod(initMethod)
                                                 .addMethod(destroyMethod)
                                                 .addMethod(startBindServiceMethod)
                                                 .addMethod(isServiceBindMethod);
        //开始生成Helper类中的各个IPC通信方法
        for (ExecutableElement methodElement : binderInterfaceMethodElements)
        {
            //生成IPC同步通信方法
            helperTypeBuilder.addMethod(generateSyncInterfaceMethod(methodElement, clientField.name,
                    startBindServiceMethod.name));
            //生成IPC异步通信方法
            helperTypeBuilder.addMethod(generateAsyncInterfaceMethod(methodElement, clientField.name,
                    startBindServiceMethod.name, waitTaskField.name));
        }
        return helperTypeBuilder.build();
    }

    public static String getHelperFullName(TypeElement typeInterface)
    {
        return typeInterface.getSimpleName().toString() + "Helper";
    }

    private static MethodSpec generateSyncInterfaceMethod(ExecutableElement methodElement, String clientName, String startBindServiceName)
    {
        String methodName = methodElement.getSimpleName().toString();
        MethodSpec.Builder interfaceMethodBuilder = MethodSpec.methodBuilder(methodName)
                                                            .addModifiers(Modifier.PUBLIC)
                                                            .returns(TypeName.get(methodElement.getReturnType()))
                                                            .addException(TypeNameHelper.typeNameOfRemoteException());
        for (VariableElement parameterElement : methodElement.getParameters())
        {
            interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()), parameterElement.getSimpleName().toString());
        }
        interfaceMethodBuilder.beginControlFlow("if ($L == null)", clientName)
                .addStatement("$L()", startBindServiceName)
                .addStatement("throw new $T(\"Remote NOT ready!!!\")", TypeNameHelper.typeNameOfRemoteException())
                .nextControlFlow("else");

        String parametersString = ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters());
        if (methodElement.getReturnType().getKind() == TypeKind.VOID)
        {
            interfaceMethodBuilder.addStatement("$L.$N($L)", clientName, methodElement.getSimpleName(), parametersString);
        }
        else
        {
            interfaceMethodBuilder.addStatement("return $L.$N($L)", clientName, methodElement.getSimpleName(), parametersString);
        }
        interfaceMethodBuilder.endControlFlow();
        return interfaceMethodBuilder.build();
    }

    private static MethodSpec generateAsyncInterfaceMethod(ExecutableElement methodElement, String clientName, String startBindServiceName, String waitTasksName)
    {
        String methodName = methodElement.getSimpleName().toString() + "Async";
        MethodSpec.Builder interfaceMethodBuilder = MethodSpec.methodBuilder(methodName)
                                                            .addModifiers(Modifier.PUBLIC)
                                                            .returns(TypeName.VOID);
        for (VariableElement parameterElement : methodElement.getParameters())
        {
            interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()), parameterElement.getSimpleName().toString(), Modifier.FINAL);
        }
        String callbackName = "callback";
        switch (methodElement.getReturnType().getKind())
        {
            case VOID:
            {
                interfaceMethodBuilder.addParameter(VoidCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case BOOLEAN:
            {
                interfaceMethodBuilder.addParameter(BooleanCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case CHAR:
            {
                interfaceMethodBuilder.addParameter(CharCallBack.class, callbackName, Modifier.FINAL);
                break;
            }
            case BYTE:
            {
                interfaceMethodBuilder.addParameter(ByteCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case SHORT:
            {
                interfaceMethodBuilder.addParameter(ShortCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case FLOAT:
            {
                interfaceMethodBuilder.addParameter(FloatCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case INT:
            {
                interfaceMethodBuilder.addParameter(IntCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case LONG:
            {
                interfaceMethodBuilder.addParameter(LongCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            case DOUBLE:
            {
                interfaceMethodBuilder.addParameter(DoubleCallback.class, callbackName, Modifier.FINAL);
                break;
            }
            default:
            {
                interfaceMethodBuilder.addParameter(ParameterizedTypeName.get(ClassName.get(ResultCallBack.class),
                        TypeName.get(methodElement.getReturnType())), callbackName, Modifier.FINAL);
                break;
            }
        }
        MethodSpec.Builder runMethodBuilder = MethodSpec.methodBuilder("run")
                                                      .addModifiers(Modifier.PUBLIC)
                                                      .returns(TypeName.VOID)
                                                      .addAnnotation(Override.class);
        if (methodElement.getReturnType().getKind() == TypeKind.VOID)
        {
            runMethodBuilder.beginControlFlow("try")
                    .addStatement("$L.$N($L)", clientName, methodElement.getSimpleName(), ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters()))
                    .endControlFlow()
                    .beginControlFlow("catch (Exception ex)")
                    .beginControlFlow("if($L != null)", callbackName)
                    .addStatement("$L.onError(ex)", callbackName)
                    .endControlFlow()
                    .addStatement("return")
                    .endControlFlow()
                    .beginControlFlow("if($L != null)", callbackName)
                    .addStatement("$L.onSuccess()", callbackName)
                    .endControlFlow();
        }
        else
        {
            runMethodBuilder.addStatement("$T result", methodElement.getReturnType())
                    .beginControlFlow("try")
                    .addStatement("result = $L.$N($L)", clientName, methodElement.getSimpleName(), ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters()))
                    .endControlFlow()
                    .beginControlFlow("catch (Exception ex)")
                    .beginControlFlow("if($L != null)", callbackName)
                    .addStatement("$L.onError(ex)", callbackName)
                    .endControlFlow()
                    .addStatement("return")
                    .endControlFlow()
                    .beginControlFlow("if($L != null)", callbackName)
                    .addStatement("$L.onSuccess(result)", callbackName)
                    .endControlFlow();
        }
        TypeSpec runnableInnerType = TypeSpec.anonymousClassBuilder("")
                                             .addSuperinterface(Runnable.class)
                                             .addMethod(runMethodBuilder.build())
                                             .build();
        interfaceMethodBuilder.addStatement("Runnable runnable = $L", runnableInnerType)
                .beginControlFlow("if ($L == null)", clientName)
                .addStatement("$L.add(runnable)", waitTasksName)
                .addStatement("$L()", startBindServiceName)
                .nextControlFlow("else")
                .addStatement("runnable.run()")
                .endControlFlow();
        return interfaceMethodBuilder.build();
    }
}
