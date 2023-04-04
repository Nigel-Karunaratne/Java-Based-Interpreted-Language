package com.nigel_karunaratne.ast.expressions;

import com.nigel_karunaratne.tokens.Token;

public class BinaryExprNode extends ExprNode {

    public BinaryExprNode(ExprNode left, Token operator, ExprNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public final ExprNode left;
    public final Token operator;
    public final ExprNode right;

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitBinaryExpr(this);
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.value.toString() + " " + right.toString();
    }
    
}
