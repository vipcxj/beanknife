package io.github.vipcxj.beanknife.tests;

import io.github.vipcxj.beanknife.ViewMetaProcessor;
import io.github.vipcxj.beanknife.ViewOfProcessor;
import org.junit.Test;

import static io.github.vipcxj.beanknife.tests.Utils.testViewCase;

public class ViewMetaProcessorTest {

    @Test
    public void testBean1() {
        testViewCase(
                new ViewMetaProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.TestBean1",
                new String[] {
                        null,
                        "io.github.vipcxj.beanknife.tests.beans.ViewOfTestBean1",
                        "io.github.vipcxj.beanknife.tests.otherbeans.TestBean1Meta",
                        "io.github.vipcxj.beanknife.tests.otherbeans.ViewOfTestBean1",
                        "io.github.vipcxj.beanknife.tests.beans.TestBean1$NestTestBean1Meta"
                },
                "Meta"
        );
        testViewCase(
                new ViewMetaProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.TestBean1Child",
                null,
                "Meta"
        );
        testViewCase(
                new ViewOfProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.GenericTestBean1",
                null,
                "View"
        );
        testViewCase(
                new ViewOfProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.GenericTestBean2",
                null,
                "View"
        );
        testViewCase(
                new ViewOfProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.TestBean3ViewConfig",
                new String[] { "io.github.vipcxj.beanknife.tests.beans.TestBean3View" },
                "View"
        );
    }
}
