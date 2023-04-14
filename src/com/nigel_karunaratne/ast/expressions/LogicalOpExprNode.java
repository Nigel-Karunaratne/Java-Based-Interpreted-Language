package com.nigel_karunaratne.ast.expressions;

import com.nigel_karunaratne.tokens.Token;

public class LogicalOpExprNode extends ExprNode{
    public final ExprNode left;
    public final Token operator;
    public final ExprNode right;

    public LogicalOpExprNode(ExprNode left, Token operator, ExprNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitLogicalOp(this);
    }


}
