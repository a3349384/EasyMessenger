package cn.zmy.easymessenger.compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

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

import cn.zmy.easymessenger.BinderServer;

/**
 * Created by zmy on 2018/4/7.
 */

public class BinderServerProcessor extends AbstractProcessor
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
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BinderServer.class))
        {
            String binderServerName = elementClass.toString();
            if (elementClass.getKind() != ElementKind.CLASS)
            {
                String errorMsg = binderServerName + " is not a class!";
                Global.messager.printMessage(Diagnostic.Kind.ERROR, errorMsg);
                throw new RuntimeException(errorMsg);
            }
            //it's type is class, so it must be a TypeElement
            TypeElement binderServerTypeElement = (TypeElement) elementClass;
            List<ExecutableElement> binderServerMethodElements = new ArrayList<>();
            for (Element elementMember : binderServerTypeElement.getEnclosedElements())
            {
                if (elementMember.getKind() == ElementKind.METHOD)
                {
                    if (elementMember.getModifiers().size() == 1 && elementMember.getModifiers().contains(Modifier.PUBLIC))
                    {
                        //it's type is method, so it must be a ExecutableElement
                        binderServerMethodElements.add((ExecutableElement) elementMember);
                    }
                }
            }
            String packageName = Global.elements.getPackageOf(binderServerTypeElement).getQualifiedName().toString();
            TypeSpec binderTypeSpec = ServerBinderGenerator.generate(binderServerTypeElement, binderServerMethodElements);
            TypeSpec providerTypeSpec = ServerContentProviderGenerator.generate(packageName,
                    binderServerTypeElement.getSimpleName().toString(), binderTypeSpec.name);
            try
            {
                JavaFile.builder(packageName, binderTypeSpec).build().writeTo(mFiler);
                JavaFile.builder(packageName, providerTypeSpec).build().writeTo(mFiler);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>(1);
        types.add(BinderServer.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }
}
