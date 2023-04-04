package com.nigel_karunaratne.ast.expressions;

public class GroupExprNode extends ExprNode {
    
    public final ExprNode expression;

    public GroupExprNode(ExprNode expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitGroupExpr(this);
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")";
    }
}
