package io.github.vipcxj.beanknife.core.spi;

import io.github.vipcxj.beanknife.core.models.MetaContext;

/**
 * Code generator plugin. Work with ViewMeta.
 * Support generate custom content in the meta class.
 * Also support generate custom file outside of the meta class.
 */
public interface MetaCodeGenerator extends CodeGenerator<MetaContext> {
}
