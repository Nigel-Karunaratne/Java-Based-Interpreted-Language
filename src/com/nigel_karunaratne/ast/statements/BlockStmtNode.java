package com.nigel_karunaratne.ast.statements;

import java.util.List;

public class BlockStmtNode extends StmtNode {

    public final List<StmtNode> contents;

    public BlockStmtNode(List<StmtNode> contents) {
        this.contents = contents;
    }

    @Override
    public <T> T accept(StmtNodeVisitor<T> visitor) {
        return visitor.visitBlockStmt(this);
    }
    
}
