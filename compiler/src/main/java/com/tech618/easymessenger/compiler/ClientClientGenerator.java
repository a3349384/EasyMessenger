package com.tech618.easymessenger.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BinderClient;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by zmy on 2019/2/19.
 * 生成IPC通信Client类
 */

public class ClientClientGenerator
{
    /**
     * 根据{@link BinderClient}的注解类，生成其IPC通信Client类。
     * @param binderClientTypeElement {@link BinderClient}的类信息
     * @param binderClientMethodElements {@link BinderClient}的类方法信息
     * */
    public static TypeSpec generateClient(TypeElement binderClientTypeElement, List<ExecutableElement> binderClientMethodElements)
    {
        String generatedClassName = getClientFullName(binderClientTypeElement);

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
        for (int i = 0; i < binderClientMethodElements.size(); i++)
        {
            ExecutableElement methodElement = binderClientMethodElements.get(i);
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
//                Global.messager.printMessage(Diagnostic.Kind.NOTE, "parameter type:" + parameterElement.asType().toString());
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

    private static void writeParameterToParcel(MethodSpec.Builder clientMethodBuilder, VariableElement parameterElement)
    {
        Name parameterName = parameterElement.getSimpleName();
        TypeKind paratemerTypeKind = parameterElement.asType().getKind();
        switch (paratemerTypeKind)
        {
            //region 值类型
            case BOOLEAN:
            {
                clientMethodBuilder.addStatement("data.writeInt($N ? 1 : 0)", parameterName);
                return;
            }
            case BYTE:
            {
                clientMethodBuilder.addStatement("data.writeByte($N)", parameterName);
                return;
            }
            case CHAR:
            case SHORT:
            case INT:
            {
                clientMethodBuilder.addStatement("data.writeInt($N)", parameterName);
                return;
            }
            case LONG:
            {
                clientMethodBuilder.addStatement("data.writeLong($N)", parameterName);
                return;
            }
            case FLOAT:
            {
                clientMethodBuilder.addStatement("data.writeFloat($N)", parameterName);
                return;
            }
            case DOUBLE:
            {
                clientMethodBuilder.addStatement("data.writeDouble($N)", parameterName);
                return;
            }
            //endregion

            //region 数组
            case ARRAY:
            {
                ArrayType arrayType = (ArrayType)parameterElement.asType();
                TypeMirror arrayComponenetTypeMirror = arrayType.getComponentType();
                TypeKind arrayComponentTypeKind = arrayComponenetTypeMirror.getKind();
                switch (arrayComponentTypeKind)
                {
                    case BOOLEAN:
                    {
                        clientMethodBuilder.addStatement("data.writeBooleanArray($N)", parameterName);
                        break;
                    }
                    case BYTE:
                    {
                        clientMethodBuilder.addStatement("data.writeByteArray($N)", parameterName);
                        break;
                    }
                    case CHAR:
                    {
                        clientMethodBuilder.addStatement("data.writeCharArray($N)", parameterName);
                        break;
                    }
                    case INT:
                    {
                        clientMethodBuilder.addStatement("data.writeIntArray($N)", parameterName);
                        break;
                    }
                    case LONG:
                    {
                        clientMethodBuilder.addStatement("data.writeLongArray($N)", parameterName);
                        break;
                    }
                    case FLOAT:
                    {
                        clientMethodBuilder.addStatement("data.writeFloatArray($N)", parameterName);
                        break;
                    }
                    case DOUBLE:
                    {
                        clientMethodBuilder.addStatement("data.writeDoubleArray($N)", parameterName);
                        break;
                    }
                    default:
                    {
                        //Parcelable array
                        if (TypeMirrorHelper.isParcelable(arrayComponenetTypeMirror))
                        {
                            clientMethodBuilder.addStatement("data.writeTypedArray($N, 0)", parameterName);
                            break;
                        }
                        break;
                    }
                }
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
                    clientMethodBuilder.addStatement("data.writeString($N)", parameterName);
                    return;
                }
                //Parcelable
                if (TypeMirrorHelper.isParcelable(parameterTypeMirror))
                {
                    clientMethodBuilder.addStatement("data.writeInt($N == null ? 0 : 1)", parameterName);
                    clientMethodBuilder.beginControlFlow("if ($N != null)", parameterName);
                    clientMethodBuilder.addStatement("$N.writeToParcel(data, 0)", parameterName);
                    clientMethodBuilder.endControlFlow();
                    return;
                }
                //Serializable
                if (TypeMirrorHelper.isSerializable(parameterTypeMirror))
                {
                    clientMethodBuilder.addStatement("data.writeSerializable($N)", parameterName);
                    return;
                }
                //List
                if (TypeMirrorHelper.isList(parameterTypeMirror))
                {
                    clientMethodBuilder.addStatement("data.writeList($N)", parameterName);
                    return;
                }
            }
            //endregion
        }
    }

    private static void readResponseFromParcel(MethodSpec.Builder clientMethodBuilder, TypeMirror returnType)
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
                clientMethodBuilder.addStatement("return reply.readByte()");
                return;
            }
            case CHAR:
            case SHORT:
            case INT:
            {
                clientMethodBuilder.addStatement("return ($T)reply.readInt()", returnType);
                return;
            }
            case BOOLEAN:
            {
                clientMethodBuilder.addStatement("return reply.readInt() > 0");
                return;
            }
            case LONG:
            {
                clientMethodBuilder.addStatement("return reply.readByte()");
                return;
            }
            case FLOAT:
            {
                clientMethodBuilder.addStatement("return reply.readFloat()");
                return;
            }
            case DOUBLE:
            {
                clientMethodBuilder.addStatement("return reply.readDouble()");
                return;
            }
            //endregion

