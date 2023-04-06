package com.nigel_karunaratne.ast.expressions;

import com.nigel_karunaratne.tokens.Token;

public class VarAssignmentExprNode extends ExprNode {
    public final Token name;
    public final ExprNode value;

    public VarAssignmentExprNode(Token name, ExprNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitVarAssignExpr(this);
    }
}