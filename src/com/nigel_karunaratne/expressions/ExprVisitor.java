package com.nigel_karunaratne.expressions;

interface ExprVisitor<T> {
    T visitBinaryExpr(BinaryExpr expr);
    T visitUnaryExpr(UnaryExpr expr);
    T visitLiteralExpr(LiteralExpr expr);
    T visitGroupExpr(GroupExpr expr);
}
