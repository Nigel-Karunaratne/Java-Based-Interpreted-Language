package com.nigel_karunaratne.expressions;

import com.nigel_karunaratne.tokens.Token;

public class UnaryExpr extends Expr{

    public UnaryExpr(Token operator, Expr expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public final Token operator;
    public final Expr expression;

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }
    
    @Override
    public String toString() {
        return operator.toString() + expression.toString();
    }
}
