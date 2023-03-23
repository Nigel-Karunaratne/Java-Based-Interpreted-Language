package com.nigel_karunaratne.expressions;

public class GroupExpr extends Expr {
    final Expr expression;

    public GroupExpr(Expr expression) {
        this.expression = expression;
    }

    @Override
    <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitGroupExpr(this);
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")";
    }
}
