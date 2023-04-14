package com.nigel_karunaratne.ast.statements;

public abstract class StmtNode {
    public abstract <T> T accept(StmtNodeVisitor<T> visitor);
}
