package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BinderInterface;

import java.io.IOException;
import java.lang.management.MemoryManagerMXBean;
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

    private TypeSpec generateCode(TypeElement typeElement, List<ExecutableElement> methodElements)
    {
        String generatedClassName = typeElement.getSimpleName().toString() + "ClientImpl";
        mMessager.printMessage(Diagnostic.Kind.NOTE, "generate class: " + generatedClassName);

        FieldSpec fieldSpecRemote = FieldSpec.builder(TypeNameHelper.typeNameOfIBinder(), "mRemote", Modifier.PRIVATE).build();
        ParameterSpec parameterSpecRemote = ParameterSpec.builder(TypeNameHelper.typeNameOfIBinder(), "remote").build();

        MethodSpec methodSpecConstructor = MethodSpec.constructorBuilder()
                                                   .addModifiers(Modifier.PRIVATE)
                                                   .addParameter(parameterSpecRemote)
                                                   .addStatement("$N = $N", fieldSpecRemote, parameterSpecRemote)
                                                   .build();

        ParameterSpec parameterSpecBinder = ParameterSpec.builder(TypeNameHelper.typeNameOfIBinder(), "binder").build();
        MethodSpec methodAsInterface = MethodSpec.methodBuilder("asInterface")
                                               .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                               .addParameter(parameterSpecBinder)
                                               .returns(TypeName.get(typeElement.asType()))
                                               .addStatement("return new $L($N)", generatedClassName, parameterSpecBinder)
                                               .build();

        TypeSpec.Builder typeImplBuilder = TypeSpec.classBuilder(generatedClassName)
                                                 .addSuperinterface(TypeName.get(typeElement.asType())) //实现指定接口
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
                                                                .addAnnotation(Override.class)
                                                                .returns(TypeName.get(methodElement.getReturnType()))
                                                                .addException(ClassName.get("android.os", "RemoteException"));

            for (VariableElement parameterElement : methodElement.getParameters())
            {
                interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()), parameterElement.getSimpleName().toString());
            }
            interfaceMethodBuilder.addStatement("$1T data = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
            interfaceMethodBuilder.addStatement("$1T reply = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
            interfaceMethodBuilder.beginControlFlow("try");
            for (VariableElement parameterElement : methodElement.getParameters())
            {
                mMessager.printMessage(Diagnostic.Kind.NOTE, "parameter type:" + parameterElement.asType().toString());
                if (ClassHelper.isThirdPartyClass(parameterElement.asType().toString()))
                {
                    //the parameter should be a thirdparty class, we regard it as a parceable
                    interfaceMethodBuilder.addStatement("$N.writeToParcel(data, 0)", parameterElement.getSimpleName());
                }
                else
                {
                    interfaceMethodBuilder.addStatement("data.$L($N)", TypeMirrorHelper.getParcelWriteString(parameterElement.asType()),
                            parameterElement.getSimpleName());
                }
            }
            interfaceMethodBuilder.addStatement("$N.transact($N, data, reply, 0)", fieldSpecRemote, fieldSpecMethodId);
            interfaceMethodBuilder.addStatement("reply.readException()");
            if (methodElement.getReturnType().getKind() != TypeKind.VOID)
            {
                String returnTypeClassName = methodElement.getReturnType().toString();
                if (ClassHelper.isThirdPartyClass(returnTypeClassName))
                {
                    interfaceMethodBuilder.addStatement("return $T.CREATOR.createFromParcel(reply)", methodElement.getReturnType());
                }
                else if (ClassHelper.isList(returnTypeClassName))
                {
                    interfaceMethodBuilder.addStatement("$T result = new $T()", List.class, ArrayList.class);
                    interfaceMethodBuilder.addStatement("reply.readList(result, getClass().getClassLoader())");
                    interfaceMethodBuilder.addStatement("return result");
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
}
