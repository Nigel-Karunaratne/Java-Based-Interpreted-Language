package com.nigel_karunaratne.tokens;

public enum TokenType {
    NULL,
    INT,
    DOUBLE,
    STRING,

    NOT,
    GREATER_THAN,
    LESS_THAN,
    ASSIGN_VALUE,

    ADD,
    MINUS,
    MUL,
    DIV,
    MOD,
    POWER,

    EQUALS,
    GREATER_THAN_EQUAL,
    LESS_THAN_EQUAL,
    NOT_EQUAL,
    OR,
    AND,

    VAR_DEC,
    IF,
    ELSE,
    WHILE,
    RETURN,
    FUNC,

    IDENTIFIER,
    
    TRUE,
    FALSE,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,

    COMMA,

    ENDLINE,
    EOF
}
