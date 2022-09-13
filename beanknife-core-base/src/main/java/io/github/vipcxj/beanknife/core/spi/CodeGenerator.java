package io.github.vipcxj.beanknife.core.spi;

import io.github.vipcxj.beanknife.core.models.Context;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public interface CodeGenerator<C extends Context> {

    /**
     * Called by Context at its initialization stage.
     * @param context context of BeanKnife
     */
    void ready(C context);

    /**
     * Write content to the target.
     * @param writer target writer
     * @param context context of BeanKnife
     * @param indent indent, should be numbers of space.
     * @param indentNum the num of indent currently.
     */
    void print(PrintWriter writer, C context, String indent, int indentNum);

    /**
     * true if generate file outside of meta class.
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

    default JavaFileManager.Location[] location() {
        return null;
    }

    static <C extends Context> void print(CodeGenerator<C> generator, PrintWriter writer, C context, String indent) {
        if (!generator.standalone()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            generator.print(pw, context, indent, 1);
            pw.flush();
            String generated = sw.toString();
            if (!generated.isEmpty()) {
                writer.print(generated);
            }
        }
    }

    static <C extends Context> void printOutside(CodeGenerator<C> generator, C context, Filer filer, Element[] dependencies) throws IOException {
        String moduleAndPkg = generator.moduleAndPkg();
        moduleAndPkg = moduleAndPkg != null ? moduleAndPkg : "";
        String relativeName = generator.relativeName();
        if (generator.standalone() && generator.fileType() != null && relativeName != null && !relativeName.isEmpty()) {
            String name = !moduleAndPkg.isEmpty() ? moduleAndPkg + "." + relativeName : relativeName;
            FileObject[] fileObjects = null;
            switch (generator.fileType()) {
                case SOURCE:
                    fileObjects = new FileObject[] { filer.createSourceFile(name, dependencies) };
                    break;
                case CLASS:
                    fileObjects = new FileObject[] { filer.createClassFile(name, dependencies) };
                    break;
                case RESOURCE:
                    JavaFileManager.Location[] locations = generator.location();
                    if (locations != null && locations.length != 0) {
                        fileObjects = new FileObject[locations.length];
                        for (int i = 0; i < locations.length; ++i) {
                            JavaFileManager.Location location = locations[i];
                            fileObjects[i] = filer.createResource(location, moduleAndPkg, relativeName, dependencies);
                        }
                    }
                    break;
            }
            if (fileObjects != null) {
                for (FileObject fileObject : fileObjects) {
                    try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                        generator.print(writer, context, Context.INDENT, 0);
                    }
                }
            }
        }
    }
}
