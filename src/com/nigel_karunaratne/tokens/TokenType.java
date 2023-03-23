package com.nigel_karunaratne.tokens;

public enum TokenType {
    NULL,
    INT,
    FLOAT,
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
    
    TRUE,
    FALSE,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,

    ENDLINE,
    EOF
}
