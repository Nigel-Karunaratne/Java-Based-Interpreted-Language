package com.nigel_karunaratne.expressions;

public class LiteralExpr extends Expr {

    public final Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }

    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }   
}
