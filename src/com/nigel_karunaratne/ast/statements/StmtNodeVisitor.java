package com.nigel_karunaratne.ast.statements;

public interface StmtNodeVisitor<T> {
    T visitExprStmt(ExprStmtNode stmt);
    T visitVarDeclStmt(VarDeclarationStmtNode stmt);
    T visitBlockStmt(BlockStmtNode stmt);
    T visitIfStmt(IfStmtNode stmt);
    T visitWhileStmt(WhileStmtNode stmt);
    T visitFunctionDefStmt(FunctionDefStmtNode stmt);
    T visitReturnStmt(ReturnStmtNode stmt);
}
