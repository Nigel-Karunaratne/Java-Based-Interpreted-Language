package com.nigel_karunaratne.ast.statements;

import com.nigel_karunaratne.ast.expressions.ExprNode;
import com.nigel_karunaratne.tokens.Token;

public class VarDeclarationStmtNode extends StmtNode {
    public final Token name;
    public final ExprNode initialExpr;

    public VarDeclarationStmtNode(Token name, ExprNode initialExpr) {
        this.name = name;
        this.initialExpr = initialExpr;

    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitVarDeclStmt(this);
    }

    
}
