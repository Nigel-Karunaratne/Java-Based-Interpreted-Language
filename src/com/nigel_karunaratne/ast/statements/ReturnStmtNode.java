package com.nigel_karunaratne.ast.statements;

import com.nigel_karunaratne.ast.expressions.ExprNode;
import com.nigel_karunaratne.tokens.Token;

public class ReturnStmtNode extends StmtNode {
    public final Token returnKeyword;
    public final ExprNode value;

    public ReturnStmtNode(Token returnKeyword, ExprNode value) {
        this.returnKeyword = returnKeyword;
        this.value = value;
    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitReturnStmt(this);
    }
}
