package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BroadcastInterface;

import java.io.IOException;
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
 * Created by zmy on 2018/5/10.
 */

public class BroadcastInterfaceProcessor extends AbstractProcessor
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
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BroadcastInterface.class))
        {
            BroadcastInterface broadcastInterface = elementClass.getAnnotation(BroadcastInterface.class);
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
                TypeSpec typeSpecHelper = generateHelper(typeElement, methodElements, broadcastInterface.key());
                JavaFile javaFileHelper = JavaFile.builder(mElements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecHelper).build();
                try
                {
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
        types.add(BroadcastInterface.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    private TypeSpec generateHelper(TypeElement typeElement, List<ExecutableElement> methodElements, String broadcastKey)
    {
        String generatedClassName = typeElement.getSimpleName().toString() + "Helper";
        TypeName helperType = ClassName.bestGuess(generatedClassName);
        FieldSpec fieldSpecContext = FieldSpec.builder(TypeNameHelper.typeNameOfContext(), "mAppContext", Modifier.PRIVATE).build();
        //单例
        FieldSpec instanceFiled = FieldSpec.builder(helperType, "instance", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                          .initializer("new $T()", helperType)
                                          .build();

        FieldSpec contextFiled = FieldSpec.builder(TypeNameHelper.typeNameOfContext(), "mAppContext", Modifier.PROTECTED).build();
        FieldSpec broadcastKeyField = FieldSpec.builder(String.class, "mBroadcastKey", Modifier.PRIVATE)
                                                  .initializer("$S", broadcastKey)
                                                  .build();
        //constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
                                         .addModifiers(Modifier.PRIVATE)
                                         .build();
        //init方法
        MethodSpec initMethod = MethodSpec.methodBuilder("__init")
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(TypeNameHelper.typeNameOfContext(), "context")
                                        .addStatement("$N = context.getApplicationContext()", contextFiled)
                                        .build();
        TypeSpec.Builder helperBuilder = TypeSpec.classBuilder(generatedClassName)
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .addMethod(constructor)
                                                 .addField(instanceFiled)
                                                 .addField(contextFiled)
                                                 .addField(broadcastKeyField)
                                                 .addMethod(initMethod);
        for (ExecutableElement methodElement : methodElements)
        {
            MethodSpec interfaceMethod = MethodSpec.methodBuilder(methodElement.getSimpleName().toString())
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .returns(TypeName.VOID)
                                                 .addStatement("$1T intent = new $1T($2S)", TypeNameHelper.typeNameOfIntent(), INTENT_ACTION)
                                                 .addStatement("intent.putExtra($S, $N)", "broadcastKey", broadcastKeyField)
                                                 .addStatement("intent.putExtra($S, $S)", "methodName", methodElement.getSimpleName().toString())
                                                 .addStatement("$N.sendBroadcast(intent)", contextFiled)
                                                 .build();
            helperBuilder.addMethod(interfaceMethod);
        }
        return helperBuilder.build();
    }
}
