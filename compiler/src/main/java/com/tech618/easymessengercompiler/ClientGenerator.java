package com.tech618.easymessengercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by zmy on 2019/2/19.
 * 生成IPC通信Client类
 */

public class ClientGenerator
{
    /**
     * 根据{@link com.tech618.easymessenger.BinderInterface}的注解类，生成其IPC通信Client类。
     * @param binderInterfaceTypeElement {@link com.tech618.easymessenger.BinderInterface}的类信息
     * @param binderInterfaceMethodElements {@link com.tech618.easymessenger.BinderInterface}的类方法信息
     * */
    public static TypeSpec generateClient(TypeElement binderInterfaceTypeElement, List<ExecutableElement> binderInterfaceMethodElements)
    {
        String generatedClassName = getClientFullName(binderInterfaceTypeElement);

        TypeName typeNameOfIBinder = TypeNameHelper.typeNameOfIBinder();
        //生成类全局属性：mRemote和TRANSACTION_CODE
        FieldSpec fieldSpecRemote = FieldSpec.builder(typeNameOfIBinder, "mRemote", Modifier.PRIVATE).build();
        FieldSpec fieldSpecTransactionCode = FieldSpec.builder(int.class, "TRANSACTION_CODE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                                     .initializer("$T.FIRST_CALL_TRANSACTION + $L", typeNameOfIBinder, 1)
                                                     .build();
        //生成构造方法
        ParameterSpec parameterSpecRemote = ParameterSpec.builder(typeNameOfIBinder, "remote").build();
        MethodSpec methodSpecConstructor = MethodSpec.constructorBuilder()
                                                   .addModifiers(Modifier.PRIVATE)
                                                   .addParameter(parameterSpecRemote)
                                                   .addStatement("$N = $N", fieldSpecRemote, parameterSpecRemote)
                                                   .build();
        //生成fromBinder静态方法
        ParameterSpec parameterSpecBinder = ParameterSpec.builder(typeNameOfIBinder, "binder").build();
        MethodSpec methodAsInterface = MethodSpec.methodBuilder("fromBinder")
                                               .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                               .addParameter(parameterSpecBinder)
                                               .returns(ClassName.bestGuess(generatedClassName))
                                               .addStatement("return new $L($N)", generatedClassName, parameterSpecBinder)
                                               .build();
        //开始生成Client类
        TypeSpec.Builder typeImplBuilder = TypeSpec.classBuilder(generatedClassName)
                                                   .addModifiers(Modifier.PUBLIC)
                                                   .addField(fieldSpecRemote)
                                                   .addField(fieldSpecTransactionCode)
                                                   .addMethod(methodSpecConstructor)
                                                   .addMethod(methodAsInterface);
        //开始生成Client类中的IPC通信方法
        for (int i = 0; i < binderInterfaceMethodElements.size(); i++)
        {
            ExecutableElement methodElement = binderInterfaceMethodElements.get(i);
            //方法的名称就等于IPC定义的方法名称
            String methodName = methodElement.getSimpleName().toString();
            //生成方法签名
            MethodSpec.Builder interfaceMethodBuilder = MethodSpec.methodBuilder(methodName)
                                                                .addModifiers(Modifier.PUBLIC)
                                                                .returns(TypeName.get(methodElement.getReturnType()))
                                                                .addException(TypeNameHelper.typeNameOfRemoteException());
            for (VariableElement parameterElement : methodElement.getParameters())
            {
                interfaceMethodBuilder.addParameter(TypeName.get(parameterElement.asType()),
                        parameterElement.getSimpleName().toString());
            }
            //生成方法代码
            interfaceMethodBuilder.addStatement("$1T data = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
            interfaceMethodBuilder.addStatement("$1T reply = $1T.obtain()", TypeNameHelper.typeNameOfParcel());
            interfaceMethodBuilder.addStatement("data.writeString($S)", methodName);
            interfaceMethodBuilder.beginControlFlow("try");
            boolean isNullFlagDefined = false;
            for (VariableElement parameterElement : methodElement.getParameters())
            {
                Global.messager.printMessage(Diagnostic.Kind.NOTE, "parameter type:" + parameterElement.asType().toString());
                writeParameterToParcel(interfaceMethodBuilder, parameterElement);
            }
            interfaceMethodBuilder.addStatement("$N.transact($N, data, reply, 0)", fieldSpecRemote, fieldSpecTransactionCode);
            interfaceMethodBuilder.addStatement("reply.readException()");
            readResponseFromParcel(interfaceMethodBuilder, methodElement.getReturnType());
            interfaceMethodBuilder.endControlFlow();
            interfaceMethodBuilder.beginControlFlow("finally");
            interfaceMethodBuilder.addStatement("data.recycle()");
            interfaceMethodBuilder.addStatement("reply.recycle()");
            interfaceMethodBuilder.endControlFlow();
            typeImplBuilder.addMethod(interfaceMethodBuilder.build());
        }

        return typeImplBuilder.build();
    }

    public static String getClientFullName(TypeElement binderInterfaceTypeElement)
    {
        return binderInterfaceTypeElement.getSimpleName().toString() + "Client";
    }

    private static void writeParameterToParcel(MethodSpec.Builder interfaceMethodBuilder, VariableElement parameterElement)
    {
        Name parameterName = parameterElement.getSimpleName();
        TypeKind paratemerTypeKind = parameterElement.asType().getKind();
        switch (paratemerTypeKind)
        {
            //region 值类型
            case BYTE:
            {
                interfaceMethodBuilder.addStatement("data.writeByte($N)", parameterName);
                return;
            }
            case CHAR:
            case SHORT:
            case INT:
            {
                interfaceMethodBuilder.addStatement("data.writeInt($N)", parameterName);
                return;
            }
            case BOOLEAN:
            {
                interfaceMethodBuilder.addStatement("data.writeInt($N ? 1 : 0)", parameterName);
                return;
            }
            case LONG:
            {
                interfaceMethodBuilder.addStatement("data.writeLong($N)", parameterName);
                return;
            }
            case FLOAT:
            {
                interfaceMethodBuilder.addStatement("data.writeFloat($N)", parameterName);
                return;
            }
            case DOUBLE:
            {
                interfaceMethodBuilder.addStatement("data.writeDouble($N)", parameterName);
                return;
            }
            //endregion

            //region 引用类型
            default:
            {
                TypeMirror parameterTypeMirror = parameterElement.asType();
                //String
                if (TypeMirrorHelper.isString(parameterTypeMirror))
                {
                    interfaceMethodBuilder.addStatement("data.writeString($N)", parameterName);
                    return;
                }
                //Parcelable
                if (TypeMirrorHelper.isParcelable(parameterTypeMirror))
                {
                    interfaceMethodBuilder.addStatement("data.writeInt($N == null ? 0 : 1)", parameterName);
                    interfaceMethodBuilder.beginControlFlow("if ($N != null)", parameterName);
                    interfaceMethodBuilder.addStatement("$N.writeToParcel(data, 0)", parameterName);
                    interfaceMethodBuilder.endControlFlow();
                    return;
                }
                //Serializable
                if (TypeMirrorHelper.isSerializable(parameterTypeMirror))
                {
                    interfaceMethodBuilder.addStatement("data.writeSerializable($N)", parameterName);
                    return;
                }
                //List
                if (TypeMirrorHelper.isList(parameterTypeMirror))
                {
                    interfaceMethodBuilder.addStatement("data.writeList($N)", parameterName);
                    return;
                }
            }
            //endregion
        }
    }

    private static void readResponseFromParcel(MethodSpec.Builder interfaceMethodBuilder, TypeMirror returnType)
    {
        TypeKind returnTypeKind = returnType.getKind();
        TypeName returnName = TypeName.get(returnType);
        switch (returnTypeKind)
        {
            case VOID:
            {
                return;
            }
            //region 值类型
            case BYTE:
            {
                interfaceMethodBuilder.addStatement("return reply.readByte()");
                return;
            }
            case CHAR:
            case SHORT:
            case INT:
            {
                interfaceMethodBuilder.addStatement("return ($T)reply.readInt()", returnType);
                return;
            }
            case BOOLEAN:
            {
                interfaceMethodBuilder.addStatement("return reply.readInt() > 0");
                return;
            }
            case LONG:
            {
                interfaceMethodBuilder.addStatement("return reply.readByte()");
                return;
            }
            case FLOAT:
            {
                interfaceMethodBuilder.addStatement("return reply.readFloat()");
                return;
            }
            case DOUBLE:
            {
                interfaceMethodBuilder.addStatement("return reply.readDouble()");
                return;
            }
            //endregion

            //region 引用类型
            default:
            {
                //String
                if (TypeMirrorHelper.isString(returnType))
                {
                    interfaceMethodBuilder.addStatement("return reply.readString()");
                    return;
                }
                //Parcelable
                if (TypeMirrorHelper.isParcelable(returnType))
                {
                    interfaceMethodBuilder.addStatement("int __nullFlag = reply.readInt()");
                    interfaceMethodBuilder.beginControlFlow("if (__nullFlag > 0)");
                    interfaceMethodBuilder.addStatement("return $T.CREATOR.createFromParcel(reply)", returnType);
                    interfaceMethodBuilder.nextControlFlow("else");
                    interfaceMethodBuilder.addStatement("return null");
                    interfaceMethodBuilder.endControlFlow();
                    return;
                }
                //Serializable
                if (TypeMirrorHelper.isSerializable(returnType))
                {
                    interfaceMethodBuilder.addStatement("return reply.readSerializable()");
                    return;
                }
                //List
                if (TypeMirrorHelper.isList(returnType))
                {
                    interfaceMethodBuilder.addStatement("$T __list = new $T()", List.class, ArrayList.class);
                    interfaceMethodBuilder.addStatement("reply.readList(__list, getClass().getClassLoader())");
                    interfaceMethodBuilder.addStatement("return list");
                    return;
                }
            }
            //endregion
        }
    }
}
