package com.nigel_karunaratne.ast.expressions;

public interface ExprNodeVisitor<T> {
    T visitBinaryExpr(BinaryExprNode expr);
    T visitUnaryExpr(UnaryExprNode expr);
    T visitLiteralExpr(LiteralExprNode expr);
    T visitGroupExpr(GroupExprNode expr);
    T visitVarAccessExpr(VarAccessExprNode expr);
    T visitVarAssignExpr(VarAssignmentExprNode expr);
    T visitLogicalOp(LogicalOpExprNode expr);
    T visitCallFunExpr(CallFnExprNode expr);
}
