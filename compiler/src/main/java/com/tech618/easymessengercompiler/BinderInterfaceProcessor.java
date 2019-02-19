package com.tech618.easymessengercompiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.tech618.easymessenger.BinderInterface;

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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by zmy on 2018/4/5.
 */

public class BinderInterfaceProcessor extends AbstractProcessor
{
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
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BinderInterface.class))
        {
            String elementClassString = elementClass.toString();
            Global.messager.printMessage(Diagnostic.Kind.NOTE, "Start processing: " + elementClassString);
            if (elementClass.getKind() == ElementKind.INTERFACE)
            {
                //it's type is class, so it must be a TypeElement
                TypeElement typeElement = (TypeElement) elementClass;
                List<ExecutableElement> methodElements = new ArrayList<>();
                for (Element elementMember : typeElement.getEnclosedElements())
                {
                    if (elementMember.getKind() == ElementKind.METHOD)
                    {
                        Global.messager.printMessage(Diagnostic.Kind.NOTE, "Start processing: " + elementClassString + "#" + elementMember.toString());
                        //it's type is method, so it must be a ExecutableElement
                        methodElements.add((ExecutableElement) elementMember);
                    }
                }
                TypeSpec typeSpecClient = ClientGenerator.generateClient(typeElement, methodElements);
                JavaFile javaFileClient = JavaFile.builder(Global.elements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecClient).build();
                TypeSpec typeSpecHelper = HelperGenerator.generateHelper(typeElement, methodElements);
                JavaFile javaFileHelper = JavaFile.builder(Global.elements.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecHelper).build();
                try
                {
                    javaFileClient.writeTo(mFiler);
                    javaFileHelper.writeTo(mFiler);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                Global.messager.printMessage(Diagnostic.Kind.WARNING, elementClass.toString() + " is not a interface, ignored!");
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
}
