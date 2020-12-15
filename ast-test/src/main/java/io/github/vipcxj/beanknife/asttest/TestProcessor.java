package io.github.vipcxj.beanknife.asttest;

import com.google.auto.service.AutoService;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({"beanknife.asttest/io.github.vipcxj.beanknife.asttest.TestAnnotation"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class TestProcessor extends AbstractProcessor {

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        System.out.println("aaaaaaaaaaaa");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("xxxxxxxxxxxxxxxx");
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(TestAnnotation.class);
        for (Element element : elements) {
            TreePath path = trees.getPath(element);
            CompilationUnitTree unit = path.getCompilationUnit();
            Tree tree = trees.getTree(element);
        }

        return false;
    }
}
