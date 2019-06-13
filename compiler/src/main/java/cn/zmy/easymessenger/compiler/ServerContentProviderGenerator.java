package cn.zmy.easymessenger.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by zmy on 2019/6/13.
 * 生成ContentProvider
 */
public class ServerContentProviderGenerator
{
    private static final String sGetBinderName = "getBinder";

    public static TypeSpec generate(String packageName, String serverName, String binderName)
    {
        String generatedClassName = serverName + "Provider";
        TypeSpec.Builder providerTypeSpec = TypeSpec.classBuilder(generatedClassName)
                                                   .addModifiers(Modifier.PUBLIC)
                                                   .superclass(TypeNameHelper.typeNameOfAbsBinderProvider());
        MethodSpec getBinderMethod = MethodSpec.methodBuilder(sGetBinderName)
                                               .addAnnotation(Override.class)
                                               .addModifiers(Modifier.PROTECTED)
                                               .returns(TypeNameHelper.typeNameOfIBinder())
                                               .addStatement("return new $T()", ClassName.get(packageName, binderName))
                                               .build();
        providerTypeSpec.addMethod(getBinderMethod);
        return providerTypeSpec.build();
    }
}
