package com.nigel_karunaratne.expressions;

public abstract class Expr {
    public abstract <T> T accept(ExprVisitor<T> visitor);
}
