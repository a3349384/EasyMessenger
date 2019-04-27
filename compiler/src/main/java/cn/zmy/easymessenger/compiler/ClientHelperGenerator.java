package cn.zmy.easymessenger.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.concurrent.Callable;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import cn.zmy.easymessenger.Constant;

/**
 * Created by zmy on 2019/2/19.
 * 生成IPC通信Helper类
 */

public class ClientHelperGenerator
{
    private static final String sClientName = "mClient";
    private static final String sStartBindServiceName = "__startBindService";
    private static final String sRunAfterConnectedName = "__runAfterConnected";
    private static final String sGetClientWithBinderName = "getClientWithBinder";
    private static final String sCheckClientAvailableName = "checkClientAvailable";

    public static TypeSpec generateHelper(TypeElement binderInterfaceTypeElement, List<ExecutableElement> binderInterfaceMethodElements)
    {
        String helperClassName = getHelperFullName(binderInterfaceTypeElement);
        TypeName helperName = ClassName.bestGuess(helperClassName);
        //单例
        FieldSpec instanceFiled = FieldSpec.builder(helperName, "instance", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                          .initializer("new $T()", helperName)
                                          .build();
        //构造方法
        MethodSpec constructor = MethodSpec.constructorBuilder()
                                         .addModifiers(Modifier.PRIVATE)
                                         .build();
        //实现getClientWithBinder
        String clientName = ClientClientGenerator.getClientFullName(binderInterfaceTypeElement);
        TypeName clientTypeName = ClassName.bestGuess(clientName);
        MethodSpec getClientWithBinderMethod = MethodSpec.methodBuilder(sGetClientWithBinderName)
                                                         .addAnnotation(Override.class)
                                                         .addModifiers(Modifier.PROTECTED)
                                                         .returns(clientTypeName)
                                                         .addParameter(TypeNameHelper.typeNameOfIBinder(), "binder")
                                                         .addStatement("return $T.fromBinder(binder)", clientTypeName)
                                                         .build();
        //开始生成Helper类
        TypeSpec.Builder helperTypeBuilder = TypeSpec.classBuilder(helperClassName)
                                                     .addModifiers(Modifier.PUBLIC)
                                                     .superclass(TypeNameHelper.typeNameOfBaseClientHelper(clientName))
                                                     .addMethod(constructor)
                                                     .addField(instanceFiled)
                                                     .addMethod(getClientWithBinderMethod);
        //开始生成Helper类中的各个IPC通信方法
        for (ExecutableElement methodElement : binderInterfaceMethodElements)
        {
            //生成IPC同步通信方法
            helperTypeBuilder.addMethod(generateSyncInterfaceMethod(methodElement));
            //生成IPC异步通信方法
            helperTypeBuilder.addMethod(generateAsyncInterfaceMethod(methodElement));
        }
        return helperTypeBuilder.build();
    }

    public static String getHelperFullName(TypeElement typeInterface)
    {
        return typeInterface.getSimpleName().toString() + "Helper";
    }

    private static MethodSpec generateSyncInterfaceMethod(ExecutableElement methodElement)
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
        interfaceMethodBuilder.beginControlFlow("if ($L())", sCheckClientAvailableName);
        String parametersString = ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters());
        if (methodElement.getReturnType().getKind() == TypeKind.VOID)
        {
            interfaceMethodBuilder.addStatement("$L.$N($L)", sClientName, methodElement.getSimpleName(), parametersString);
        }
        else
        {
            interfaceMethodBuilder.addStatement("return $L.$N($L)", sClientName, methodElement.getSimpleName(), parametersString);
        }
        interfaceMethodBuilder.nextControlFlow("else");
        interfaceMethodBuilder.addStatement("throw new RemoteException(\"Remote NOT ready!!!\")");
        interfaceMethodBuilder.endControlFlow();
        return interfaceMethodBuilder.build();
    }

    private static MethodSpec generateAsyncInterfaceMethod(ExecutableElement methodElement)
    {
        String methodName = methodElement.getSimpleName().toString() + "Async";
        MethodSpec.Builder interfaceMethodBuilder = MethodSpec.methodBuilder(methodName)
                                                            .addModifiers(Modifier.PUBLIC)
                                                            .returns(TypeName.VOID);
        for (VariableElement parameterElement : methodElement.getParameters())
        {
            interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()), parameterElement.getSimpleName().toString(), Modifier.FINAL);
        }
        String callbackParameterName = "callback";
        TypeKind returnKind = methodElement.getReturnType().getKind();
        TypeName callBackTypeName = ClassName.bestGuess(
                String.format("%s.%sCallback", Constant.PACKAGE_BASE, kindToPrefix(returnKind)));
        interfaceMethodBuilder.addParameter(callBackTypeName, callbackParameterName, Modifier.FINAL);
        String parameterString = ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters());
        MethodSpec.Builder callMethodBuilder = MethodSpec.methodBuilder("call")
                                                         .addAnnotation(Override.class)
                                                         .returns(Object.class)
                                                         .addModifiers(Modifier.PUBLIC)
                                                         .addException(Exception.class);
        if (returnKind == TypeKind.VOID)
        {
            callMethodBuilder.addStatement("$L.$N($L)", sClientName,
                    methodElement.getSimpleName(), parameterString);
            callMethodBuilder.addStatement("return null");
        }
        else
        {
            callMethodBuilder.addStatement("return $L.$N($L)", sClientName,
                    methodElement.getSimpleName(), parameterString);
        }
        TypeSpec callableInnerType = TypeSpec.anonymousClassBuilder("")
                                             .addSuperinterface(Callable.class)
                                             .addMethod(callMethodBuilder.build())
                                             .build();
        TypeName taskTypeName = ClassName.bestGuess(
                String.format("%s.task.%sTask", Constant.PACKAGE_BASE, kindToPrefix(returnKind)));
        interfaceMethodBuilder.addStatement("new $T($L, $L, this).run()", taskTypeName, callableInnerType, callbackParameterName);
        return interfaceMethodBuilder.build();
    }

    private static String kindToPrefix(TypeKind kind)
    {
        switch (kind)
        {
            case VOID:
            {
                return "Void";
            }
            case BOOLEAN:
            {
                return "Boolean";
            }
            case CHAR:
            {
                return "Char";
            }
            case BYTE:
            {
                return "Byte";
            }
            case SHORT:
            {
                return "Short";
            }
            case FLOAT:
            {
                return "Float";
            }
            case INT:
            {
                return "Int";
            }
            case LONG:
            {
                return "Long";
            }
            case DOUBLE:
            {
                return "Double";
            }
            default:
            {
                return "Result";
            }
        }
    }
}
