package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.FieldAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.FieldAnnotation2;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation2;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.UnUseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationDest;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationSource;

import java.util.Date;

@ViewOf(value = AnnotationBean.class, includePattern = ".*")
@UnUseAnnotation(FieldAnnotation2.class)
@UseAnnotation(value = PropertyAnnotation2.class, from = { AnnotationSource.CONFIG, AnnotationSource.TARGET_FIELD })
public class AnnotationBeanViewConfigure extends InheritedAnnotationBeanViewConfigure {

    @UseAnnotation(FieldAnnotation2.class)
    @OverrideViewProperty(AnnotationBeanMeta.c)
    private String[] c;

    // FieldAnnotation1 can not be put on a method, so this annotation is ignored.
    @UseAnnotation(value = FieldAnnotation1.class, dest = AnnotationDest.GETTER)
    // PropertyAnnotation1 is put on the field in the original class,
    // but will be put on getter method in the generated class.
    @UseAnnotation(value = PropertyAnnotation1.class, dest = AnnotationDest.GETTER)
    @OverrideViewProperty(AnnotationBeanMeta.d)
    private int d;

    @PropertyAnnotation2(stringValue = "config_e")
    @OverrideViewProperty(AnnotationBeanMeta.e)
    private Date e;

}
