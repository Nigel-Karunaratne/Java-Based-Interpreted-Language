package com.nigel_karunaratne.ast.statements;

import com.nigel_karunaratne.ast.expressions.ExprNode;

public class IfStmtNode extends StmtNode {

    public final ExprNode conditionExpr;
    public final StmtNode thenStmt;
    public final StmtNode elseStmt;

    public IfStmtNode(ExprNode conditionExpr, StmtNode thenStmt, StmtNode elseStmt) {
        this.conditionExpr = conditionExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitIfStmt(this);
    }
    
}
