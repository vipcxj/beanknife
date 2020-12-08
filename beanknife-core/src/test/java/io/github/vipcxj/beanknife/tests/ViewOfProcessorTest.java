package io.github.vipcxj.beanknife.tests;

import io.github.vipcxj.beanknife.ViewOfProcessor;
import org.junit.Test;

public class ViewOfProcessorTest {

    @Test
    public void testBeanWithOnlyFields() {
        Utils.testViewCase(
                new ViewOfProcessor(),
                "io.github.vipcxj.beanknife.cases.view.FieldBeanViewConfig",
                new String[] {"io.github.vipcxj.beanknife.cases.meta.FieldBeanView"},
                "View"
        );
    }
}
