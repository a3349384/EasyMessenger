package com.tech618.easymessengercompiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BinderInterfaceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by zmy on 2018/4/7.
 */

public class BinderInterfaceImplProcessor extends AbstractProcessor
{
    public static final String TAG = "BinderInterfaceImplProcessor";

    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        Global.messager = processingEnv.getMessager();
        Global.types = processingEnv.getTypeUtils();
        Global.elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BinderInterfaceImpl.class))
        {
            String elementClassString = elementClass.toString();
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
                            Global.messager.printMessage(Diagnostic.Kind.NOTE, "Start processing: " + elementClassString + "#" + elementMember.toString());
                            //it's type is method, so it must be a ExecutableElement
                            methodElements.add((ExecutableElement) elementMember);
                        }
                    }
                }
                TypeSpec typeSpecImpl = BinderGenerator.generateBinder(typeElement, methodElements);
                JavaFile javaFile = JavaFile.builder(Global.elements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecImpl).build();
                try
                {
                    javaFile.writeTo(mFiler);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                Global.messager.printMessage(Diagnostic.Kind.WARNING, elementClass.toString() + " is not a class, ignored!");
            }
        }
        return true;
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
}
