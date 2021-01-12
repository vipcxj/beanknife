package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ExtraParam;
import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "ExtraParamsBeanView")
public class ExtraParamsViewConfigure extends IncludeAllBaseConfigure {

    @NewViewProperty("x")
    public static Class<?> x(@ExtraParam("x") Class<?> x) {
        return x;
    }

    @NewViewProperty("y")
    public static String y(@ExtraParam("x") Class<?> x) {
        return x.getName();
    }

    @NewViewProperty("z")
    public static ExtraParamsBeanView z(@ExtraParam("z") ExtraParamsBeanView z) {
        return z;
    }
}
