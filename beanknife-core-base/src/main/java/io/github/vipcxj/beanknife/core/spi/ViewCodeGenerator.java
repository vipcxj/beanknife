package io.github.vipcxj.beanknife.core.spi;

import io.github.vipcxj.beanknife.core.models.ViewContext;

import javax.lang.model.element.Element;
import javax.tools.JavaFileManager;
import java.io.PrintWriter;

/**
 * Code generator plugin.
 * Support generate custom content in the generated class.
 * Also support generate custom file outside of the generated class.
 */
public interface ViewCodeGenerator {

    /**
     * Called by ViewContext at its initialization stage.
     * @param context context of BeanKnife
     */
    void ready(ViewContext context);

    /**
     * Write content to the target.
     * @param writer target writer
     * @param context context of BeanKnife
     * @param indent indent, should be numbers of space.
     * @param indentNum the num of indent currently.
     */
    void print(PrintWriter writer, ViewContext context, String indent, int indentNum);

    /**
     * true if generate file outside of generated class.
     * To make this work, {@link #fileType()}, {@link #location()}, {@link #moduleAndPkg()} and {@link #relativeName()} should be correctly implemented.
     * For {@code FileType.SOURCE} and {@code FileType.CLASS}, {@link #moduleAndPkg()} and {@link #relativeName()} should be provided.
     * For {@code FileType.RESOURCE}, {@link #location()} also should be provided.
     * @return standalone or not
     * @see javax.annotation.processing.Filer#createClassFile(CharSequence, Element...)
     * @see javax.annotation.processing.Filer#createSourceFile(CharSequence, Element...)
     * @see javax.annotation.processing.Filer#createResource(JavaFileManager.Location, CharSequence, CharSequence, Element...)
     */
    default boolean standalone() {
        return false;
    }

    default FileType fileType() {
        return null;
    }

    default String moduleAndPkg() {
        return null;
    }

    default String relativeName() {
        return null;
    }

    default JavaFileManager.Location location() {
        return null;
    }

    enum FileType {
        SOURCE, CLASS, RESOURCE
    }
}
