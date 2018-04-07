package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BinderInterfaceImpl;

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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by zmy on 2018/4/7.
 */

public class BinderInterfaceImplProcessor extends AbstractProcessor
{
    public static final String TAG = "BinderInterfaceImplProcessor";

    private Filer mFiler;
    private Elements mElements;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BinderInterfaceImpl.class))
        {
            String elementClassString = elementClass.toString();
            log("Start processing: " + elementClassString);
            if (elementClass.getKind() == ElementKind.CLASS)
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
                TypeSpec typeSpecImpl = generateCode(typeElement, methodElements);
                JavaFile javaFile = JavaFile.builder(mElements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecImpl).build();
                try
                {
                    javaFile.writeTo(mFiler);
                    //                    javaFile.writeTo(new File("d:\\binder"));
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                mMessager.printMessage(Diagnostic.Kind.WARNING, elementClass.toString() + " is not a class, ignored!");
            }
        }
        return true;
    }

    private TypeSpec generateCode(TypeElement typeElement, List<ExecutableElement> methodElements)
    {
        String generatedClassName = typeElement.getSimpleName().toString() + "ServerImpl";
        mMessager.printMessage(Diagnostic.Kind.NOTE, "generate class: " + generatedClassName);

        FieldSpec fieldSpecInterfaceImpl = FieldSpec.builder(TypeName.get(typeElement.asType()), "mInterfaceImpl", Modifier.PRIVATE).build();
        ParameterSpec parameterSpecInterfaceImpl = ParameterSpec.builder(TypeName.get(typeElement.asType()), "interfaceImpl").build();

        MethodSpec methodSpecConstructor = MethodSpec.constructorBuilder()
                                                   .addModifiers(Modifier.PRIVATE)
                                                   .addParameter(parameterSpecInterfaceImpl)
                                                   .addStatement("$N = $N", fieldSpecInterfaceImpl, parameterSpecInterfaceImpl)
                                                   .build();


        ParameterSpec onTransactMethodCodeParameter = ParameterSpec.builder(int.class, "code").build();
        ParameterSpec onTransactMethodDataParameter = ParameterSpec.builder(TypeNameHelper.typeNameOfParcel(), "data").build();
        ParameterSpec onTransactMethodReplyParameter = ParameterSpec.builder(TypeNameHelper.typeNameOfParcel(), "reply").build();
        MethodSpec.Builder onTransactMethodBuilder = MethodSpec.methodBuilder("onTransact")
                                                             .addModifiers(Modifier.PUBLIC)
                                                             .returns(boolean.class)
                                                             .addAnnotation(Override.class)
                                                             .addException(TypeNameHelper.typeNameOfRemoteException())
                                                             .addParameter(onTransactMethodCodeParameter)
                                                             .addParameter(onTransactMethodDataParameter)
                                                             .addParameter(onTransactMethodReplyParameter)
                                                             .addParameter(ParameterSpec.builder(int.class, "flags").build())
                .beginControlFlow("switch($N)", onTransactMethodCodeParameter)
                .endControlFlow();

        for (int i = 0; i < methodElements.size(); i++)
        {
//            ExecutableElement methodElement = methodElements.get(i);
//            String methodName = methodElement.getSimpleName().toString();
//            FieldSpec fieldSpecMethodId = FieldSpec.builder(TypeName.INT, "TRANSACTION_" + methodName, Modifier.PRIVATE, Modifier.STATIC)
//                                                  .initializer("$T.FIRST_CALL_TRANSACTION + $L", TypeNameHelper.typeNameOfIBinder(), i + 1)
//                                                  .build();
//            typeImplBuilder.addField(fieldSpecMethodId);
//            MethodSpec.Builder interfaceMethodBuilder = MethodSpec.methodBuilder(methodName)
//                                                                .addModifiers(Modifier.PUBLIC)
//                                                                .addAnnotation(Override.class)
//                                                                .returns(TypeName.get(methodElement.getReturnType()))
//                                                                .addException(TypeNameHelper.typeNameOfRemoteException());
//
//            for (VariableElement parameterElement : methodElement.getParameters())
//            {
//                interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()), parameterElement.getSimpleName().toString());
//            }
//            interfaceMethodBuilder.addStatement("$1T data = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
//            interfaceMethodBuilder.addStatement("$1T reply = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
//            interfaceMethodBuilder.beginControlFlow("try");
//            for (VariableElement parameterElement : methodElement.getParameters())
//            {
//                mMessager.printMessage(Diagnostic.Kind.NOTE, "parameter type:" + parameterElement.asType().getKind());
//                switch (parameterElement.asType().getKind())
//                {
//                    case INT:
//                    {
//                        interfaceMethodBuilder.addStatement("data.writeInt($L)", parameterElement.getSimpleName().toString());
//                        break;
//                    }
//                    case BYTE:
//                    {
//                        interfaceMethodBuilder.addStatement("data.writeByte($L)", parameterElement.getSimpleName().toString());
//                        break;
//                    }
//                    case LONG:
//                    {
//                        interfaceMethodBuilder.addStatement("data.writeLong($L)", parameterElement.getSimpleName().toString());
//                        break;
//                    }
//                    case FLOAT:
//                    {
//                        interfaceMethodBuilder.addStatement("data.writeFloat($L)", parameterElement.getSimpleName().toString());
//                        break;
//                    }
//                }
//            }
//            interfaceMethodBuilder.addStatement("$N.transact($N, data, reply, 0)", fieldSpecRemote, fieldSpecMethodId);
//            interfaceMethodBuilder.addStatement("reply.readException()");
//            switch (methodElement.getReturnType().getKind())
//            {
//                case INT:
//                {
//                    interfaceMethodBuilder.addStatement("return reply.readInt()");
//                    break;
//                }
//                case BYTE:
//                {
//                    interfaceMethodBuilder.addStatement("return reply.readByte()");
//                    break;
//                }
//                case LONG:
//                {
//                    interfaceMethodBuilder.addStatement("return reply.readLong()");
//                    break;
//                }
//                case FLOAT:
//                {
//                    interfaceMethodBuilder.addStatement("return reply.readFloat()");
//                    break;
//                }
//            }
//            interfaceMethodBuilder.endControlFlow();
//            interfaceMethodBuilder.beginControlFlow("finally");
//            interfaceMethodBuilder.addStatement("data.recycle()");
//            interfaceMethodBuilder.addStatement("reply.recycle()");
//            interfaceMethodBuilder.endControlFlow();
//            typeImplBuilder.addMethod(interfaceMethodBuilder.build());
        }

        TypeSpec.Builder typeImplBuilder = TypeSpec.classBuilder(generatedClassName)
                                                   .addModifiers(Modifier.PUBLIC)
                                                   .superclass(ClassName.get("android.os", "Binder"))
                                                   .addField(fieldSpecInterfaceImpl)
                                                   .addMethod(methodSpecConstructor);
        return typeImplBuilder.build();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>(1);
        types.add(BinderInterfaceImpl.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    private void log(String log)
    {
        mMessager.printMessage(Diagnostic.Kind.NOTE, TAG + ": " + log);
    }
}
