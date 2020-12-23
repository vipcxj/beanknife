package io.github.vipcxj.beanknife.tests;

import io.github.vipcxj.beanknife.core.GeneratedMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewOfProcessor;
import org.junit.Test;

import java.util.Arrays;

import static io.github.vipcxj.beanknife.tests.Utils.testViewCase;

public class ViewMetaProcessorTest {

    @Test
    public void testBean1() {
        testViewCase(
                Arrays.asList(new ViewMetaProcessor(), new GeneratedMetaProcessor()),
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
                Arrays.asList(new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                "io.github.vipcxj.beanknife.tests.beans.TestBean1Child",
                null,
                "Meta"
        );
        testViewCase(
                Arrays.asList(new ViewOfProcessor(), new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                "io.github.vipcxj.beanknife.tests.beans.GenericTestBean1",
                null,
                "View"
        );
        testViewCase(
                Arrays.asList(new ViewOfProcessor(), new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                "io.github.vipcxj.beanknife.tests.beans.GenericTestBean2",
                null,
                "View"
        );
        testViewCase(
                Arrays.asList(new ViewOfProcessor(), new ViewMetaProcessor(), new GeneratedMetaProcessor()),
                "io.github.vipcxj.beanknife.tests.beans.TestBean3ViewConfig",
                new String[] { "io.github.vipcxj.beanknife.tests.beans.TestBean3View" },
                "View"
        );
    }
}
