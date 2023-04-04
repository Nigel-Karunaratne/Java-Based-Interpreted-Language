package com.nigel_karunaratne.ast.expressions;

public class LiteralExprNode extends BaseExprNode {

    public final Object value;

    public LiteralExprNode(Object value) {
        this.value = value;
    }

    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }   
}
