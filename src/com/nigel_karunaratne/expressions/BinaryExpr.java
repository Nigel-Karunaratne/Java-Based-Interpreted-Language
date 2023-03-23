package com.nigel_karunaratne.expressions;

import com.nigel_karunaratne.tokens.Token;

public class BinaryExpr extends Expr {

    public BinaryExpr(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;

    @Override
    <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitBinaryExpr(this);
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.value.toString() + " " + right.toString();
    }
    
}
