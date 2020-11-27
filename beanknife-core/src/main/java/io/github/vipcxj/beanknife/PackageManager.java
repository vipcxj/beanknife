package io.github.vipcxj.beanknife;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageManager {
    private final String packageName;
    private final List<String> imports;
    private final Set<String> symbols;

    public PackageManager(String packageName) {
        this.packageName = packageName;
        this.imports = new ArrayList<>();
        this.symbols = new HashSet<>();
    }

    public boolean importVariable(ClassName name) {
        if (symbols.contains(name.getImportSimpleName())) {
            return imports.contains(name.getImportName());
        }
        imports.add(name.getImportName());
        symbols.add(name.getImportSimpleName());
        return true;
    }

    private void println(Printer printer, PrintLiner printLiner) {
        if (printLiner == null) {
            printer.print("\n");
        } else {
            printLiner.println();
        }
    }

    public void print(@Nonnull Printer printer, @Nullable PrintLiner printLiner) {
        if (!packageName.isEmpty()) {
            printer.print("package ");
            printer.print(packageName);
            printer.print(";");
            println(printer, printLiner);
            println(printer, printLiner);
        }
        for (String anImport : imports) {
            if ((!anImport.startsWith(packageName)
                    || (anImport.length() > packageName.length() + 1 && anImport.substring(packageName.length() + 1).indexOf('.') != -1))
                    && !anImport.startsWith("java.lang")
            ) {
                printer.print("import ");
                printer.print(anImport);
                printer.print(";");
                println(printer, printLiner);
            }
        }
    }

    interface Printer {
        void print(String msg);
    }

    interface PrintLiner {
        void println();
    }
}
