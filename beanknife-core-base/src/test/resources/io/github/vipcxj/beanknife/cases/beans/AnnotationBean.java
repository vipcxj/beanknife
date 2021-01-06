package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.DocumentedTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.TypeAnnotation;
import io.github.vipcxj.beanknife.cases.models.AEnum;

import java.util.Date;

@TypeAnnotation(Date.class)
@DocumentedTypeAnnotation(enumValue = AEnum.B, enumValues = {AEnum.C, AEnum.B, AEnum.A})
public class AnnotationBean extends BaseAnnotationBean {
}
