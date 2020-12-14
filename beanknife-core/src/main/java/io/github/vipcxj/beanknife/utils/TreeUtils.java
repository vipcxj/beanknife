package io.github.vipcxj.beanknife.utils;

import com.sun.source.tree.*;

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
}
