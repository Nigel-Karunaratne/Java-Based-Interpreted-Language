package com.nigel_karunaratne.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
// import java.util.HashMap;
import java.util.Map;

import com.nigel_karunaratne.tokens.Token;
import com.nigel_karunaratne.tokens.TokenType;

public class LexerInstance {

    //Could refactor into sorted arrays and use Arrays.binarySearch?
    // public final String[] reserved = {"var", "if", "else", "while", "return", "func", "true", "false"};
    // public final char[] singleOperators = {'!', '<', '>', '=', '+', '-', '/', '*', '%'};
    // public final String[] doubleOperators = {"==", "<=", ">=", "!=", "||", "&&"};

    // HashMap<String, TokenType> mws = Map.ofEntries();

    //TODO - Convert to HashMaps
    Map<String,TokenType> reservedMap = Map.ofEntries(
        Map.entry("var", TokenType.VAR_DEC),
        Map.entry("if", TokenType.IF),
        Map.entry("else", TokenType.ELSE),
        Map.entry("while", TokenType.WHILE),
        Map.entry("return", TokenType.RETURN),
        Map.entry("func", TokenType.FUNC),
        Map.entry("true", TokenType.TRUE),
        Map.entry("false", TokenType.FALSE)
    );

    Map<String,TokenType> singleOperatorMap = Collections.unmodifiableMap(Map.ofEntries(
        Map.entry("!", TokenType.NOT),
        Map.entry("<", TokenType.LESS_THAN),
        Map.entry(">", TokenType.GREATER_THAN),
        Map.entry("=", TokenType.ASSIGN_VALUE),
        Map.entry("+", TokenType.ADD),
        Map.entry("-", TokenType.SUB),
        Map.entry("/", TokenType.DIV),
        Map.entry("*", TokenType.MUL),
        Map.entry("%", TokenType.MOD)
    ));

    Map<String,TokenType> doubleOperatorMap = Collections.unmodifiableMap(Map.ofEntries(
        Map.entry("==", TokenType.EQUALS),
        Map.entry("<=", TokenType.LESS_THAN_EQUAL),
        Map.entry(">=", TokenType.GREATER_THAN_EQUAL),
        Map.entry("!=", TokenType.NOT_EQUAL),
        Map.entry("||", TokenType.OR),
        Map.entry("&&", TokenType.AND)
    ));

    public ArrayList<Token> tokens = new ArrayList<>();

    int line = 0;
    int column = 0;
    int currentInputIndex = 0;
    char[] input;
    char current;

    public void LexInput(String inputText) {

        line = 0;
        column = 0;
        input = inputText.toCharArray();
        currentInputIndex = 0;
        tokens = new ArrayList<>();

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
                    System.out.print("(|");
                setCurrentToNextIfExists();
            }

            else if(current == ')') {
                tokens.add(new Token(TokenType.RPAREN, ')', line, column));
                    System.out.print(")|");
                setCurrentToNextIfExists();
            }

            else if(current == '{') {
                tokens.add(new Token(TokenType.LBRACE, '{', line, column));
                    System.out.print("{|");
                setCurrentToNextIfExists();
            }

            else if(current == '}') {
                tokens.add(new Token(TokenType.RPAREN, '}', line, column));
                    System.out.print("}|");
                setCurrentToNextIfExists();
            }

            else if(current == ';') {
                handleSemicolon();
            }

            else {
                handleOperator();
            }
        }
        //END OF WHILE
        int x = 1;
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
        String returnVal = "" + current;
        while(peekNextExists() && (Character.isLetter(peekNext()) || Character.isDigit(peekNext()))) {
            setCurrentToNext();
            returnVal += current;
        }

        setCurrentToNextIfExists();
        
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
                System.out.print(returnVal + "|");
            tokens.add(new Token(reservedMap.get(returnVal), returnVal, line, column));
            return;
        }

        //NOTE - FOUND VARIABLE (add to symbol table?)
            System.out.print(returnVal + "|");
    }
    
    private void handleIntegerOrFloat() {
        String returnVal = "" + current;
        boolean isFloat = false;

        // while(peekNextExists() && Character.isDigit(current)) {
        //     returnVal += current;
        //     setCurrentToNext();
        //     if(current == '.')
        //         isFloat = true;
        // } //OLD METHOD

        while(peekNextExists() && (Character.isDigit(peekNext()) || peekNext() == '.')) {
            setCurrentToNext();
            returnVal += current;
            if(current == '.')
            {
                if(!isFloat)
                    isFloat = true;
                else {} //TODO - THROW ERROR
            }
        }
        //By now, current is the char after the number

        if(isFloat) {
            //NOTE - FOUND FLOAT
                System.out.print(returnVal + "|");
            tokens.add(new Token(TokenType.FLOAT, Float.parseFloat(returnVal), line, column));
                
        }
        else {
            //NOTE - FOUND INTEGER
                System.out.print(returnVal + "|");
            tokens.add(new Token(TokenType.INT, Integer.parseInt(returnVal), line, column));
        }
        setCurrentToNextIfExists();
    }

    private void handleString() {
        String returnVal = "";
        setCurrentToNext();

        while(current != '"') {
            returnVal += "" + current;
            if(!peekNextExists()) {
                //TODO - THROW ERROR
            }
            setCurrentToNext();
        }

        //The resulting string should not contain any quotation marks

        //NOTE - FOUND STRING
            System.out.print(returnVal + "|");
        tokens.add(new Token(TokenType.STRING, returnVal, line, column));

            setCurrentToNextIfExists();
    }

    private void handleSemicolon() {
        //NOTE - FOUND SEMICOLON
            System.out.print(";" + "|");
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
                    System.out.print(rval + "|");
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
                System.out.print("" + current + "|");
            tokens.add(new Token(singleOperatorMap.get("" + current), "" + current, line, column));
                
            setCurrentToNextIfExists();
            return;
        }


        //If execution reaches here, we have encountered an invalid character. Throw Error
        //TODO - THROW ERROR
    }
}
