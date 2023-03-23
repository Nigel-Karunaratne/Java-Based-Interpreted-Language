package com.nigel_karunaratne.expressions;

public abstract class Expr {
    abstract <T> T accept(ExprVisitor<T> visitor);
}
