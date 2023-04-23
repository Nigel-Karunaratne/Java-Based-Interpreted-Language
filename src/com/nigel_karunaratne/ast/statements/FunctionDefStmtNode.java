package com.nigel_karunaratne.ast.statements;

import java.util.List;

import com.nigel_karunaratne.tokens.Token;

public class FunctionDefStmtNode extends StmtNode {

    public final Token name;
    public final List<Token> parameters;
    public final List<StmtNode> functionBody;

    public FunctionDefStmtNode(Token name, List<Token> parameters, List<StmtNode> functionBody) {
        this.name = name;
        this.parameters = parameters;
        this.functionBody = functionBody;
    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitFunctionDefStmt(this);
    }
    
}
