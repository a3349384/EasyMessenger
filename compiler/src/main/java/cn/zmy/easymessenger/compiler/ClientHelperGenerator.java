package cn.zmy.easymessenger.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import cn.zmy.easymessenger.BooleanCallback;
import cn.zmy.easymessenger.ByteCallback;
import cn.zmy.easymessenger.CharCallBack;
import cn.zmy.easymessenger.DoubleCallback;
import cn.zmy.easymessenger.FloatCallback;
import cn.zmy.easymessenger.IntCallback;
import cn.zmy.easymessenger.LongCallback;
import cn.zmy.easymessenger.ResultCallBack;
import cn.zmy.easymessenger.ShortCallback;
import cn.zmy.easymessenger.VoidCallback;

/**
 * Created by zmy on 2019/2/19.
 * 生成IPC通信Helper类
 */

public class ClientHelperGenerator
{
    private static final String sClientName = "mClient";
    private static final String sStartBindServiceName = "__startBindService";
    private static final String sWaitTasksName = "mWaitTasks";
    private static final String sGetClientWithBinderName = "getClientWithBinder";

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
        interfaceMethodBuilder.beginControlFlow("if ($L == null)", sClientName)
                .addStatement("$L()", sStartBindServiceName)
                .addStatement("throw new $T(\"Remote NOT ready!!!\")", TypeNameHelper.typeNameOfRemoteException())
                .nextControlFlow("else");

        String parametersString = ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters());
        if (methodElement.getReturnType().getKind() == TypeKind.VOID)
        {
            interfaceMethodBuilder.addStatement("$L.$N($L)", sClientName, methodElement.getSimpleName(), parametersString);
        }
        else
        {
            interfaceMethodBuilder.addStatement("return $L.$N($L)", sClientName, methodElement.getSimpleName(), parametersString);
        }
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
                    .addStatement("$L.$N($L)", sClientName, methodElement.getSimpleName(), ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters()))
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
                    .addStatement("result = $L.$N($L)", sClientName, methodElement.getSimpleName(), ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters()))
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
                .beginControlFlow("if ($L == null)", sClientName)
                .addStatement("$L.add(runnable)", sWaitTasksName)
                .addStatement("$L()", sStartBindServiceName)
                .nextControlFlow("else")
                .addStatement("runnable.run()")
                .endControlFlow();
        return interfaceMethodBuilder.build();
    }
}
