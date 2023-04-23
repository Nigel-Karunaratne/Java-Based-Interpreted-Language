package com.nigel_karunaratne.error_handler;

import com.nigel_karunaratne.JBIL_Main;
import com.nigel_karunaratne.tokens.Token;

public final class ErrorHandler {
    public static void outputWarning(String message) {
        System.out.printf("WARNING | ????, ???? | %s\n", message);
    }

    public static void outputWarning(String message, int line, int col) {
        System.out.printf("WARNING | %4d, %4d | %s\n", line, col, message);
    }

    public static void outputException(String message) {
        System.out.printf("ERROR   | ????, ???? | %s\n", message);
    }

    public static void outputException(String message, int line, int col) {
        System.out.printf("ERROR   | %4d, %4d | %s\n", line, col, message);
    }


    public static void outputFatalException() {

    }

    public static void debugOutput(String message) {
        System.out.printf("DEBUG   | ????, ???? | %s\n", message);
    }

    public static RuntimeError throwRuntimeError(Token currentToken, String errorMessage) {
        ErrorHandler.outputException(errorMessage, currentToken.line, currentToken.column);
        JBIL_Main.hadError = true;
        return new RuntimeError();
    }

    public static void reportLexerError(int line, int col, String errorMessage) {
        //nothing needs to be thrown, just reported and aborted.
        ErrorHandler.outputException(errorMessage, line, col);
        JBIL_Main.hadError = true;
    }

    public static void reportResolverError(Token currentToken, String errorMessage) {
        ErrorHandler.outputException(errorMessage, currentToken.line, currentToken.column);
        JBIL_Main.hadError = true;
    }
}
