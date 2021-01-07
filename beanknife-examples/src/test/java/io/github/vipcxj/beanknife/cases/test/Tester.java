package io.github.vipcxj.beanknife.cases.test;

import io.github.vipcxj.beanknife.cases.beans.*;
import io.github.vipcxj.beanknife.runtime.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Tester {

    private static void checkView(Class<?> type) {
        checkView(type, Collections.emptyList());
    }

    private static void checkView(Class<?> type, List<String> errorMessages) {
        errorMessages = new ArrayList<>(errorMessages);
        System.out.println("Checking generated view type: " + type);
        Assertions.assertTrue(Utils.isGeneratedView(type), "The type " + type.getName() + " is not a generated view class.");
        for (Method method : type.getDeclaredMethods()) {
            boolean isError = method.getName().matches("error\\d+") && Modifier.isStatic(method.getModifiers());
            if (errorMessages.isEmpty()) {
                if (isError) {
                    try {
                        String error = (String) method.invoke(null);
                        Assertions.fail("The generated class " + type.getName() + " has some error: " + error);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (isError) {
                    try {
                        String error = (String) method.invoke(null);
                        if (!errorMessages.removeIf(error::matches)) {
                            Assertions.fail("The generated class " + type.getName() + " has some error: " + error);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        String messages = errorMessages.stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", "));
        Assertions.assertTrue(errorMessages.isEmpty(), "The generated class " + type.getName() + " should has some errors which match " + messages + ".");
    }

    @Test
    public void checkView() {
        checkView(AnnotationBeanView.class);
        checkView(BeanAView.class);
        checkView(BeanAViewWithInheritedConfig.class);
        checkView(BeanBView.class);
        checkView(BeanBViewWithInheritedConfig.class);
        checkView(CommentBeanView.class);
        checkView(ConverterBeanView.class);
        checkView(DynamicMethodPropertyBeanView.class);
        checkView(FieldBeanView.class);
        checkView(GenericBeanView.class);
        checkView(MetaAndViewOfBothOnBeanView.class);
        checkView(MetaAndViewOfOnDiffBean1View.class);
        checkView(MetaAndViewOfOnDiffBean2View.class);
        checkView(MetaAndViewOfOnDiffBean3View.class);
        checkView(MetaAndViewOfOnDiffBean4$NestedBeanView.class);
        checkView(NestedGenericBean$DynamicChildBeanView.class);
        checkView(NestedGenericBean$StaticChildBeanView.class);
        checkView(NestedGenericBeanView.class);
        checkView(SerializableBeanView.class);
        checkView(SimpleBeanView.class);
        checkView(SimpleBeanViewNotUnique.class);
        checkView(SimpleBeanWithDefaultGetters.class);
        checkView(SimpleBeanWithDefaultSetters.class);
        checkView(SimpleBeanWithInvalidIncludePattern.class, Collections.singletonList(
                "Invalid include pattern part:[\\s\\S]*"
        ));
        checkView(SimpleBeanWithoutGetters.class);
        checkView(SimpleBeanWithPrivateGetters.class);
        checkView(SimpleBeanWithPrivateSetters.class);
        checkView(SimpleBeanWithProtectedGetters.class);
        checkView(SimpleBeanWithProtectedSetters.class);
        checkView(SimpleBeanWithUnknownGetters.class);
        checkView(SimpleBeanWithUnknownSetters.class);
        checkView(StaticMethodPropertyBeanView.class);
        checkView(ViewOfDirectOnBeanView.class);
        checkView(ViewOfInNestBean$Bean1$Bean2$Bean3View.class);
        checkView(ViewOfInNestBean$Bean1View.class);
        checkView(ViewOfInNestBean$Bean2$Bean1$Bean3View.class);
        checkView(ViewOfInNestBean$Bean2$Bean1View.class);
        checkView(ViewPropertyBeanWithoutParent.class);
        checkView(ViewPropertyContainerBeanView.class);
    }
}
