package com.nigel_karunaratne;

import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.expressions.Expr;
import com.nigel_karunaratne.lexer.LexerInstance;
import com.nigel_karunaratne.parser.Parser;
import com.nigel_karunaratne.tokens.Token;

//* This is the entry point for the interpreter command line. It is temporary
public class App {
    public static void main(String[] args) throws Exception {
        ErrorHandler.DebugOutput("Main App started.");

        // String inputString = "var variable = 1 + 3 / 4;\nvar stringVar123 = \"Hello World! 123454321;\";   \nvar a = \"oonga\";";
        // String inputString = "var x = 3 + 2 / 4 * 5 % 2;\nvar y = \"Hello World\";\nif(x >= 3) y = \"hello worl\";";
        String inputString = "(2 + 4 / 3) >= 1 == null;";

        System.out.println("Lexing: \n" + inputString + "\n");

        LexerInstance lexer = new LexerInstance();
        lexer.LexInput(inputString);

        Parser parser = new Parser(lexer.getGeneratedTokens());

        Expr expression = parser.parse();

        ErrorHandler.DebugOutput("Main App ended.");
    }
}
