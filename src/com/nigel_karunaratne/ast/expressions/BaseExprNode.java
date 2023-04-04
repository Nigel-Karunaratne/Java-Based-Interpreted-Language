package com.nigel_karunaratne.ast.expressions;

public abstract class BaseExprNode {
    public abstract <T> T accept(ExprNodeVisitor<T> visitor);
}
