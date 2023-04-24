package com.nigel_karunaratne.lexer;

import java.util.ArrayList;
import java.util.HashMap;

import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.tokens.Token;
import com.nigel_karunaratne.tokens.TokenType;

public class Lexer {

    //Could refactor into sorted arrays and use Arrays.binarySearch?
    // public final String[] reserved = {"var", "if", "else", "while", "return", "func", "true", "false"};
    // public final char[] singleOperators = {'!', '<', '>', '=', '+', '-', '/', '*', '%'};
    // public final String[] doubleOperators = {"==", "<=", ">=", "!=", "||", "&&"};

    // HashMap<String, TokenType> mws = Map.ofEntries();

    final static HashMap<String,TokenType> reservedMap = new HashMap<>();
    static {
        reservedMap.put("var", TokenType.VAR_DEC);
        reservedMap.put("if", TokenType.IF);
        reservedMap.put("else", TokenType.ELSE);
        reservedMap.put("while", TokenType.WHILE);
        reservedMap.put("return", TokenType.RETURN);
        reservedMap.put("func", TokenType.FUNC);
        reservedMap.put("true", TokenType.TRUE);
        reservedMap.put("false", TokenType.FALSE);
        reservedMap.put("null", TokenType.NULL);
    }

    final static HashMap<String, TokenType> singleOperatorMap = new HashMap<>();
    static {
        singleOperatorMap.put("!", TokenType.NOT);
        singleOperatorMap.put("<", TokenType.LESS_THAN);
        singleOperatorMap.put(">", TokenType.GREATER_THAN);
        singleOperatorMap.put("=", TokenType.ASSIGN_VALUE);
        singleOperatorMap.put("+", TokenType.ADD);
        singleOperatorMap.put("-", TokenType.MINUS);
        singleOperatorMap.put("/", TokenType.DIV);
        singleOperatorMap.put("*", TokenType.MUL);
        singleOperatorMap.put("%", TokenType.MOD);
    }

    
    final static HashMap<String,TokenType> doubleOperatorMap = new HashMap<>();
    static {
        doubleOperatorMap.put("==", TokenType.EQUALS);
        doubleOperatorMap.put("<=", TokenType.LESS_THAN_EQUAL);
        doubleOperatorMap.put(">=", TokenType.GREATER_THAN_EQUAL);
        doubleOperatorMap.put("!=", TokenType.NOT_EQUAL);
        doubleOperatorMap.put("||", TokenType.OR);
        doubleOperatorMap.put("&&", TokenType.AND);
    }

    private ArrayList<Token> tokens = new ArrayList<>();

    int line = 0;
    int column = 0;
    int currentInputIndex = 0;
    char[] input;
    char current;

    public ArrayList<Token> getGeneratedTokens() {return tokens;}

    public void LexInput(String inputText) {

        line = 0;
        column = 0;
        input = inputText.toCharArray();
        currentInputIndex = 0;
        tokens = new ArrayList<>(); //REVIEW - Estimate default size of list using size of input? Could reduce the amount of time spent resizing internal array?

        while(currentInputIndex < input.length) {
            current = getCurrent();
            
            if(current == '\n') {
                newLine();
                currentInputIndex++;
            }
            
            /* Looking for:
            * letter -> keyword or variable
            * number -> integer or float
            * quote -> string
            * lparen -> section OR function call
            * rparen ->
            * lbrace -> new block (block int ++)
            * rbrace -> block int --
            * semicolon -> statement end
            * 
            * default -> either whitespace or operator
            */

            //Whitespace check
            else if(Character.isWhitespace(current)) {
                //NOTE - FOUND WHITESPACE (do nothing)
                setCurrentToNext();
            }

            else if(Character.isLetter(current)) {
                //* variable or keyword
                handleVarOrKeyword();
            }

            else if(Character.isDigit(current)) {
                handleIntegerOrFloat();
            }

            else if(current == '"') {
                handleString();
            }

            else if(current == '(') {
                tokens.add(new Token(TokenType.LPAREN, '(', line, column));
                    //TODO - Cleanup System.out.print("(|");
                setCurrentToNextIfExists();
            }

            else if(current == ')') {
                tokens.add(new Token(TokenType.RPAREN, ')', line, column));
                    // System.out.print(")|");
                setCurrentToNextIfExists();
            }

            else if(current == '{') {
                tokens.add(new Token(TokenType.LBRACE, '{', line, column));
                    // System.out.print("{|");
                setCurrentToNextIfExists();
            }

            else if(current == '}') {
                tokens.add(new Token(TokenType.RBRACE, '}', line, column));
                    // System.out.print("}|");
                setCurrentToNextIfExists();
            }

            else if(current == ';') {
                handleSemicolon();
            }

            else if (current == ',') {
                tokens.add(new Token(TokenType.COMMA, ',', line, column));
                setCurrentToNextIfExists();  
            }

            else {
                handleOperator();
            }
        }
        //END OF WHILE
        tokens.add(new Token(TokenType.EOF, null, line+1, column)); //TODO - check
        // ErrorHandler.debugOutput("Lexer has finished.");
    }
    
    private char getCurrent() {
        return input[currentInputIndex];
    }
    
    private void setCurrentToNext() {
        //TODO - Should throw error if next does not exist
        currentInputIndex++;
        line++;
        current = input[currentInputIndex];
    }

    private void setCurrentToNextIfExists() {
        currentInputIndex++;
        line++;
        if(currentInputIndex < input.length)
            current = input[currentInputIndex];
    }
    
