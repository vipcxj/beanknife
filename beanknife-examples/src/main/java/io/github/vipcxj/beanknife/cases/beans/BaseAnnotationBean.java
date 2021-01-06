package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.InheritableTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.ValueAnnotation;

@InheritableTypeAnnotation(
        annotation = @ValueAnnotation(type = int.class),
        annotations = {
                @ValueAnnotation(type = void.class),
                @ValueAnnotation(type = String.class),
                @ValueAnnotation(type = int[][][].class),
                @ValueAnnotation(type = Void.class),
                @ValueAnnotation(type = Void[].class)
        }
)
public class BaseAnnotationBean {
}
