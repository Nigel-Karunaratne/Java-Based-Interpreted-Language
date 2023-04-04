package com.nigel_karunaratne.ast.expressions;

public abstract class ExprNode {
    public abstract <T> T accept(ExprNodeVisitor<T> visitor);
}
