package io.github.vipcxj.beanknife.cases.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
public @interface ValueAnnotation {
    Class<?>[] type();
}
