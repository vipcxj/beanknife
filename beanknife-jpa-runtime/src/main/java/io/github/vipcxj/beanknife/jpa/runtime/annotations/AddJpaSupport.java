package io.github.vipcxj.beanknife.jpa.runtime.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface AddJpaSupport {
    Class<?>[] value() default {};
    Class<?>[] extraTargets() default {};
}
