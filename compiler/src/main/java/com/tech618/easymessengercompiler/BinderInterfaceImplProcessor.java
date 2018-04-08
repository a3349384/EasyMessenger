package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
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
import javax.lang.model.type.TypeKind;
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
                                                   .addModifiers(Modifier.PUBLIC)
                                                   .addParameter(parameterSpecInterfaceImpl)
                                                   .addStatement("$N = $N", fieldSpecInterfaceImpl, parameterSpecInterfaceImpl)
                                                   .build();

        TypeSpec.Builder typeImplBuilder = TypeSpec.classBuilder(generatedClassName)
                                                   .addModifiers(Modifier.PUBLIC)
                                                   .superclass(ClassName.get("android.os", "Binder"))
                                                   .addField(fieldSpecInterfaceImpl)
                                                   .addMethod(methodSpecConstructor);

        ParameterSpec onTransactMethodCodeParameter = ParameterSpec.builder(int.class, "code").build();
        ParameterSpec onTransactMethodDataParameter = ParameterSpec.builder(TypeNameHelper.typeNameOfParcel(), "data").build();
        ParameterSpec onTransactMethodReplyParameter = ParameterSpec.builder(TypeNameHelper.typeNameOfParcel(), "reply").build();
        ParameterSpec onTransactMethodFlagsParameter = ParameterSpec.builder(int.class, "flags").build();
        MethodSpec.Builder onTransactMethodBuilder = MethodSpec.methodBuilder("onTransact")
                                                             .addModifiers(Modifier.PUBLIC)
                                                             .returns(boolean.class)
                                                             .addAnnotation(Override.class)
                                                             .addException(TypeNameHelper.typeNameOfRemoteException())
                                                             .addParameter(onTransactMethodCodeParameter)
                                                             .addParameter(onTransactMethodDataParameter)
                                                             .addParameter(onTransactMethodReplyParameter)
                                                             .addParameter(onTransactMethodFlagsParameter)
                .beginControlFlow("switch($N)", onTransactMethodCodeParameter);
        for (int i = 0; i < methodElements.size(); i++)
        {
            ExecutableElement methodElement = methodElements.get(i);
            String methodName = methodElement.getSimpleName().toString();
            FieldSpec fieldSpecMethodId = FieldSpec.builder(TypeName.INT, "TRANSACTION_" + methodName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                                  .initializer("$T.FIRST_CALL_TRANSACTION + $L", TypeNameHelper.typeNameOfIBinder(), i + 1)
                                                  .build();
            typeImplBuilder.addField(fieldSpecMethodId);

            onTransactMethodBuilder.beginControlFlow("case $N:", fieldSpecMethodId);
            List<String> parameterNames = new ArrayList<>(methodElement.getParameters().size());
            for (VariableElement parameterElement : methodElement.getParameters())
            {
                String parameterName = parameterElement.getSimpleName().toString();
                //add statement like: int _arg0 = data.readInt();
                onTransactMethodBuilder.addStatement(generateParcelRead(onTransactMethodDataParameter.name, parameterName, methodElement.getReturnType().getKind()));
                parameterNames.add(parameterName);
            }
            // add statement like: int result = mInterfaceImpl.intTest(num1, num2);
            if (methodElement.getReturnType().getKind() == TypeKind.VOID)
            {
                onTransactMethodBuilder.addStatement("$N.$N($L)", fieldSpecInterfaceImpl, methodElement.getSimpleName(),
                        getMethodParameterStringByParameterNames(parameterNames));
            }
            else
            {
                onTransactMethodBuilder.addStatement("$L result = $N.$N($L)", getJavaTypeStringByTypeKind(methodElement.getReturnType().getKind()),
                        fieldSpecInterfaceImpl, methodElement.getSimpleName(), getMethodParameterStringByParameterNames(parameterNames));
            }


            onTransactMethodBuilder.addStatement("$N.writeNoException()", onTransactMethodReplyParameter);
            if (methodElement.getReturnType().getKind() != TypeKind.VOID)
            {
                onTransactMethodBuilder.addStatement("$N.$L(result)", onTransactMethodReplyParameter, getParcelWriteString(methodElement.getReturnType().getKind()));
            }
            onTransactMethodBuilder.addStatement("return true");
            onTransactMethodBuilder.endControlFlow();
        }
        onTransactMethodBuilder.endControlFlow();
        onTransactMethodBuilder.addStatement("return super.onTransact($N, $N, $N, $N)", onTransactMethodCodeParameter,
                onTransactMethodDataParameter, onTransactMethodReplyParameter, onTransactMethodFlagsParameter);
        typeImplBuilder.addMethod(onTransactMethodBuilder.build());
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

    private CodeBlock generateParcelRead(String parcelName, String parameterName, TypeKind parameterType)
    {
        // generate like: int _arg0 = data.readInt();
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        switch (parameterType)
        {
            case INT:
            {
                codeBlockBuilder.add("int $L = $L.readInt()", parameterName, parcelName);
                break;
            }
            case BYTE:
            {
                codeBlockBuilder.add("byte $L = $L.readByte()", parameterName, parcelName);
                break;
            }
            case LONG:
            {
                codeBlockBuilder.add("long $L = $L.readLong()", parameterName, parcelName);
                break;
            }
            case FLOAT:
            {
                codeBlockBuilder.add("float $L = $L.readFloat()", parameterName, parcelName);
                break;
            }
        }
        return codeBlockBuilder.build();
    }

    private String getParcelWriteString(TypeKind parameterType)
    {
        switch (parameterType)
        {
            case INT:
            {
                return "writeInt";
            }
            case BYTE:
            {
                return "writeByte";
            }
            case LONG:
            {
                return "writeLong";
            }
            case FLOAT:
            {
                return "writeFloat";
            }
        }
        return "null";
    }

    private String getJavaTypeStringByTypeKind(TypeKind typeKind)
    {
        switch (typeKind)
        {
            case INT:
            {
                return "int";
            }
            case BYTE:
            {
                return "byte";
            }
            case LONG:
            {
                return "long";
            }
            case FLOAT:
            {
                return "float";
            }
        }
        return "null";
    }

    private String getMethodParameterStringByParameterNames(List<String> parameterNames)
    {
        if (parameterNames.size() == 0)
        {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameterNames.size() - 1; i++)
        {
            builder.append(parameterNames.get(i));
            builder.append(", ");
        }
        builder.append(parameterNames.get(parameterNames.size() - 1));
        return builder.toString();
    }

    private void log(String log)
    {
        mMessager.printMessage(Diagnostic.Kind.NOTE, TAG + ": " + log);
    }
}
