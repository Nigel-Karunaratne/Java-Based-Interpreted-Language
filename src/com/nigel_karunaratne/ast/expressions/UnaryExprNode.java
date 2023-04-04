package com.nigel_karunaratne.ast.expressions;

import com.nigel_karunaratne.tokens.Token;

public class UnaryExprNode extends BaseExprNode{

    public UnaryExprNode(Token operator, BaseExprNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public final Token operator;
    public final BaseExprNode expression;

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }
    
    @Override
    public String toString() {
        return operator.toString() + expression.toString();
    }
}
