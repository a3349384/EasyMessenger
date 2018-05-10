package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BinderInterface;
import com.tech618.easymessenger.BooleanCallback;
import com.tech618.easymessenger.ByteCallback;
import com.tech618.easymessenger.FloatCallback;
import com.tech618.easymessenger.IntCallback;
import com.tech618.easymessenger.LongCallback;
import com.tech618.easymessenger.ResultCallBack;
import com.tech618.easymessenger.VoidCallback;

import java.io.IOException;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by zmy on 2018/4/5.
 */

public class BinderInterfaceProcessor extends AbstractProcessor
{
    private Filer mFiler;
    private Elements mElements;
    private Types mTypes;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BinderInterface.class))
        {
            String elementClassString = elementClass.toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "Start processing: " + elementClassString);
            if (elementClass.getKind() == ElementKind.INTERFACE)
            {
                //it's type is class, so it must be a TypeElement
                TypeElement typeElement = (TypeElement) elementClass;
                List<ExecutableElement> methodElements = new ArrayList<>();
                for (Element elementMember : typeElement.getEnclosedElements())
                {
                    if (elementMember.getKind() == ElementKind.METHOD)
                    {
                        mMessager.printMessage(Diagnostic.Kind.NOTE, "Start processing: " + elementClassString + "#" + elementMember.toString());
                        //it's type is method, so it must be a ExecutableElement
                        methodElements.add((ExecutableElement) elementMember);
                    }
                }
                TypeSpec typeSpecClient = generateClient(typeElement, methodElements);
                JavaFile javaFileClient = JavaFile.builder(mElements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecClient).build();
                TypeSpec typeSpecHelper = generateHelper(typeElement, methodElements);
                JavaFile javaFileHelper = JavaFile.builder(mElements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecHelper).build();
                try
                {
                    javaFileClient.writeTo(mFiler);
                    javaFileHelper.writeTo(mFiler);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                mMessager.printMessage(Diagnostic.Kind.WARNING, elementClass.toString() + " is not a interface, ignored!");
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>(1);
        types.add(BinderInterface.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    private TypeSpec generateClient(TypeElement typeElement, List<ExecutableElement> methodElements)
    {
        String generatedClassName = getClientFullName(typeElement);
        FieldSpec fieldSpecRemote = FieldSpec.builder(TypeNameHelper.typeNameOfIBinder(), "mRemote", Modifier.PRIVATE).build();
        ParameterSpec parameterSpecRemote = ParameterSpec.builder(TypeNameHelper.typeNameOfIBinder(), "remote").build();

        MethodSpec methodSpecConstructor = MethodSpec.constructorBuilder()
                                                   .addModifiers(Modifier.PRIVATE)
                                                   .addParameter(parameterSpecRemote)
                                                   .addStatement("$N = $N", fieldSpecRemote, parameterSpecRemote)
                                                   .build();

        ParameterSpec parameterSpecBinder = ParameterSpec.builder(TypeNameHelper.typeNameOfIBinder(), "binder").build();
        MethodSpec methodAsInterface = MethodSpec.methodBuilder("fromBinder")
                                               .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                               .addParameter(parameterSpecBinder)
                                               .returns(ClassName.bestGuess(generatedClassName))
                                               .addStatement("return new $L($N)", generatedClassName, parameterSpecBinder)
                                               .build();

        TypeSpec.Builder typeImplBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(fieldSpecRemote)
                .addMethod(methodSpecConstructor)
                .addMethod(methodAsInterface);
        for (int i = 0; i < methodElements.size(); i++)
        {
            ExecutableElement methodElement = methodElements.get(i);
            String methodName = methodElement.getSimpleName().toString();
            FieldSpec fieldSpecMethodId = FieldSpec.builder(TypeName.INT, "TRANSACTION_" + methodName, Modifier.PRIVATE, Modifier.STATIC)
                                                  .initializer("$T.FIRST_CALL_TRANSACTION + $L", TypeNameHelper.typeNameOfIBinder(), i + 1)
                                                  .build();
            typeImplBuilder.addField(fieldSpecMethodId);
            MethodSpec.Builder interfaceMethodBuilder = MethodSpec.methodBuilder(methodName)
                                                                .addModifiers(Modifier.PUBLIC)
                                                                .returns(TypeName.get(methodElement.getReturnType()))
                                                                .addException(ClassName.get("android.os", "RemoteException"));

            for (VariableElement parameterElement : methodElement.getParameters())
            {
                interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()), parameterElement.getSimpleName().toString());
            }
            interfaceMethodBuilder.addStatement("$1T data = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
            interfaceMethodBuilder.addStatement("$1T reply = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
            interfaceMethodBuilder.beginControlFlow("try");
            boolean isNullFlagDefined = false;
            for (VariableElement parameterElement : methodElement.getParameters())
            {
                mMessager.printMessage(Diagnostic.Kind.NOTE, "parameter type:" + parameterElement.asType().toString());
                if (ClassHelper.isThirdPartyClass(parameterElement.asType().toString()))
                {
                    //the parameter should be a thirdparty class, we regard it as a parceable
                    if (!isNullFlagDefined)
                    {
                        interfaceMethodBuilder.addStatement("int isNullFlag = $N == null ? 0 : 1", parameterElement.getSimpleName());
                        isNullFlagDefined = true;
                    }
                    else
                    {
                        interfaceMethodBuilder.addStatement("isNullFlag = $N == null ? 0 : 1", parameterElement.getSimpleName());
                    }
                    interfaceMethodBuilder.addStatement("data.writeInt(isNullFlag)");
                    interfaceMethodBuilder.beginControlFlow("if (isNullFlag > 0)");
                    interfaceMethodBuilder.addStatement("$N.writeToParcel(data, 0)", parameterElement.getSimpleName());
                    interfaceMethodBuilder.endControlFlow();
                }
                else
                {
                    if (parameterElement.asType().getKind() == TypeKind.BOOLEAN)
                    {
                        interfaceMethodBuilder.addStatement("data.writeInt($N ? 1 : 0)", parameterElement.getSimpleName());
                    }
                    else
                    {
                        interfaceMethodBuilder.addStatement("data.$L($N)", TypeMirrorHelper.getParcelWriteString(parameterElement.asType()),
                                parameterElement.getSimpleName());
                    }
                }
            }
            interfaceMethodBuilder.addStatement("$N.transact($N, data, reply, 0)", fieldSpecRemote, fieldSpecMethodId);
            interfaceMethodBuilder.addStatement("reply.readException()");
            if (methodElement.getReturnType().getKind() != TypeKind.VOID)
            {
                String returnTypeClassName = methodElement.getReturnType().toString();
                if (ClassHelper.isThirdPartyClass(returnTypeClassName))
                {
                    interfaceMethodBuilder.addStatement("int isResultNullFlag = reply.readInt()");
                    interfaceMethodBuilder.beginControlFlow("if (isResultNullFlag > 0)");
                    interfaceMethodBuilder.addStatement("return $T.CREATOR.createFromParcel(reply)", methodElement.getReturnType());
                    interfaceMethodBuilder.nextControlFlow("else");
                    interfaceMethodBuilder.addStatement("return null");
                    interfaceMethodBuilder.endControlFlow();
                }
                else if (ClassHelper.isList(returnTypeClassName))
                {
                    interfaceMethodBuilder.addStatement("$T result = new $T()", List.class, ArrayList.class);
                    interfaceMethodBuilder.addStatement("reply.readList(result, getClass().getClassLoader())");
                    interfaceMethodBuilder.addStatement("return result");
                }
                else if (methodElement.getReturnType().getKind() == TypeKind.BOOLEAN)
                {
                    interfaceMethodBuilder.addStatement("return reply.readInt() > 0 ? true : false", TypeMirrorHelper.getParcelReadString(methodElement.getReturnType()));
                }
                else
                {
                    interfaceMethodBuilder.addStatement("return reply.$L()", TypeMirrorHelper.getParcelReadString(methodElement.getReturnType()));
                }
            }
            interfaceMethodBuilder.endControlFlow();
            interfaceMethodBuilder.beginControlFlow("finally");
            interfaceMethodBuilder.addStatement("data.recycle()");
            interfaceMethodBuilder.addStatement("reply.recycle()");
            interfaceMethodBuilder.endControlFlow();
            typeImplBuilder.addMethod(interfaceMethodBuilder.build());
        }

        return typeImplBuilder.build();
    }

    private TypeSpec generateHelper(TypeElement typeElement, List<ExecutableElement> methodElements)
    {
        String generatedClassName = getHelperFullName(typeElement);
        TypeName helperType = ClassName.bestGuess(generatedClassName);
        TypeName clientType = ClassName.bestGuess(getClientFullName(typeElement));

        //单例
        FieldSpec instanceFiled = FieldSpec.builder(helperType, "instance", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()", helperType)
                .build();

        FieldSpec contextFiled = FieldSpec.builder(TypeNameHelper.typeNameOfContext(), "mAppContext", Modifier.PROTECTED).build();
        FieldSpec componentNameField = FieldSpec.builder(TypeNameHelper.typeNameOfComponentName(), "mServiceComponentName", Modifier.PROTECTED).build();
        FieldSpec waitTaskField = FieldSpec.builder(ParameterizedTypeName.get(List.class, Runnable.class), "mWaitTasks", Modifier.PROTECTED).build();
        FieldSpec clientField = FieldSpec.builder(clientType, "mClient", Modifier.PROTECTED).build();

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
                                                      .addStatement("$N = $T.fromBinder($N)", clientField, clientType, ibinderParameter)
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

        //constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        //init方法
        MethodSpec initMethod = MethodSpec.methodBuilder("__init")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNameHelper.typeNameOfContext(), "context")
                .addParameter(TypeNameHelper.typeNameOfComponentName(), "serviceComponentName")
                .addStatement("$N = context.getApplicationContext()", contextFiled)
                .addStatement("$N = serviceComponentName", componentNameField)
                .addStatement("$N = new $T<>()", waitTaskField, ArrayList.class)
                .build();

        //destroy方法
        MethodSpec destroyMethod = MethodSpec.methodBuilder("__destroy")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.unbindService($N)", contextFiled, serviceConnectionField)
                .addStatement("$N = null", contextFiled)
                .addStatement("$N = null", clientField)
                .addStatement("$N.clear()", waitTaskField)
                .addStatement("$N = null", waitTaskField)
                .build();

        //startBindService方法
        MethodSpec startBindServiceMethod = MethodSpec.methodBuilder("__startBindService")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$1T intent = new $1T()", ClassName.bestGuess("android.content.Intent"))
                .addStatement("intent.setComponent($N)", componentNameField)
                .addStatement("$N.bindService(intent, $N, $T.BIND_AUTO_CREATE)", contextFiled, serviceConnectionField, TypeNameHelper.typeNameOfContext())
                .build();

        MethodSpec isServiceBindMethod = MethodSpec.methodBuilder("__isServiceBind")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("return $N != null", clientField)
                .build();

        TypeSpec.Builder clientBuilder = TypeSpec.classBuilder(generatedClassName)
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

        for (ExecutableElement methodElement : methodElements)
        {
            clientBuilder.addMethod(generateSyncInterfaceMethod(methodElement, clientField.name, startBindServiceMethod.name));
            clientBuilder.addMethod(generateAsyncInterfaceMethod(methodElement, clientField.name, startBindServiceMethod.name, waitTaskField.name));
        }
        return clientBuilder.build();
    }

    private String getClientFullName(TypeElement typeInterface)
    {
        return typeInterface.getSimpleName().toString() + "Client";
    }

    private String getHelperFullName(TypeElement typeInterface)
    {
        return typeInterface.getSimpleName().toString() + "Helper";
    }

    private MethodSpec generateSyncInterfaceMethod(ExecutableElement methodElement, String clientName, String startBindServiceName)
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

    private MethodSpec generateAsyncInterfaceMethod(ExecutableElement methodElement, String clientName, String startBindServiceName, String waitTasksName)
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
            case BYTE:
            {
                interfaceMethodBuilder.addParameter(ByteCallback.class, callbackName, Modifier.FINAL);
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
            default:
            {
                interfaceMethodBuilder.addParameter(ParameterizedTypeName.get(ClassName.get(ResultCallBack.class), TypeName.get(methodElement.getReturnType())), callbackName, Modifier.FINAL);
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
