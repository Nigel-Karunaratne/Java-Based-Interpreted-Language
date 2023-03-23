package com.nigel_karunaratne.ast;

import com.nigel_karunaratne.tokens.Token;

public class BinaryOperator extends ASTNode {
    public Token leftToken;
    public Token operatorToken;
    public Token rightToken;

    public BinaryOperator(Token left, Token operator, Token right) {
        leftToken = left;
        operatorToken = operator;
        rightToken = right;
    }
}
