package cn.zmy.easymessenger.compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import cn.zmy.easymessenger.BinderClient;

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

public class BinderClientProcessor extends AbstractProcessor
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
        for (Element elementClass : roundEnv.getElementsAnnotatedWith(BinderClient.class))
        {
            String binderClientName = elementClass.toString();
            if (elementClass.getKind() != ElementKind.INTERFACE)
            {
                Global.messager.printMessage(Diagnostic.Kind.ERROR, binderClientName + " is not a interface!");
                return false;
            }
            TypeElement binderClientTypeElement = (TypeElement) elementClass;
            List<ExecutableElement> binderClientMethodElements = new ArrayList<>();
            for (Element elementMember : binderClientTypeElement.getEnclosedElements())
            {
                if (elementMember.getKind() == ElementKind.METHOD)
                {
                    //it's type is method, so it must be a ExecutableElement
                    binderClientMethodElements.add((ExecutableElement) elementMember);
                }
            }
            String packageName = Global.elements.getPackageOf(binderClientTypeElement).getQualifiedName().toString();
            TypeSpec typeSpecClient = ClientClientGenerator.generateClient(binderClientTypeElement, binderClientMethodElements);
            JavaFile javaFileClient = JavaFile.builder(packageName, typeSpecClient).build();
            TypeSpec typeSpecHelper = ClientHelperGenerator.generateHelper(binderClientTypeElement, binderClientMethodElements);
            JavaFile javaFileHelper = JavaFile.builder(packageName, typeSpecHelper).build();
            try
            {
                javaFileClient.writeTo(mFiler);
                Global.messager.printMessage(Diagnostic.Kind.NOTE,  packageName + "." + typeSpecClient.name + " has generated!");
                javaFileHelper.writeTo(mFiler);
                Global.messager.printMessage(Diagnostic.Kind.NOTE,  packageName + "." + typeSpecHelper.name + " has generated!");
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
        types.add(BinderClient.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }
}
