package io.github.vipcxj.beanknife.core.utils;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.models.Context;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class TreeUtils {

    public static String parseMemberSelect(MemberSelectTree tree) {
        ExpressionTree expression = tree.getExpression();
        if (expression.getKind() == Tree.Kind.MEMBER_SELECT) {
            return parseMemberSelect((MemberSelectTree) expression) + "." + tree.getIdentifier().toString();
        } else if (expression.getKind() == Tree.Kind.IDENTIFIER) {
            IdentifierTree identifierTree = (IdentifierTree) expression;
            return identifierTree.getName().toString() + "." + tree.getIdentifier().toString();
        } else {
            throw new UnsupportedOperationException("Unsupported tree kind: " + tree.getKind() + ".");
        }
    }

    public static String parseImport(ImportTree importTree) {
        Tree qualifiedIdentifier = importTree.getQualifiedIdentifier();
        if (qualifiedIdentifier.getKind() == Tree.Kind.MEMBER_SELECT) {
            return parseMemberSelect((MemberSelectTree) qualifiedIdentifier);
        } else {
            throw new UnsupportedOperationException("Unsupported tree kind: " + qualifiedIdentifier.getKind() + ".");
        }
    }

    public static String parsePackageName(CompilationUnitTree unit) {
        ExpressionTree packageName = unit.getPackageName();
        if (packageName == null) {
            return "";
        }
        if (packageName.getKind() == Tree.Kind.IDENTIFIER) {
            IdentifierTree identifierTree = (IdentifierTree) packageName;
            return identifierTree.getName().toString();
        } else if (packageName.getKind() == Tree.Kind.MEMBER_SELECT) {
            return parseMemberSelect((MemberSelectTree) packageName);
        } else {
            throw new IllegalArgumentException("This is impossible!");
        }
    }

    @CheckForNull
    public static CompilationUnitTree getCompilationUnit(@NonNull Context context, @NonNull Element element) {
        TreePath path = context.getTrees().getPath(element);
        return path != null ? path.getCompilationUnit() : null;
    }

    public static TypeMirror tryGetTypeMirror(@NonNull Context context, @NonNull Element element, @NonNull Tree tree) {
        TreePath path = context.getTrees().getPath(element);
        CompilationUnitTree unit = path.getCompilationUnit();
        TreePath treePath = context.getTrees().getPath(unit, tree);
        return context.getTrees().getTypeMirror(treePath);
    }
}
