package com.nigel_karunaratne.ast.expressions;

import com.nigel_karunaratne.tokens.Token;

public class UnaryExprNode extends ExprNode{

    public UnaryExprNode(Token operator, ExprNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public final Token operator;
    public final ExprNode expression;

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }
    
    @Override
    public String toString() {
        return operator.toString() + expression.toString();
    }
}
