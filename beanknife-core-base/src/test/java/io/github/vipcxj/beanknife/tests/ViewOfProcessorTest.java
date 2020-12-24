package io.github.vipcxj.beanknife.tests;

import io.github.vipcxj.beanknife.core.GeneratedMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewOfProcessor;
import org.junit.Test;

import java.util.Arrays;

import static io.github.vipcxj.beanknife.tests.Utils.testViewCase;

public class ViewOfProcessorTest {

    @Test
    public void testBeanWithOnlyFields() {
        Utils.testViewCase(
                Arrays.asList(new ViewOfProcessor(), new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                "io.github.vipcxj.beanknife.cases.beans.FieldBeanViewConfig",
                new String[] {"io.github.vipcxj.beanknife.cases.beans.FieldBeanView"},
                "View"
        );
    }

    @Test
    public void testNestGenericBean() {
        testViewCase(
                Arrays.asList(new ViewOfProcessor(), new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean",
                new String[] {
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBeanView",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$StaticChildBeanMeta",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$StaticChildBeanView",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$DynamicChildBeanMeta",
                        "io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$DynamicChildBeanView"
                },
                "View"
        );
    }
}