    private char peekNext() {
        return input[currentInputIndex + 1];
    }

    private boolean peekNextExists() {
        return currentInputIndex + 1 < input.length; 
    }
    
    private void newLine() {
        line++;
        column=0;
    }
    
    private void handleVarOrKeyword() {
        StringBuilder builder = new StringBuilder();
        builder.append(current);
        while(peekNextExists() && (Character.isLetter(peekNext()) || Character.isDigit(peekNext()))) {
            setCurrentToNext();
            // returnVal += current;
            builder.append(current);
        }

        setCurrentToNextIfExists();
        String returnVal = builder.toString();
        
        //Check if returnVal is in reserved
        // for (String string : reserved) {
        //     if (returnVal.equals(string)) {
        //         //NOTE - FOUND KEYWORD ->  need to check if it is a bool value
        //             System.out.print(returnVal + "|");

        //         return;
        //     }
        // }
        if(reservedMap.containsKey(returnVal)) {
            //NOTE - FOUND KEYWORD
                // System.out.print(returnVal + "|");
            tokens.add(new Token(reservedMap.get(returnVal), returnVal, line, column));
            return;
        }

        //NOTE - FOUND VARIABLE (add to symbol table?)
            // System.out.print(returnVal + "|");
        tokens.add(new Token(TokenType.IDENTIFIER, returnVal, line, column));
    }
    
    private void handleIntegerOrFloat() {
        StringBuilder builder = new StringBuilder();
        builder.append(current);
        boolean isFloat = false;

        // while(peekNextExists() && Character.isDigit(current)) {
        //     returnVal += current;
        //     setCurrentToNext();
        //     if(current == '.')
        //         isFloat = true;
        // } //OLD METHOD

        while(peekNextExists() && (Character.isDigit(peekNext()) || peekNext() == '.')) {
            setCurrentToNext();
            builder.append(current);
            if(current == '.')
            {
                if(!isFloat)
                    isFloat = true;
                else {
                    ErrorHandler.reportLexerError(line, column, "Numbers cannot have multiple decimal points.");
                    endLexerEarly();
                    return;
                }
            }
        }
        //By now, current is the char after the number
        String returnVal = builder.toString();
        if(isFloat) {
            //NOTE - FOUND FLOAT
                // System.out.print(returnVal + "|");
            tokens.add(new Token(TokenType.DOUBLE, Double.parseDouble(returnVal), line, column));
                
        }
        else {
            //NOTE - FOUND INTEGER
                // System.out.print(returnVal + "|");
            tokens.add(new Token(TokenType.INT, Integer.parseInt(returnVal), line, column));
        }
        setCurrentToNextIfExists();
    }

    private void handleString() {
        StringBuilder builder = new StringBuilder();
        // String returnVal = "";
        setCurrentToNext();

        while(current != '"') {
            builder.append(current);
            if(!peekNextExists()) {
                ErrorHandler.reportLexerError(line, column, "Expected \", but end of file reached.");
                endLexerEarly();
                return;
            }
            setCurrentToNext();
        }

        //The resulting string should not contain any quotation marks

        String returnVal = builder.toString();
        //NOTE - FOUND STRING
            // System.out.print(returnVal + "|");
        tokens.add(new Token(TokenType.STRING, returnVal, line, column));

            setCurrentToNextIfExists();
    }

    private void handleSemicolon() {
        //NOTE - FOUND SEMICOLON
            // System.out.print(";" + "|");
        tokens.add(new Token(TokenType.ENDLINE, ';', line, column));
        setCurrentToNextIfExists();
    }

    private void handleOperator() {
        if(peekNextExists() && !Character.isWhitespace(peekNext())) {
            //could be double operator, need to check first
            String rval = "" + current + peekNext();
            // for (String string : doubleOperators) {
            //     if (rval.equals(string)) {
            //         //NOTE - DOUBLE OPERATOR FOUND
            //             System.out.print(rval + "|");
                    
            //         //Twice due to a double operator
            //         currentInputIndex+=1;
            //         setCurrentToNextIfExists();
            //         return;
            //     }
            // }

            if(doubleOperatorMap.containsKey(rval)) {
                //NOTE - DOUBLE OPERATOR FOUND
                    // System.out.print(rval + "|");
                tokens.add(new Token(doubleOperatorMap.get(rval), rval, line, column));
                    
                //Twice due to a double operator
                currentInputIndex+=1;
                setCurrentToNextIfExists();
                return;
            }
        }
        //Not double operator, could be single

        // for (char character : singleOperators) {
        //     if (current == character) {
        //         //NOTE - SINGLE OPERATOR FOUND
        //             System.out.print("" + current + "|");

        //         setCurrentToNextIfExists();
        //         return;
        //     }
        // }

        if(singleOperatorMap.containsKey("" + current)) {
            //NOTE - SINGLE OPERATOR FOUND
                // System.out.print("" + current + "|");
            tokens.add(new Token(singleOperatorMap.get("" + current), "" + current, line, column));
                
            setCurrentToNextIfExists();
            return;
        }


        //If execution reaches here, we have encountered an invalid character. Throw Error
        ErrorHandler.reportLexerError(line, column, "There's an invalid character.");
        endLexerEarly();
        return;
    }

    //Ends the lexer by setting currentInputIndex to be larger than the length of the input, terminating the while loop.
    private void endLexerEarly() {
        currentInputIndex = input.length + 1;
    }
}
