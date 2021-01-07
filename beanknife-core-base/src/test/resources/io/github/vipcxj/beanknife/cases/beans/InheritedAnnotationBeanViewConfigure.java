package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.*;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotation;

@UseAnnotation(TypeAnnotation.class)
@UseAnnotation(DocumentedTypeAnnotation.class)
@UseAnnotation(InheritableTypeAnnotation.class)
@UseAnnotation({ FieldAnnotation1.class, FieldAnnotation2.class })
@UseAnnotation(MethodAnnotation1.class)
@UseAnnotation(PropertyAnnotation1.class)
public class InheritedAnnotationBeanViewConfigure {
}
