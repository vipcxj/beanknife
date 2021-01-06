package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.DocumentedTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.InheritableTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.TypeAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotation;

@UseAnnotation(TypeAnnotation.class)
@UseAnnotation(DocumentedTypeAnnotation.class)
@UseAnnotation(InheritableTypeAnnotation.class)
public class InheritedAnnotationBeanViewConfigure {
}
