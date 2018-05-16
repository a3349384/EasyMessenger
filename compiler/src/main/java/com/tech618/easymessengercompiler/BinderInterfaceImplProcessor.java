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
                        if (elementMember.getModifiers().size() == 1 && elementMember.getModifiers().contains(Modifier.PUBLIC))
                        {
                            mMessager.printMessage(Diagnostic.Kind.NOTE, "Start processing: " + elementClassString + "#" + elementMember.toString());
                            //it's type is method, so it must be a ExecutableElement
                            methodElements.add((ExecutableElement) elementMember);
                        }
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
        String generatedClassName = typeElement.getSimpleName().toString() + "Binder";
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
            boolean isNullFlagDefined = false;
            for (VariableElement parameterElement : methodElement.getParameters())
            {
                String parameterClassName = parameterElement.asType().toString();
                if (ClassHelper.isThirdPartyClass(parameterClassName))
                {
                    //the parameter should be a thirdparty class, we regard it as a parceable
                    onTransactMethodBuilder.addStatement("$1T $2N = null", parameterElement.asType(), parameterElement.getSimpleName());
                    if (!isNullFlagDefined)
                    {
                        onTransactMethodBuilder.addStatement("int isNullFlag = $N.readInt()", onTransactMethodDataParameter);
                        isNullFlagDefined = true;
                    }
                    else
                    {
                        onTransactMethodBuilder.addStatement("isNullFlag = $N.readInt()", onTransactMethodDataParameter);
                    }
                    onTransactMethodBuilder.beginControlFlow("if (isNullFlag > 0)");
                    onTransactMethodBuilder.addStatement("$N = $T.CREATOR.createFromParcel($N)", parameterElement.getSimpleName(),
                            parameterElement.asType(), onTransactMethodDataParameter);
                    onTransactMethodBuilder.endControlFlow();
                }
                else if (ClassHelper.isList(parameterClassName))
                {
                    onTransactMethodBuilder.addStatement("$T $N = new $T<>()", parameterElement.asType(), parameterElement.getSimpleName(), ArrayList.class);
                    onTransactMethodBuilder.addStatement("$N.readList($N, getClass().getClassLoader())", onTransactMethodDataParameter, parameterElement.getSimpleName());
                }
                else if (parameterElement.asType().getKind() == TypeKind.BOOLEAN)
                {
                    onTransactMethodBuilder.addStatement("$T $N = $N.readInt() > 0 ? true : false", parameterElement.asType(),
                            parameterElement.getSimpleName(), onTransactMethodDataParameter);
                }
                else
                {
                    //add statement like: int arg0 = data.readInt();
                    onTransactMethodBuilder.addStatement("$T $N = $N.$L()", parameterElement.asType(),
                            parameterElement.getSimpleName(), onTransactMethodDataParameter, TypeMirrorHelper.getParcelReadString(parameterElement.asType()));
                }
                parameterNames.add(parameterElement.getSimpleName().toString());
            }

            if (methodElement.getReturnType().getKind() == TypeKind.VOID)
            {
                onTransactMethodBuilder.addStatement("$N.$N($L)", fieldSpecInterfaceImpl, methodElement.getSimpleName(),
                        ParameterHelper.getMethodParameterStringByParameterNames(parameterNames));
            }
            else
            {
                // add statement like: int result = mInterfaceImpl.intTest(num1, num2);
                onTransactMethodBuilder.addStatement("$T result = $N.$N($L)", methodElement.getReturnType(),
                        fieldSpecInterfaceImpl, methodElement.getSimpleName(), ParameterHelper.getMethodParameterStringByParameterNames(parameterNames));
            }

            onTransactMethodBuilder.addStatement("$N.writeNoException()", onTransactMethodReplyParameter);
            if (methodElement.getReturnType().getKind() != TypeKind.VOID)
            {
                if (ClassHelper.isThirdPartyClass(methodElement.getReturnType().toString()))
                {
                    onTransactMethodBuilder.addStatement("int isResultNullFlag = result == null ? 0 : 1");
                    onTransactMethodBuilder.addStatement("$N.writeInt(isResultNullFlag)", onTransactMethodReplyParameter);
                    onTransactMethodBuilder.beginControlFlow("if (isResultNullFlag > 0)");
                    onTransactMethodBuilder.addStatement("result.writeToParcel($N, $T.PARCELABLE_WRITE_RETURN_VALUE)", onTransactMethodReplyParameter, TypeNameHelper.typeNameOfParcelable());
                    onTransactMethodBuilder.endControlFlow();
                }
                else if (methodElement.getReturnType().getKind() == TypeKind.BOOLEAN)
                {
                    onTransactMethodBuilder.addStatement("$N.writeInt(result ? 1 : 0)", onTransactMethodReplyParameter);
                }
                else
                {
                    //add statement like: reply.writeString(result)
                    onTransactMethodBuilder.addStatement("$N.$L(result)", onTransactMethodReplyParameter, TypeMirrorHelper.getParcelWriteString(methodElement.getReturnType()));
                }
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

    private void log(String log)
    {
        mMessager.printMessage(Diagnostic.Kind.NOTE, TAG + ": " + log);
    }
}
