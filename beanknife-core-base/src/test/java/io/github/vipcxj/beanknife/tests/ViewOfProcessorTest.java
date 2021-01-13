package io.github.vipcxj.beanknife.tests;

import io.github.vipcxj.beanknife.core.GeneratedMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewOfProcessor;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.github.vipcxj.beanknife.tests.Utils.testViewCase;

public class ViewOfProcessorTest {

    private void testViewCase(List<String> qualifiedClassNames, List<String> targetQualifiedClassNames) {
        Utils.testViewCase(
                Arrays.asList(new ViewOfProcessor(), new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                qualifiedClassNames,
                targetQualifiedClassNames
        );
    }

    private void testViewCase(String qualifiedClassName, List<String> targetQualifiedClassNames) {
        testViewCase(
                Collections.singletonList(qualifiedClassName),
                targetQualifiedClassNames
        );
    }

    private void testViewCase(String qualifiedClassName, String targetQualifiedClassNames) {
        testViewCase(
                Collections.singletonList(qualifiedClassName),
                Collections.singletonList(targetQualifiedClassNames)
        );
    }

    @Test
    public void testBeanWithOnlyFields() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.FieldBeanViewConfig",
                "io.github.vipcxj.beanknife.cases.beans.FieldBeanView"
        );
    }

    @Test
    public void testNestGenericBean() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean",
                Arrays.asList(
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBeanView",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$StaticChildBeanMeta",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$StaticChildBeanView",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$DynamicChildBeanMeta",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$DynamicChildBeanView"
                )
        );
    }

    @Test
    public void testInheritedViewConfig() {
        testViewCase(
                Arrays.asList(
                        "io.github.vipcxj.beanknife.cases.beans.InheritedConfigBeanAViewConfig",
                        "io.github.vipcxj.beanknife.cases.beans.InheritedConfigBeanBViewConfig"
                ),
                Arrays.asList(
                        "io.github.vipcxj.beanknife.cases.beans.BeanAViewWithInheritedConfig",
                        "io.github.vipcxj.beanknife.cases.beans.BeanBViewWithInheritedConfig"
                )
        );
    }

    @Test
    public void testStaticPropertyMethod() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.StaticMethodPropertyBeanViewConfig",
                "io.github.vipcxj.beanknife.cases.beans.StaticMethodPropertyBeanView"
        );
    }

    @Test
    public void testDynamicPropertyMethod() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.DynamicMethodPropertyBeanViewConfig",
                "io.github.vipcxj.beanknife.cases.beans.DynamicMethodPropertyBeanView"
        );
    }

    @Test
    public void testUseAnnotation() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.AnnotationBeanViewConfigure",
                "io.github.vipcxj.beanknife.cases.beans.AnnotationBeanView"
        );
    }

    @Test
    public void testWriteableBean() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.WriteableBeanViewConfigure",
                "io.github.vipcxj.beanknife.cases.beans.WriteableBeanView"
        );
    }

    @Test
    public void testConfigurationInheritance() {
        testViewCase(
                "io.github.vipcxj.beanknife.cases.beans.Leaf11BeanViewConfigure",
                "io.github.vipcxj.beanknife.cases.beans.ViewOfLeaf11Bean"
        );
    }
}
