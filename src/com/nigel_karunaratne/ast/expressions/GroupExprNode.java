package com.nigel_karunaratne.ast.expressions;

public class GroupExprNode extends BaseExprNode {
    
    public final BaseExprNode expression;

    public GroupExprNode(BaseExprNode expression) {
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
