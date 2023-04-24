package com.nigel_karunaratne.parser;

import java.util.ArrayList;
import java.util.List;

import com.nigel_karunaratne.tokens.Token;
import com.nigel_karunaratne.tokens.TokenType;
import com.nigel_karunaratne.ast.expressions.*;
import com.nigel_karunaratne.ast.statements.*;
import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.error_handler.ParsingError;

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


    public ArrayList<StmtNode> parse() {
        ArrayList<StmtNode> stmts = new ArrayList<>();

        while(!isAtEndOfFile()) {
            stmts.add(declaration());
        }

        return stmts;
    }

    //* declaration :	fnDecl | variableDecl | statement
    private StmtNode declaration() {
        try {
            if(matchTokenType(TokenType.VAR_DEC))
                return variableDecl();
            if(matchTokenType(TokenType.FUNC))
                return functionDecl();
            else
                return statement();
        } catch (ParsingError e) {
            synchronizeState();
            return null;
        }
    }

    private StmtNode functionDecl() {
        Token name = consumeCurrentToken(TokenType.IDENTIFIER, "Expected function name here.");
        consumeCurrentToken(TokenType.LPAREN, "Expected '(' after function name declaration.");

        List<Token> parameters = new ArrayList<>();
        if(!checkTokenType(TokenType.RPAREN)) {
            do {
                if(parameters.size() >= 16)
                    ErrorHandler.outputWarning("Cannot have more than 16 arguments in a function call.", getCurrentToken().line, getCurrentToken().column);

                parameters.add(consumeCurrentToken(TokenType.IDENTIFIER, "Expected name of parameter."));
            } while (matchTokenType(TokenType.COMMA));
        }

        consumeCurrentToken(TokenType.RPAREN, "Expected ')' after function paramters.");
        consumeCurrentToken(TokenType.LBRACE, "Expected '{' before function body (body must be a block).");
        
        List<StmtNode> body = block();

        return new FunctionDefStmtNode(name, parameters, body);
    }

    //* variableDecl :	"var" IDENTIFIER ( "=" expression )? ";"
    private StmtNode variableDecl() {
        Token variableName = consumeCurrentToken(TokenType.IDENTIFIER, "Variable needs a valid name");
        ExprNode variableInitializer = null;
        if(matchTokenType(TokenType.ASSIGN_VALUE))
            variableInitializer = expression();
        
        consumeCurrentToken(TokenType.ENDLINE, "Variable declaration must end with ';'");
        return new VarDeclarationStmtNode(variableName, variableInitializer);
    }

    //* statement :		exprStmt | blockStmt | ifStmt | whileStmt
    private StmtNode statement() {
        if(matchTokenType(TokenType.LBRACE)) {
            return new BlockStmtNode(block());
        }
        if(matchTokenType(TokenType.IF)) {
            return ifStmt();
        }
        if(matchTokenType(TokenType.WHILE)) {
            return whileStmt();
        }
        if(matchTokenType(TokenType.RETURN)) {
            return returnStmt();
        }
        return exprStatement();
    }

    private StmtNode ifStmt() {
        consumeCurrentToken(TokenType.LPAREN, "Need a '(' after 'if'");
        ExprNode condition = expression();
        consumeCurrentToken(TokenType.RPAREN, "Need a ')' in an if statement");
        StmtNode thenStmt = statement();
        StmtNode elseStmt = null;

        if(matchTokenType(TokenType.ELSE)) 
            elseStmt = statement();

        return new IfStmtNode(condition, thenStmt, elseStmt);
    }

    private StmtNode whileStmt() {
        consumeCurrentToken(TokenType.LPAREN, "Need a '(' after 'while'");
        ExprNode condition = expression();
        consumeCurrentToken(TokenType.RPAREN, "Need a ')' in a while statement");

        StmtNode bodyStmt = statement();

        return new WhileStmtNode(condition, bodyStmt);
    }

    private StmtNode returnStmt() {
        Token returnKeyword = getPreviousToken();
        ExprNode returnValue = null;
        if(!checkTokenType(TokenType.ENDLINE)) {
            returnValue = expression();
        }

        consumeCurrentToken(TokenType.ENDLINE, "Need a ';' after a return value.");

        return new ReturnStmtNode(returnKeyword, returnValue);
    }

    private List<StmtNode> block() {
        List<StmtNode> contents = new ArrayList<>();
        
        while(!checkTokenType(TokenType.RBRACE) && !isAtEndOfFile()) {
            contents.add(declaration());
        }

        consumeCurrentToken(TokenType.RBRACE, "Need a '}' to end a block");
        return contents;
    }

    //* exprStmt :		expression ";"
    private StmtNode exprStatement() {
        ExprNode expr = expression();
        consumeCurrentToken(TokenType.ENDLINE, "All statements must end with a ';'");
        return new ExprStmtNode(expr);
    }

    //* logicOr :	    logicAnd ("||" logicAnd)*
    private ExprNode logicOr() {
        ExprNode expr = logicAnd();

        while(matchTokenType(TokenType.OR)) {
            Token opToken = getPreviousToken();
            ExprNode right = logicAnd();

            expr = new LogicalOpExprNode(expr, opToken, right);
        }

        return expr;
    }

    //* logicAnd :      equality ("&&" equality)*
    private ExprNode logicAnd() {
        ExprNode expr = equality();

        while(matchTokenType(TokenType.AND)) {
            Token opToken = getPreviousToken();
            ExprNode right = equality();

            expr = new LogicalOpExprNode(expr, opToken, right);
        }

        return expr;
    }

    //* expression :	assignment
    private ExprNode expression() {
        return assignment();
    }

    //* assignment :    IDENTIFIER "=" assignment | equality
    private ExprNode assignment() {
        ExprNode expr = logicOr();
        if(matchTokenType(TokenType.ASSIGN_VALUE)) {
            Token equalsToken = getPreviousToken();
            ExprNode value = assignment();

            if (expr instanceof VarAccessExprNode) {
                Token name = ((VarAccessExprNode)expr).identifier;
                return new VarAssignmentExprNode(name, value);
            }

            //TODO - report error using ErrorHandler
            ErrorHandler.outputException(null, equalsToken.line, equalsToken.column);

        }

        return expr;
    }

    //* equality :		comparator ( ("!= | "==") comparator)*
    private ExprNode equality() {
        ExprNode activeExpression = comparator();

        while(matchTokenType(TokenType.NOT_EQUAL, TokenType.EQUALS)) { //if we can make an == or != expression then make one
            Token operator = getPreviousToken();
            ExprNode right = comparator();

            activeExpression = new BinaryExprNode(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* comparator :	term ( (">=" | "<=" | "<" | ">") term)*
    private ExprNode comparator() {
        ExprNode activeExpression = term();
        while(matchTokenType(TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUAL, TokenType.LESS_THAN, TokenType.LESS_THAN_EQUAL)) {
            Token operator = getPreviousToken();
            ExprNode right = term();

            activeExpression = new BinaryExprNode(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* term :		factor ( ("+" | "-") factor)*
    private ExprNode term() {
        ExprNode activeExpression = factor();
        while(matchTokenType(TokenType.ADD, TokenType.MINUS)) {
            Token operator = getPreviousToken();
            ExprNode right = factor();

            activeExpression = new BinaryExprNode(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* factor : 		unary ( ("/" | "*" | "%" | "**") unary)*
    private ExprNode factor() {
        ExprNode activeExpression = unary();
        while(matchTokenType(TokenType.DIV, TokenType.MUL, TokenType.MOD, TokenType.POWER)) {
            Token operator = getPreviousToken();
            ExprNode right = unary();

            activeExpression = new BinaryExprNode(activeExpression, operator, right);
        }

        return activeExpression;
    }

    //* unary :		("!" | "-") unary | primary
    private ExprNode unary() {
        if(matchTokenType(TokenType.MINUS, TokenType.NOT)) {
            Token operator = getPreviousToken();
            ExprNode right = unary();

            return new UnaryExprNode(operator, right);
        }

        return callFn();
    }

    private ExprNode callFn() {
        ExprNode expr = primary();
        while(true) {
            if(matchTokenType(TokenType.LPAREN)) {
                expr = finishBuildingCall(expr);
            } 
            else {
                break;
            }
        }

        return expr;
    }

    private ExprNode finishBuildingCall(ExprNode expr) {
        List<ExprNode> args = new ArrayList<>();
        if(!checkTokenType(TokenType.RPAREN)) {
            args.add(expression());
            while(matchTokenType(TokenType.COMMA)) {
                if(args.size() >= 16) {
                    ErrorHandler.outputWarning("Cannot have more than 16 arguments in a function call.", getCurrentToken().line, getCurrentToken().column);
                }
                args.add(expression());
            }
        }

        Token parenthesis = consumeCurrentToken(TokenType.RPAREN, "Need a ')' after arguments.");

        return new CallFnExprNode(expr, parenthesis, args);
    }

    //* primary :		INT | FLOAT | STR | "true" | "false" | "null" | "(" expression ")" | IDENTIFIER
    private ExprNode primary() {
        if(matchTokenType(TokenType.INT, TokenType.DOUBLE, TokenType.STRING))
            return new LiteralExprNode(getPreviousToken().value);
        
        if(matchTokenType(TokenType.FALSE))
            return new LiteralExprNode(false);

        if(matchTokenType(TokenType.TRUE))
            return new LiteralExprNode(true);

        if(matchTokenType(TokenType.NULL))
            return new LiteralExprNode(null);

        if(matchTokenType(TokenType.LPAREN)) {
            ExprNode expression = expression();
            consumeCurrentToken(TokenType.RPAREN, "Expected ')', found none");
            return new GroupExprNode(expression);
        }

        if(matchTokenType(TokenType.IDENTIFIER)) {
            return new VarAccessExprNode(getPreviousToken());
        }

        //Nothing found -> throw error
        // throw throwParsingError(getCurrentToken(), "Expression was expected, none was found");
        throw ErrorHandler.throwParsingError(getCurrentToken(), "Expression was expected, none was found");
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

    //If the current token is one of the specified types, return true. Do NOT move the position forward
    private boolean checkTokenType(TokenType... types) {
        for (TokenType tokenType : types) {
            if(isCurrentTypeEqualTo(tokenType)) {
                return true;
            }
        }

        return false;
    }

    //Only check for equality, does not advance forward
    private boolean isCurrentTypeEqualTo(TokenType typeToCheck) {
        if(isAtEndOfFile())
            return false;
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
        return getCurrentToken().type.equals(TokenType.EOF); //or position > size of list?
    }

    //consumes an expected token (advances position). if not expected token, throw error
    private Token consumeCurrentToken(TokenType type, String errorMessage) {
        if(isCurrentTypeEqualTo(type))
            return advancePosition();

        // throw throwParsingError(getCurrentToken(), errorMessage);
        throw ErrorHandler.throwParsingError(getCurrentToken(), errorMessage);

    }

    // private ParsingError throwParsingError(Token currentToken, String errorMessage) {
    //     ErrorHandler.outputException(errorMessage, currentToken.line, currentToken.column);
    //     JBIL_Main.hadError = true;
    //     return new ParsingError();
    // }
}
