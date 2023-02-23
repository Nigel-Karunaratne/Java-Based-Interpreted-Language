package com.nigel_karunaratne.tokens;

public class Token {
    public TokenType type;
    public Object value;

    public int line;
    public int column;

    public Token(TokenType type, Object value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return this.type.toString() + " : " + this.value;
    }
}
