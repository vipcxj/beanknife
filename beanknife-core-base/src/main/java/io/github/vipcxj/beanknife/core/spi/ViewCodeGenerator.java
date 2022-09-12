package io.github.vipcxj.beanknife.core.spi;

import io.github.vipcxj.beanknife.core.models.ViewContext;

import javax.lang.model.element.Element;
import javax.tools.JavaFileManager;
import java.io.PrintWriter;

/**
 * Code generator plugin. Work with ViewOf.
 * Support generate custom content in the generated class.
 * Also support generate custom file outside of the generated class.
 */
public interface ViewCodeGenerator extends CodeGenerator<ViewContext> {
}
