package com.nigel_karunaratne.parser;

import java.util.ArrayList;
import com.nigel_karunaratne.tokens.Token;
import com.nigel_karunaratne.tokens.TokenType;
import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.error_handler.ParsingError;
import com.nigel_karunaratne.expressions.*;

//Takes in tokens and creates nodes / node trees.
public class Parser {
    //Used as a sentinel to break out of parsing errors. When an error is detected, this gets returned instead of ...
    // private static class pe extends RuntimeException {}

    private ArrayList<Token> tokens;
    int positionInList;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        positionInList = 0;
    }

    public Expr parse() {
        try {
            return expression();
        } catch (ParsingError error) {
            return null;
        }
    }

    //* expression :	equality
    private Expr expression() {
        return equality();
    }

    //* equality :		comparator ( ("!= | "==") comparator)*
    private Expr equality() {
        Expr activeExpression = comparator();

        while(matchTokenType(TokenType.NOT_EQUAL, TokenType.EQUALS)) { //if we can make an == or != expression then make one
            Token operator = getPreviousToken();
            Expr right = comparator();

            activeExpression = new BinaryExpr(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* comparator :	term ( (">=" | "<=" | "<" | ">") term)*
    private Expr comparator() {
        Expr activeExpression = term();
        while(matchTokenType(TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUAL, TokenType.LESS_THAN, TokenType.LESS_THAN_EQUAL)) {
            Token operator = getPreviousToken();
            Expr right = term();

            activeExpression = new BinaryExpr(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* term :		factor ( ("+" | "-") factor)*
    private Expr term() {
        Expr activeExpression = factor();
        while(matchTokenType(TokenType.ADD, TokenType.MINUS)) {
            Token operator = getPreviousToken();
            Expr right = factor();

            activeExpression = new BinaryExpr(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* factor : 		unary ( ("/" | "*" | "%") unary)*
    private Expr factor() {
        Expr activeExpression = unary();
        while(matchTokenType(TokenType.DIV, TokenType.MUL, TokenType.MOD)) {
            Token operator = getPreviousToken();
            Expr right = unary();

            activeExpression = new BinaryExpr(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* unary :		("!" | "-") unary | primary
    private Expr unary() {
        if(matchTokenType(TokenType.MINUS, TokenType.NOT)) {
            Token operator = getPreviousToken();
            Expr right = unary();

            return new UnaryExpr(operator, right);
        }

        return primary();
    }

    //* primary :		INT | FLOAT | STR | "true" | "false" | "null" | "(" expression ")"
    private Expr primary() {
        if(matchTokenType(TokenType.INT, TokenType.FLOAT, TokenType.STRING))
            return new LiteralExpr(getPreviousToken().value);
        
        if(matchTokenType(TokenType.FALSE))
            return new LiteralExpr(false);

        if(matchTokenType(TokenType.TRUE))
            return new LiteralExpr(true);

        if(matchTokenType(TokenType.NULL))
            return new LiteralExpr(null);

        if(matchTokenType(TokenType.LPAREN)) {
            Expr expression = expression();
            consumeCurrentToken(TokenType.RPAREN, "Expected ')', found none");
            return new GroupExpr(expression);
        }

        //Nothing found -> throw error
        throw throwParsingError(getCurrentToken(), "Expression was expected, none was found");
    }

    //If a parsing error occurs, use this to wait for a new statement (TODO), and then resume parsing.
    private void synchronizeState() {
        advancePosition();

        while(!isAtEndOfFile()) {
            if(getPreviousToken().type == TokenType.ENDLINE)
                return;
            
            switch(getCurrentToken().type) {
                case VAR_DEC:
                case IF:
                case WHILE:
                case FUNC:
                case RETURN:
                    return;
                default:
                    break;
            }

            advancePosition();
        }
    }


    //SECTION - Helper methods

    //If the current token is one of the specified types, return true and move the position forward
    private boolean matchTokenType(TokenType... types) {
        for (TokenType tokenType : types) {
            if(isCurrentTypeEqualTo(tokenType)) {
                advancePosition();
                return true;
            }
        }

        return false;
    }

    //Only check for equality, does not advance forward
    private boolean isCurrentTypeEqualTo(TokenType typeToCheck) {
        return getCurrentToken().type == typeToCheck;
    }

    //Return the current token
    private Token getCurrentToken() {
        return tokens.get(positionInList);
    }

    //Return the previous token
    private Token getPreviousToken() {
        return tokens.get(positionInList-1);
    }

    //Increments the position in list. Also return the previous token (ie, the current token BEFORE the increment) ie ADVANCE
    private Token advancePosition() {
        if(!isAtEndOfFile()) positionInList++;
        return getPreviousToken();

    }

    private boolean isAtEndOfFile() {
        return positionInList >= tokens.size(); //TODO - Check to see if this works
    }

    private Token consumeCurrentToken(TokenType type, String errorMessage) { //TODO - rename
        if(isCurrentTypeEqualTo(type))
            return advancePosition();

        throw throwParsingError(getCurrentToken(), errorMessage);

    }

    private ParsingError throwParsingError(Token currentToken, String errorMessage) {
        ErrorHandler.OutputException(errorMessage, currentToken.line, currentToken.column);
        return new ParsingError();
    }
}
