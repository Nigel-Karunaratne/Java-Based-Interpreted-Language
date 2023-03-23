package com.nigel_karunaratne.expressions;

public class GroupExpr extends Expr {
    
    public final Expr expression;

    public GroupExpr(Expr expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitGroupExpr(this);
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")";
    }
}
