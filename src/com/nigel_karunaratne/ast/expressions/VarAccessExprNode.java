package com.nigel_karunaratne.ast.expressions;

import com.nigel_karunaratne.tokens.Token;

public class VarAccessExprNode extends ExprNode {
    public final Token identifier;

    public VarAccessExprNode(Token identifier) {
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitVarAccessExpr(this);
    }
}
