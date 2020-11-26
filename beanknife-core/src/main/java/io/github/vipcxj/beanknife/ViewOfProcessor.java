package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("io.github.vipcxj.beanknife.ViewOf")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class ViewOfProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
