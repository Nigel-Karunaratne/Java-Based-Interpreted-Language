package com.nigel_karunaratne.ast.statements;

import com.nigel_karunaratne.ast.expressions.ExprNode;

public class WhileStmtNode extends StmtNode {
    public final ExprNode conditionExpr;
    public final StmtNode bodyStmt;

    public WhileStmtNode(ExprNode conditionExpr, StmtNode bodyStmt) {
        this.conditionExpr = conditionExpr;
        this.bodyStmt = bodyStmt;
    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitWhileStmt(this);
    }
}
