package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.FieldAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.InheritableTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.ValueAnnotation1;
import io.github.vipcxj.beanknife.cases.models.AEnum;

@InheritableTypeAnnotation(
        annotation = @ValueAnnotation1(type = int.class),
        annotations = {
                @ValueAnnotation1(type = void.class),
                @ValueAnnotation1(type = String.class),
                @ValueAnnotation1(type = int[][][].class),
                @ValueAnnotation1(type = Void.class),
                @ValueAnnotation1(type = Void[].class)
        }
)
public class BaseAnnotationBean {

        @FieldAnnotation1(enumClassArray = {AEnum.class, AEnum.class})
        public Class<?> type;
}
