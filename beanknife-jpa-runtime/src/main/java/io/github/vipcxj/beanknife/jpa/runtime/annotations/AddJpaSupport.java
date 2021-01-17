package io.github.vipcxj.beanknife.jpa.runtime.annotations;

public @interface AddJpaSupport {
    Class<?>[] value() default {};
    Class<?>[] extraTargets() default {};
}
