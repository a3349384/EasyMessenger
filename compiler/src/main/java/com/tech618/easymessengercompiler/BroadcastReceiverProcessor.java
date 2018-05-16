package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BrodcastReceiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by zmy on 2018/5/10.
 */

public class BroadcastReceiverProcessor extends AbstractProcessor
{
    private Filer mFiler;
    private Elements mElements;
    private Types mTypes;
    private Messager mMessager;

    public String INTENT_ACTION = "com.tech618.ipc.broadcast.action";

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
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BrodcastReceiver.class);
        if (elements.size() == 0)
        {
            return true;
        }
        Map<String, Element> map = new HashMap<>();
        String packageName = "";
        for (Element element : elements)
        {
            packageName = mElements.getPackageOf(element).getQualifiedName().toString();
            BrodcastReceiver annotation = element.getAnnotation(BrodcastReceiver.class);
            map.put(annotation.key(), element);
        }

        TypeSpec typeSpecHelper = generateHelper(map);
        JavaFile javaFileHelper = JavaFile.builder(packageName, typeSpecHelper).build();
        try
        {
            javaFileHelper.writeTo(mFiler);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>(1);
        types.add(BrodcastReceiver.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    private TypeSpec generateHelper(Map<String, Element> map)
    {
        TypeSpec.Builder broadcastReceiverInnerBuilder = TypeSpec.classBuilder("TheBroadcastReceiver")
                                                                 .superclass(TypeNameHelper.typeNameOfBroadcastReceiver());
        for (Map.Entry<String, Element> entry : map.entrySet())
        {
            TypeName fieldType = ClassName.get((TypeElement) entry.getValue());
            FieldSpec fieldSpec = FieldSpec.builder(fieldType, entry.getKey(), Modifier.PRIVATE)
                                          .initializer("new $T()", fieldType)
                                          .build();
            broadcastReceiverInnerBuilder.addField(fieldSpec);
        }
        MethodSpec.Builder onReceiveMethodBuilder = MethodSpec.methodBuilder("onReceive")
                                                            .addModifiers(Modifier.PUBLIC)
                                                            .returns(TypeName.VOID)
                                                            .addAnnotation(Override.class)
                                                            .addParameter(TypeNameHelper.typeNameOfContext(), "context")
                                                            .addParameter(TypeNameHelper.typeNameOfIntent(), "intent");
        onReceiveMethodBuilder.addStatement("String key = intent.getStringExtra($S)", "broadcastKey")
                .addStatement("String methodName = intent.getStringExtra($S)", "methodName")
                .beginControlFlow("switch(key)");
        for (Map.Entry<String, Element> entry : map.entrySet())
        {
            TypeElement typeElement = (TypeElement) entry.getValue();
            onReceiveMethodBuilder.beginControlFlow("case $S:", entry.getKey());
            onReceiveMethodBuilder.beginControlFlow("switch(methodName)");
            for (Element elementMember : typeElement.getEnclosedElements())
            {
                if (elementMember.getKind() == ElementKind.METHOD && elementMember.getModifiers().size() == 1
                        && elementMember.getModifiers().contains(Modifier.PUBLIC))
                {
                    ExecutableElement methodElement = (ExecutableElement) elementMember;
                    onReceiveMethodBuilder.beginControlFlow("case $S:", methodElement.getSimpleName());
                    for (VariableElement parameterElement : methodElement.getParameters())
                    {
                        String parameterClassName = parameterElement.asType().toString();
                        TypeMirror parameterType = parameterElement.asType();
                        if (parameterType instanceof ArrayType)
                        {
                            //array,example: int[],string[],paracelable[]
                            TypeMirror parameterComponentType = ((ArrayType) parameterType).getComponentType();
                            String parameterComponentClassName = parameterComponentType.toString();
                            if (TypeMirrorHelper.isInt(parameterComponentType))
                            {

                            }
                            else if (TypeMirrorHelper.isString(parameterComponentType))
                            {

                            }
                            else
                            {

                            }
                        }
                        else if (ClassHelper.isThirdPartyClass(parameterClassName))
                        {
                            //regard the parameter as a parceable type
                            //generate like: User user = intent.getParcelableExtra("")
                            onReceiveMethodBuilder.addStatement("$T $N = intent.getParcelableExtra($S)",
                                    TypeName.get(parameterElement.asType()),
                                    parameterElement.getSimpleName(),
                                    parameterElement.getSimpleName().toString());
                        }
                        else if (ClassHelper.isList(parameterClassName))
                        {
                            //list parameter type
                        }
                        else
                        {
                            //base type, int, bool, etc
                            //generate like: int num = intent.readIntExtra("num", 0)
                            onReceiveMethodBuilder.addStatement("$T $N = intent.$L",
                                    TypeName.get(parameterElement.asType()),
                                    parameterElement.getSimpleName(),
                                    TypeMirrorHelper.getIntentReadString(parameterElement.getSimpleName().toString(), parameterElement.asType()));
                        }
                    }
                    onReceiveMethodBuilder.addStatement("$L.$L($L)", entry.getKey(), methodElement.getSimpleName(), ParameterHelper.getMethodParameterStringByParameterElements(methodElement.getParameters()))
                            .addStatement("break")
                            .endControlFlow();
                }
            }
            onReceiveMethodBuilder.endControlFlow()
                    .addStatement("break")
                    .endControlFlow();
        }
        onReceiveMethodBuilder.endControlFlow();
        broadcastReceiverInnerBuilder.addMethod(onReceiveMethodBuilder.build());

        String generatedClassName = "BroadcastReceiverHelper";
        TypeName helperType = ClassName.bestGuess(generatedClassName);
        //单例
        FieldSpec instanceFiled = FieldSpec.builder(helperType, "instance", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                          .initializer("new $T()", helperType)
                                          .build();
        //constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
                                         .addModifiers(Modifier.PRIVATE)
                                         .build();
        //init方法
        TypeSpec broadcastReceiver = broadcastReceiverInnerBuilder.build();
        MethodSpec initMethod = MethodSpec.methodBuilder("__init")
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(TypeNameHelper.typeNameOfContext(), "appContext")
                                        .addStatement("appContext.registerReceiver(new $L(), new $T($S))", broadcastReceiver.name, TypeNameHelper.typeNameOfIntentFilter(), INTENT_ACTION)
                                        .build();
        TypeSpec.Builder helperBuilder = TypeSpec.classBuilder(generatedClassName)
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .addMethod(constructor)
                                                 .addField(instanceFiled)
                                                 .addMethod(initMethod)
                                                 .addType(broadcastReceiver);
        return helperBuilder.build();
    }
}
