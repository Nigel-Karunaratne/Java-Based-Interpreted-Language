package com.nigel_karunaratne.ast.statements;

import com.nigel_karunaratne.ast.expressions.ExprNode;

public class ExprStmtNode extends StmtNode {
    public final ExprNode expr;
    
    public ExprStmtNode(ExprNode expr) {
        this.expr = expr;
    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitExprStmt(this);
    }
}