            //region 数组
            case ARRAY:
            {
                ArrayType arrayType = (ArrayType)returnType;
                TypeMirror arrayComponenetTypeMirror = arrayType.getComponentType();
                TypeKind arrayComponentTypeKind = arrayComponenetTypeMirror.getKind();
                switch (arrayComponentTypeKind)
                {
                    case BOOLEAN:
                    {
                        clientMethodBuilder.addStatement("return reply.createBooleanArray()");
                        break;
                    }
                    case BYTE:
                    {
                        clientMethodBuilder.addStatement("return reply.createByteArray()");
                        break;
                    }
                    case CHAR:
                    {
                        clientMethodBuilder.addStatement("return reply.createCharArray()");
                        break;
                    }
                    case INT:
                    {
                        clientMethodBuilder.addStatement("return reply.createIntArray()");
                        break;
                    }
                    case LONG:
                    {
                        clientMethodBuilder.addStatement("return reply.createLongArray()");
                        break;
                    }
                    case FLOAT:
                    {
                        clientMethodBuilder.addStatement("return reply.createFloatArray()");
                        break;
                    }
                    case DOUBLE:
                    {
                        clientMethodBuilder.addStatement("return reply.createDoubleArray()");
                        break;
                    }
                    default:
                    {
                        //Parcelable array
                        if (TypeMirrorHelper.isParcelable(arrayComponenetTypeMirror))
                        {
                            clientMethodBuilder.addStatement("return reply.createTypedArray($T.CREATOR)",
                                    arrayComponenetTypeMirror);
                            break;
                        }
                        break;
                    }
                }
                return;
            }
            //endregion

            //region 引用类型
            default:
            {
                //String
                if (TypeMirrorHelper.isString(returnType))
                {
                    clientMethodBuilder.addStatement("return reply.readString()");
                    return;
                }
                //Parcelable
                if (TypeMirrorHelper.isParcelable(returnType))
                {
                    clientMethodBuilder.addStatement("int __nullFlag = reply.readInt()");
                    clientMethodBuilder.beginControlFlow("if (__nullFlag > 0)");
                    clientMethodBuilder.addStatement("return $T.CREATOR.createFromParcel(reply)", returnType);
                    clientMethodBuilder.nextControlFlow("else");
                    clientMethodBuilder.addStatement("return null");
                    clientMethodBuilder.endControlFlow();
                    return;
                }
                //Serializable
                if (TypeMirrorHelper.isSerializable(returnType))
                {
                    clientMethodBuilder.addStatement("return reply.readSerializable()");
                    return;
                }
                //List
                if (TypeMirrorHelper.isList(returnType))
                {
                    clientMethodBuilder.addStatement("$T __list = new $T()", List.class, ArrayList.class);
                    clientMethodBuilder.addStatement("reply.readList(__list, getClass().getClassLoader())");
                    clientMethodBuilder.addStatement("return list");
                    return;
                }
            }
            //endregion
        }
    }
}
