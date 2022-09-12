package io.github.vipcxj.beanknife.core.models;

import com.sun.source.util.Trees;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.ElementsCompatible;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Objects;

public class TypeContext extends Context {

    private final TypeElement element;

    public TypeContext(@NonNull Trees trees, @NonNull ProcessingEnvironment processingEnv, ProcessorData processorData, TypeElement typeElement, String packageName) {
        super(trees, processingEnv, processorData);
        this.element = typeElement;
        this.packageName = packageName;
    }

    public void collectData() {
        String thePackageName = ElementsCompatible.getPackageNameOf(element);
        collectData(element, null, Objects.equals(thePackageName, packageName));
    }
}
